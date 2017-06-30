package com.ac.common.fabric;

import com.ac.common.constant.SmartContractConstant;
import com.ac.common.fabric.model.ChainCodeResultModel;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zhenchao.bi on 6/27/2017.
 */
@Service
public class SmartContractWapper {

    @Autowired
    private ChannelWapper channel;

    private ResourceLoader loader = new DefaultResourceLoader();


    public ChainCodeResultModel installHospitalSC() throws Exception {

        try {
            //smartContract\hospital
            File scFile = loader.getResource("classpath:/smartContract/hospital").getFile();

            List<Peer> peers = channel.getAllPeers().stream().filter(peer -> StringUtils.contains(peer.getName(), "org1.example.com")).collect(Collectors.toList());

            return channel.installChaincode(scFile, SmartContractConstant.Hospital.CHAINCODE_NAME, SmartContractConstant.Hospital.CHAINCODE_VERSION,
                    SmartContractConstant.Hospital.CHAINCODE_PATH, peers);
        } catch (Exception ex) {
            //log

            throw ex;
        }
    }

    public ChainCodeResultModel instantHospitalSC() throws Exception {

        try {
            //smartContract\hospital
            File scFile = loader.getResource("classpath:/endorsementPolicy/hospital/chaincodeendorsementpolicy.yaml").getFile();

            List<Peer> peers = channel.getAllPeers().stream().filter(peer -> StringUtils.contains(peer.getName(), "org1.example.com")).collect(Collectors.toList());

            //String chaincodeName, String chaincodeVersion, String path, File endorsementPolicyFile,
            // Collection<Peer> peers, String invokeMethod, String[] invokeArgs
            return channel.instantChaincode(SmartContractConstant.Hospital.CHAINCODE_NAME, SmartContractConstant.Hospital.CHAINCODE_VERSION,
                    SmartContractConstant.Hospital.CHAINCODE_PATH, scFile, peers, "init", new String[]{ "test"});
        } catch (Exception ex) {
            //log

            throw ex;
        }
    }

    public ChainCodeResultModel installInsuranceSC() {

        try {
            //smartContract\hospital
            File scFile = loader.getResource("classpath:/smartContract/insurance").getFile();

            List<Peer> peers = channel.getAllPeers().stream().filter(peer -> StringUtils.contains(peer.getName(), "org1.example.com")).collect(Collectors.toList());

            return channel.installChaincode(scFile, SmartContractConstant.Insurance.CHAINCODE_NAME, SmartContractConstant.Insurance.CHAINCODE_VERSION,
                    SmartContractConstant.Insurance.CHAINCODE_PATH, channel.getAllPeers());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public ChainCodeResultModel installCustomerSC() {

        try {
            //smartContract\hospital
            File scFile = loader.getResource("classpath:/smartContract/customer").getFile();

            List<Peer> peers = channel.getAllPeers().stream().filter(peer -> StringUtils.contains(peer.getName(), "org1.example.com")).collect(Collectors.toList());

            return channel.installChaincode(scFile, SmartContractConstant.Customer.CHAINCODE_NAME, SmartContractConstant.Customer.CHAINCODE_VERSION,
                    SmartContractConstant.Customer.CHAINCODE_PATH, channel.getAllPeers());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
