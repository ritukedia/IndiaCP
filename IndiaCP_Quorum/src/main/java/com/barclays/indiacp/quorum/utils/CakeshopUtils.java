package com.barclays.indiacp.quorum.utils;

import com.barclays.indiacp.quorum.contract.code.SolidityContractCode;
import com.barclays.indiacp.quorum.contract.code.SolidityContractCodeFactory;
import com.jpmorgan.cakeshop.client.ClientManager;
import com.jpmorgan.cakeshop.client.api.ContractApi;
import com.jpmorgan.cakeshop.client.model.Contract;
import com.jpmorgan.cakeshop.client.model.TransactionResult;
import com.jpmorgan.cakeshop.client.model.req.ContractCompileCommand;
import com.jpmorgan.cakeshop.client.model.req.ContractCreateCommand;
import com.jpmorgan.cakeshop.client.model.res.APIData;
import com.jpmorgan.cakeshop.client.model.res.APIResponse;
import java.util.List;

/**
 * Created by ritukedia on 28/12/16.
 */
public class CakeshopUtils {

    public static ContractApi getCakeshopContractApi() {
        // setup cakeshop manager
        final ClientManager manager = ClientManager.create("http://localhost:8080/cakeshop");
        ContractApi contractApi = manager.getClient(ContractApi.class);
        return contractApi;
    }

    //Takes solidity string, returns unmined contract object
    public static Contract compileSolidity(String contractCode) {
        ContractCompileCommand c = new ContractCompileCommand().code(contractCode).codeType(Contract.CodeTypeEnum.SOLIDITY).optimize(true);
        APIResponse<List<APIData<Contract>>, Contract> res = getCakeshopContractApi().compile(c);
        APIData<Contract> result = res.getApiData().get(0);
        return result.getAttributes();
    }

    public static String createContract(String contractName, Object... contractModel) {
        APIResponse<APIData<TransactionResult>, TransactionResult> res = getCakeshopContractApi().create(getContractCreateCommand(contractName, contractModel));
        //TODO: fetch contract address from the APIResponse
        return "contract address";
    }

    public static ContractCreateCommand getContractCreateCommand(String contractName, Object... contractModel) {
        SolidityContractCode contractCode = SolidityContractCodeFactory.getInstance(contractName);
        ContractCreateCommand contractCreateCommand = new ContractCreateCommand();
        contractCreateCommand.setCode(contractCode.getContractCode());
        contractCreateCommand.setBinary(contractCode.getContractBinary());
        contractCreateCommand.setCodeType(contractCode.getCodeType());
        contractCreateCommand.setFrom("0x2e219248f44546d966808cdd20cb6c36df6efa82");
        contractCreateCommand.setArgs(contractCode.getConstructorArgs(contractModel));
        return contractCreateCommand;
    }
}
