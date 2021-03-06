package com.barclays.indiacp.test;

import static org.testng.Assert.*;

import com.barclays.indiacp.config.AppStartup;
import com.barclays.indiacp.error.APIException;
import com.barclays.indiacp.model.Transaction;
import com.barclays.indiacp.service.ContractService;
import com.barclays.indiacp.service.GethHttpService;
import com.barclays.indiacp.service.TransactionService;
import com.google.common.collect.Lists;
import com.barclays.indiacp.bean.GethConfigBean;
import com.barclays.indiacp.model.TransactionResult;
import com.barclays.indiacp.test.config.TempFileManager;
import com.barclays.indiacp.test.config.TestAppConfig;
import com.barclays.indiacp.util.FileUtils;
import com.barclays.indiacp.util.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;

@ActiveProfiles("test")
@ContextConfiguration(classes = {TestAppConfig.class})
//@Listeners(CleanConsoleListener.class) // uncomment for extra debug help
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public abstract class BaseGethRpcTest extends AbstractTestNGSpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(BaseGethRpcTest.class);

    static {
        System.setProperty("spring.profiles.active", "test");
        System.setProperty("cakeshop.database.vendor", "hsqldb");
    }

	@Autowired
	private ContractService contractService;

	@Autowired
	private TransactionService transactionService;

    @Autowired
    private AppStartup appStartup;

    @Autowired
    protected GethHttpService geth;

    @Value("${geth.datadir}")
    private String ethDataDir;

    @Value("${config.path}")
    private String CONFIG_ROOT;

    @Autowired
    private GethConfigBean gethConfig;


    @Autowired
    @Qualifier("hsql")
    private DataSource embeddedDb;

    public BaseGethRpcTest() {
        super();
    }

    public boolean runGeth() {
        return true;
    }

    @AfterSuite(alwaysRun=true)
    public void stopSolc() throws IOException {
        List<String> args = Lists.newArrayList(
                gethConfig.getNodePath(),
                gethConfig.getSolcPath(),
                "--stop-ipc");

        ProcessBuilder builder = ProcessUtils.createProcessBuilder(gethConfig, args);
        builder.start();
    }

    @AfterSuite(alwaysRun=true)
    public void cleanupTempPaths() {
        try {
            if (CONFIG_ROOT != null) {
                FileUtils.deleteDirectory(new File(CONFIG_ROOT));
            }
            TempFileManager.cleanupTempPaths();
        } catch (IOException e) {
        }
    }

    @BeforeClass
    public void startGeth() throws IOException {
        if (!runGeth()) {
            return;
        }

        assertTrue(appStartup.isHealthy(), "Healthcheck should pass");

        LOG.info("Starting Ethereum at test startup");
        assertTrue(_startGeth());
    }

    private boolean _startGeth() throws IOException {
        gethConfig.setGenesisBlockFilename(FileUtils.getClasspathPath("genesis_block.json").toAbsolutePath().toString());
        gethConfig.setKeystorePath(FileUtils.getClasspathPath("keystore").toAbsolutePath().toString());
        return geth.start();
    }


    /**
     * Stop geth & delete data dir
     */
    @AfterClass(alwaysRun=true)
    public void stopGeth() {
        if (!runGeth()) {
            return;
        }
        LOG.info("Stopping Ethereum at test teardown");
        _stopGeth();
    }

    private void _stopGeth() {
        geth.stop();
        try {
            FileUtils.deleteDirectory(new File(ethDataDir));
        } catch (IOException e) {
            logger.warn(e);
        }
        String db = System.getProperty("cakeshop.database.vendor");
        if (db.equalsIgnoreCase("hsqldb")) {
            ((EmbeddedDatabase) embeddedDb).shutdown();
        }
    }

    /**
     * Read the given test resource file
     *
     * @param path
     * @return
     * @throws IOException
     */
    protected String readTestFile(String path) throws IOException {
    	return FileUtils.readClasspathFile(path);
    }

    /**
     * Deploy SimpleStorage sample to the chain and return its address
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    protected String createContract() throws IOException, InterruptedException {
    	String code = readTestFile("contracts/simplestorage.sol");
    	return createContract(code, null);
    }

    /**
     * Deploy the given contract code to the chain
     *
     * @param code
     * @return
     * @throws APIException
     * @throws InterruptedException
     */
    protected String createContract(String code, Object[] args) throws APIException, InterruptedException {
        TransactionResult result = contractService.create(null, code, ContractService.CodeType.solidity, args, null, null, null);
    	assertNotNull(result);
    	assertNotNull(result.getId());
    	assertTrue(!result.getId().isEmpty());

    	// make sure mining is enabled
    	Map<String, Object> res = geth.executeGethCall("miner_start", new Object[]{ });

    	Transaction tx = transactionService.waitForTx(result, 50, TimeUnit.MILLISECONDS);
    	return tx.getContractAddress();
    }

}
