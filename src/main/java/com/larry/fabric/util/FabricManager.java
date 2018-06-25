package com.larry.fabric.util;

import com.larry.fabric.ChaincodeManager;
import com.larry.fabric.FabricConfig;
import com.larry.fabric.bean.Chaincode;
import com.larry.fabric.bean.Orderers;
import com.larry.fabric.bean.Peers;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@Slf4j
public class FabricManager {

    private static Logger log = Logger.getLogger(FabricManager.class);
    private ChaincodeManager manager;

    private static FabricManager instance = null;

    public static FabricManager obtain()
            throws CryptoException, InvalidArgumentException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, TransactionException, IOException {
        if (null == instance) {
            synchronized (FabricManager.class) {
                if (null == instance) {
                    instance = new FabricManager();
                }
            }
        }
        return instance;
    }

    private FabricManager()
            throws CryptoException, InvalidArgumentException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, TransactionException, IOException {
        manager = new ChaincodeManager(getConfig());
    }

    /**
     * 获取节点服务器管理器
     *
     * @return 节点服务器管理器
     */
    public ChaincodeManager getManager() {
        return manager;
    }

    /**
     * 根据节点作用类型获取节点服务器配置
     *
     * @param //type 服务器作用类型（1、执行；2、查询）
     * @return 节点服务器配置
     */
    private FabricConfig getConfig() {
        FabricConfig config = new FabricConfig();
        config.setOrderers(getOrderers());
        config.setPeers(getPeers());
        config.setChaincode(getChaincode("mychannel", "mycc",
                "github.com/hyperledger/fabric/examples/chaincode/go/chaincode_example02", "1.0"));
        config.setChannelArtifactsPath(getChannleArtifactsPath());
        config.setCryptoConfigPath(getCryptoConfigPath());
        return config;
    }

    private Orderers getOrderers() {
        Orderers orderer = new Orderers();
        orderer.setOrdererDomainName("example.com");
        orderer.addOrderer("orderer.example.com", "grpc://47.98.143.199:7050");
        //orderer.addOrderer("orderer1.example.com", "grpc://127.0.0.1:8050");
//        orderer.addOrderer("orderer2.example.com", "grpc://x.x.x.xxx:7050");
        return orderer;
    }

    /**
     * 获取节点服务器集
     *
     * @return 节点服务器集
     */
    private Peers getPeers() {
        Peers peers = new Peers();
        peers.setOrgName("Org1");
        peers.setOrgMSPID("Org1MSP");
        peers.setOrgDomainName("org1.example.com");
        peers.addPeer("peer0.org1.example.com", "peer0.org1.example.com",
                "grpc://47.98.143.199:7051", "grpc://47.98.143.199:7053", "http://47.98.143.199:7054");
        return peers;
    }

    /**
     * 获取智能合约
     *
     * @param channelName      频道名称
     * @param chaincodeName    智能合约名称
     * @param chaincodePath    智能合约路径
     * @param chaincodeVersion 智能合约版本
     * @return 智能合约
     */
    private Chaincode getChaincode(String channelName, String chaincodeName, String chaincodePath, String chaincodeVersion) {
        Chaincode chaincode = new Chaincode();
        chaincode.setChannelName(channelName);
        chaincode.setChaincodeName(chaincodeName);
        chaincode.setChaincodePath(chaincodePath);
        chaincode.setChaincodeVersion(chaincodeVersion);
        chaincode.setInvokeWatiTime(100000);
        chaincode.setDeployWatiTime(120000);
        return chaincode;
    }

    /**
     * 获取channel-artifacts配置路径
     *
     * @return /WEB-INF/classes/fabric/channel-artifacts/
     */
    private String getChannleArtifactsPath() {
        String directorys = FabricManager.class.getClassLoader().getResource("fabric").getFile();
        log.debug("directorys = " + directorys);
        File directory = new File(directorys);
        log.debug("directory = " + directory.getPath());

        return directory.getPath() + "/channel-artifacts/";
    }

    /**
     * 获取crypto-config配置路径
     *
     * @return /WEB-INF/classes/fabric/crypto-config/
     */
    private String getCryptoConfigPath() {
        String directorys = FabricManager.class.getClassLoader().getResource("fabric").getFile();
        log.debug("directorys = " + directorys);
        File directory = new File(directorys);
        log.debug("directory = " + directory.getPath());

        return directory.getPath() + "/crypto-config/";
    }

    public static void main(String[] args) {
        try {
            ChaincodeManager manager = FabricManager.obtain().getManager();

             String[] str = {"a"};
             Map<String, String> query = manager.query("query", str);
             System.out.println(query);

        //  String[] str = {"a", "b", "20"};
        //  Map<String, String> result = manager.invoke("invoke", str);
        //  System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void query() {
        String[] str = {"a"};
        try {
            ChaincodeManager manager = FabricManager.obtain().getManager();
            Map<String, String> query = manager.query("query", str);
            System.out.println(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}