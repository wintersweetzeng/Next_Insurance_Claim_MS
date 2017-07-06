package com.ac.common.fabric;

import com.ac.common.constant.SmartContractConstant;
import com.ac.common.fabric.model.ChainCodeResultModel;
import com.ac.common.fabric.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by zhenchao.bi on 6/27/2017.
 */
@Service
public class SmartContractWapper {

	public static ChaincodeID HOSPITAL_CHAINCODE_ID = null;

	public static ChaincodeID INDURANCE_CHAINCODE_ID = null;

	public static ChaincodeID CUSTOMER_CHAINCODE_ID = null;

	public static List<Peer> peers = null;

	@Autowired
	private ChannelWapper channel;

	private ResourceLoader loader = new DefaultResourceLoader();

	@PostConstruct
	private void init() throws Exception {

		HOSPITAL_CHAINCODE_ID = ChaincodeID.newBuilder().setName(SmartContractConstant.Hospital.CHAINCODE_NAME)
				.setVersion(SmartContractConstant.Hospital.CHAINCODE_VERSION)
				.setPath(SmartContractConstant.Hospital.CHAINCODE_PATH).build();

		INDURANCE_CHAINCODE_ID = ChaincodeID.newBuilder().setName(SmartContractConstant.Insurance.CHAINCODE_NAME)
				.setVersion(SmartContractConstant.Insurance.CHAINCODE_VERSION)
				.setPath(SmartContractConstant.Insurance.CHAINCODE_PATH).build();

		CUSTOMER_CHAINCODE_ID = ChaincodeID.newBuilder().setName(SmartContractConstant.Customer.CHAINCODE_NAME)
				.setVersion(SmartContractConstant.Customer.CHAINCODE_VERSION)
				.setPath(SmartContractConstant.Customer.CHAINCODE_PATH).build();

		peers = channel.getAllPeers().stream().filter(peer -> StringUtils.contains(peer.getName(), "org1.example.com")
				&& StringUtils.contains(peer.getName(), "peer0")).collect(Collectors.toList());
	}

	public ChainCodeResultModel installHospitalSC() throws Exception {

		try {
			// smartContract\hospital
			 File scFile = loader.getResource("classpath:/smartContract/hospital").getFile();
			//File scFile = IOUtils.getFileFromClasspath("/smartContract/hospital");
			return channel.installChaincode(HOSPITAL_CHAINCODE_ID, scFile, peers);
		} catch (Exception ex) {
			// log
			throw ex;
		}
	}

	public CompletableFuture<TransactionEvent> instantHospitalSC() throws Exception {

		try {
			// smartContract\hospital
			File scFile = IOUtils.getFileFromClasspath("/endorsementPolicy/hospital/chaincodeendorsementpolicy.yaml");
			//File scFile = loader.getResource("classpath:/endorsementPolicy/hospital/chaincodeendorsementpolicy.yaml")
			//		.getFile();

			return channel.instantChaincode(HOSPITAL_CHAINCODE_ID, scFile, peers, "init", new String[] { "test" });

		} catch (Exception ex) {
			// log
			throw ex;
		}
	}

	public ChainCodeResultModel installInsuranceSC() {

		try {
			// smartContract\hospital
			//File scFile = loader.getResource("classpath:/smartContract/insurance").getFile();
			File scFile = IOUtils.getFileFromClasspath("/smartContract/insurance");
			return channel.installChaincode(INDURANCE_CHAINCODE_ID, scFile, peers);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public ChainCodeResultModel installCustomerSC() {

		try {
			// smartContract\hospital
			//File scFile = loader.getResource("classpath:/smartContract/customer").getFile();
			File scFile = IOUtils.getFileFromClasspath("/smartContract/customer");
			return channel.installChaincode(INDURANCE_CHAINCODE_ID, scFile, peers);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

}
