package com.barclays.indiacp.service.impl;

import com.barclays.indiacp.bean.GethConfigBean;
import com.barclays.indiacp.dao.TransactionDAO;
import com.barclays.indiacp.error.APIException;
import com.barclays.indiacp.error.CompilerException;
import com.barclays.indiacp.service.task.ContractRegistrationTask;
import com.barclays.indiacp.util.ProcessUtils;
import com.barclays.indiacp.util.StreamGobbler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.barclays.indiacp.model.Contract;
import com.barclays.indiacp.model.ContractABI;
import com.barclays.indiacp.model.Transaction;
import com.barclays.indiacp.model.TransactionRequest;
import com.barclays.indiacp.model.TransactionResult;
import com.barclays.indiacp.service.ContractRegistryService;
import com.barclays.indiacp.service.ContractService;
import com.barclays.indiacp.service.GethHttpService;
import com.barclays.indiacp.service.TransactionService;
import com.barclays.indiacp.service.WalletService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ContractServiceImpl implements ContractService {

    static final Logger LOG = LoggerFactory.getLogger(ContractServiceImpl.class);

    @Value("${contract.poll.delay.millis}")
    Long pollDelayMillis;

    @Autowired
    private GethConfigBean gethConfig;

    @Autowired
    private GethHttpService geth;

    @Autowired
    private ContractRegistryService contractRegistry;

    @Autowired(required = false)
    private TransactionDAO transactionDAO;

    @Autowired
    private TransactionService txnService;

    @Autowired
    private WalletService walletService;

    private String defaultFromAddress;

    @Autowired
    @Qualifier("asyncExecutor")
    private TaskExecutor executor;

    @Autowired
    private ApplicationContext appContext;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SuppressWarnings("unchecked")
    public List<Contract> compile(String code, CodeType codeType, Boolean optimize) throws APIException {

        if (codeType != CodeType.solidity) {
            throw new APIException("Only 'solidity' source is currently supported");
        }

        if (optimize == null) {
            optimize = true; // default to true
        }

        List<Contract> contracts = new ArrayList<>();
        long createdDate = System.currentTimeMillis() / 1000;

        Map<String, Object> res = null;
        try {
            List<String> args = Lists.newArrayList(
                    gethConfig.getNodePath(),
                    gethConfig.getSolcPath(),
                    "--ipc");

            ProcessBuilder builder = ProcessUtils.createProcessBuilder(gethConfig, args);
            Process proc = builder.start();

            StreamGobbler stdout = StreamGobbler.create(proc.getInputStream());
            StreamGobbler stderr = StreamGobbler.create(proc.getErrorStream());

            proc.getOutputStream().write(code.getBytes());;
            proc.getOutputStream().close();

            proc.waitFor();

            if (proc.exitValue() != 0) {
                throw new APIException("Failed to compile contract (solc exited with code " + proc.exitValue() + ")\n" + stderr.getString());
            }

            res = objectMapper.readValue(stdout.getString(), Map.class);

        } catch (IOException | InterruptedException e) {
            LOG.error("REASON FOR CONTRATC FAILURE " + e.getMessage());
            throw new APIException("Failed to compile contract", e);
        }

        if (res.containsKey("errors") && res.get("errors") instanceof List) {
            throw new CompilerException((List<String>) res.get("errors"));
        }

        // res is a hash of contract name -> compiled result map
        for (Entry<String, Object> contractRes : res.entrySet()) {
            Contract contract = new Contract();
            contract.setName(contractRes.getKey());
            contract.setCreatedDate(createdDate);
            contract.setCode(code);
            contract.setCodeType(codeType);

            Map<String, Object> compiled = (Map<String, Object>) contractRes.getValue();

            contract.setBinary((String) compiled.get("bin"));
            contract.setABI((String) compiled.get("abi"));
            contract.setGasEstimates((Map<String, Object>) compiled.get("gas"));
            contract.setFunctionHashes((Map<String, String>) compiled.get("hashes"));
            contract.setSolidityInterface((String) compiled.get("interface"));

            contracts.add(contract);
        }

        return contracts;
    }

    @Override
    public TransactionResult create(String from, String code, CodeType codeType, Object[] args, String binary,
                                    String privateFrom, List<String> privateFor) throws APIException {

        List<Contract> contracts = compile(code, codeType, true); // always deploy optimized contracts

        Contract contract = null;
        if (binary != null && binary.length() > 0) {
            // look for binary in compiled output, comparing first 64 chars of binary
            String bin = binary.trim().substring(0, 63);
            for (Contract c : contracts) {
                if (c.getBinary().trim().startsWith(bin)) {
                    contract = c;
                    break;
                }
            }

            if (contract == null) {
                throw new APIException("Binary does not match given code");
            }

        } else {
            contract = contracts.get(0);
        }

        contract.setOwner(from);

        // handle constructor args -- data is the EVM code that is to be deployed. Contract constructor argumemnts are encoded in.
        String data = contract.getBinary();
        if (args != null && args.length > 0) {
            ContractABI abi = ContractABI.fromJson(contract.getABI());
            ContractABI.Constructor constructor = abi.getConstructor();
            if (constructor == null) {
                throw new APIException("Unable to locate constructor method in ABI");
            }
            data = data + Hex.toHexString(constructor.encode(args)); //adding the arguments for this instantiation
        }

        Map<String, Object> contractArgs = new HashMap<>();
        contractArgs.put("from", getAddress(from));
        contractArgs.put("data", data);
        contractArgs.put("gas", TransactionRequest.DEFAULT_GAS);

        // add quorum args
        if (StringUtils.isNotBlank(privateFrom)) {
            contractArgs.put("privateFrom", privateFrom);
        }
        if (privateFor != null && privateFor.size() > 0) {
            contractArgs.put("privateFor",  privateFor);
        }

        Map<String, Object> contractRes = geth.executeGethCall("eth_sendTransaction", new Object[]{contractArgs});

        TransactionResult tr = new TransactionResult();
        tr.setId((String) contractRes.get("_result"));

        // defer contract registration
        executor.execute(appContext.getBean(ContractRegistrationTask.class, contract, tr));

        return tr;
    }

    @Override
    public TransactionResult delete() throws APIException {
        throw new APIException("Not yet implemented"); // TODO
    }

    @Cacheable(value = "contracts", unless = "#result == null")
    @Override
    public Contract get(String address) throws APIException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Contract cache miss for: " + address);
        }

        Map<String, Object> contractRes = geth.executeGethCall("eth_getCode", new Object[]{address, "latest"});

        String bin = (String) contractRes.get("_result");
        if (bin.contentEquals("0x")) {
            throw new APIException("Contract does not exist at " + address);
        }

        Contract contract = contractRegistry.getById(address);
        if (contract == null) {
            // not [yet] in registry. only binary code will be returned (assuming it exists)
            contract = new Contract();
            contract.setAddress(address);
        }

        contract.setBinary(bin);

        return contract;
    }

    @Override
    public List<Contract> list() throws APIException {
        return contractRegistry.list();
    }

    @Override
    public TransactionResult migrate() throws APIException {
        throw new APIException("Not yet implemented"); // TODO
    }

    @Override
    public Object[] read(String id, String from, String method, Object[] args, Object blockNumber) throws APIException {
        return read(id, lookupABI(id), from, method, args, blockNumber);
    }

    @Override
    public Object[] read(String id, ContractABI abi, String from, String method, Object[] args, Object blockNumber) throws APIException {
        TransactionRequest req = new TransactionRequest(getAddress(from), id, abi, method, args, true);

        if (blockNumber != null) {
            if (!(blockNumber instanceof Number || blockNumber instanceof String)) {
                throw new APIException("Invalid value for blockNumber: must be long or string");
            }
            req.setBlockNumber(blockNumber);
        }

        return read(req);
    }

    @Override
    public Object[] read(TransactionRequest request) throws APIException {
        request.setFromAddress(getAddress(request.getFromAddress())); // make sure we have a non-null from address

        Map<String, Object> readRes = geth.executeGethCall("eth_call", request.toGethArgs());
        String res = (String) readRes.get("_result");
        if (StringUtils.isNotBlank(res) && res.length() == 2 && res.contentEquals("0x")) {
            throw new APIException("eth_call failed (returned 0 bytes)");
        }

        Object[] decodedResults = request.getFunction().decodeHexResult(res).toArray();

        return decodedResults;
    }

    @Override
    public TransactionResult transact(String id, String from, String method, Object[] args) throws APIException {
        return transact(id, lookupABI(id), from, method, args);
    }

    @Override
    public TransactionResult transact(String id, ContractABI abi, String from, String method, Object[] args) throws APIException {
        TransactionRequest req = new TransactionRequest(getAddress(from), id, abi, method, args, false);
        return transact(req);
    }

    @Override
    public TransactionResult transact(TransactionRequest request) throws APIException {
        request.setFromAddress(getAddress(request.getFromAddress())); // make sure we have a non-null from address
        Map<String, Object> readRes = geth.executeGethCall("eth_sendTransaction", request.toGethArgs());
        return new TransactionResult((String) readRes.get("_result"));
    }

    @Override
    public List<Transaction> listTransactions(String contractId) throws APIException {

        Contract contract = get(contractId);
        ContractABI abi = ContractABI.fromJson(contract.getABI());

        List<Transaction> txns = transactionDAO.listForContractId(contractId);

        for (Transaction tx : txns) {
            txnService.loadPrivatePayload(tx);
            tx.decodeContractInput(abi);
        }

        return txns;
    }

    private String getAddress(String from) throws APIException {
        if (StringUtils.isNotBlank(from)) {
            return from;
        }
        if (defaultFromAddress == null) {
            defaultFromAddress = walletService.list().get(0).getAddress();
        }
        return defaultFromAddress;
    }

    private ContractABI lookupABI(String id) throws APIException {
        Contract contract = contractRegistry.getById(id);
        if (contract != null) {
            return contract.getContractAbi();
        }
        return null;
    }

}
