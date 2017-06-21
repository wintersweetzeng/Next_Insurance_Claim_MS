package com.ac.common.fabric;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.assertj.core.util.Lists;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.ac.common.fabric.hfc.OrgInfo;
import com.ac.common.fabric.model.ChainCodeResultModel;

@Service
public class ChannelWapper {

	@Autowired
	private OrgWapper orgWapper;

	private HFClient client;

	/**
	 * For hospital and insurance
	 */
	private Channel channel;

	private ResourceLoader loader = new DefaultResourceLoader();

	@PostConstruct
	private void init() throws Exception {
		client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

		channel = this.constructChannel("foo");

	}

	@PreDestroy
	private void destory() {
		if (channel != null) {
			channel.shutdown(true);
		}
	}

	public ChainCodeResultModel installChaincode(String chaincodeSourceLocation, String chaincodeName,
			String chaincodeVersion, String path, Collection<Peer> peers)
			throws InvalidArgumentException, ProposalException {

		client.setUserContext(orgWapper.getInsuranceOrgInfo().getPeerAdmin());

		InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();

		ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion)
				.setPath(path).build();
		installProposalRequest.setChaincodeID(chaincodeID);

		installProposalRequest.setChaincodeSourceLocation(new File(chaincodeSourceLocation));

		installProposalRequest.setChaincodeVersion(chaincodeVersion);

		int numInstallProposal = 0;
		numInstallProposal = numInstallProposal + peers.size();

		Collection<ProposalResponse> responses = client.sendInstallProposal(installProposalRequest, peers);

		Collection<ProposalResponse> successful = Lists.newArrayList();
		Collection<ProposalResponse> failed = Lists.newArrayList();
		for (ProposalResponse response : responses) {
			if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
				successful.add(response);
			} else {
				failed.add(response);
			}
		}

		return new ChainCodeResultModel(successful, failed);
		// SDKUtils.getProposalConsistencySets(responses);
	}

	public ChainCodeResultModel instantChaincode(ChaincodeID chaincodeID, String endorsementPolicyFile,
			Collection<Peer> peers, String invokeMethod, String[] invokeArgs)
			throws InvalidArgumentException, ChaincodeEndorsementPolicyParseException, IOException, ProposalException {

		InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();

		instantiateProposalRequest.setProposalWaitTime(300000);
		instantiateProposalRequest.setChaincodeID(chaincodeID);
		instantiateProposalRequest.setFcn(invokeMethod);
		instantiateProposalRequest.setArgs(invokeArgs);
		Map<String, byte[]> tm = new HashMap<>();
		tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
		tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
		instantiateProposalRequest.setTransientMap(tm);

		/*
		 * policy OR(Org1MSP.member, Org2MSP.member) meaning 1 signature from
		 * someone in either Org1 or Org2 See README.md Chaincode endorsement
		 * policies section for more details.
		 */
		ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
		chaincodeEndorsementPolicy.fromYamlFile(new File(endorsementPolicyFile));
		instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);

		Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest, peers);

		Collection<ProposalResponse> successful = Lists.newArrayList();
		Collection<ProposalResponse> failed = Lists.newArrayList();
		for (ProposalResponse response : responses) {
			if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
				successful.add(response);
			} else {
				failed.add(response);
			}
		}

		return new ChainCodeResultModel(successful, failed);

	}

	public ChainCodeResultModel transationProposal(ChaincodeID chaincodeID, Collection<Peer> peers, String invokeMethod,
			String[] invokeArgs) throws InvalidArgumentException, ProposalException {

		client.setUserContext(orgWapper.getInsuranceOrgInfo().getPeerAdmin());

		TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
		transactionProposalRequest.setChaincodeID(chaincodeID);
		transactionProposalRequest.setFcn(invokeMethod);
		transactionProposalRequest.setArgs(invokeArgs);
		transactionProposalRequest.setProposalWaitTime(300000);

		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
		tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
		tm2.put("result", ":)".getBytes(UTF_8));
		transactionProposalRequest.setTransientMap(tm2);

		Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest,
				peers);

		Collection<ProposalResponse> successful = Lists.newArrayList();
		Collection<ProposalResponse> failed = Lists.newArrayList();
		for (ProposalResponse response : transactionPropResp) {
			if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
				successful.add(response);
			} else {
				failed.add(response);
			}
		}

		return new ChainCodeResultModel(successful, failed);
	}

	public CompletableFuture<TransactionEvent> transationSubmit(Collection<ProposalResponse> successful) {
		return channel.sendTransaction(successful);
	}

	public ChainCodeResultModel query(ChaincodeID chaincodeID, Collection<Peer> peers, String invokeMethod,
			String[] invokeArgs) throws InvalidArgumentException, ProposalException {

		QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
		queryByChaincodeRequest.setArgs(invokeArgs);
		queryByChaincodeRequest.setFcn(invokeMethod);
		queryByChaincodeRequest.setChaincodeID(chaincodeID);

		Map<String, byte[]> tm = new HashMap<>();
		tm.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
		tm.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
		queryByChaincodeRequest.setTransientMap(tm);

		Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, peers);

		Collection<ProposalResponse> successful = Lists.newArrayList();
		Collection<ProposalResponse> failed = Lists.newArrayList();
		for (ProposalResponse proposalResponse : queryProposals) {
			if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
				successful.add(proposalResponse);
			} else {
				failed.add(proposalResponse);
			}
		}

		return new ChainCodeResultModel(successful, failed);

	}

	private Channel constructChannel(String channelName)
			throws InvalidArgumentException, IOException, TransactionException {

		OrgInfo insuranceOrg = orgWapper.getInsuranceOrgInfo();

		Collection<Orderer> orderers = Lists.newArrayList();
		for (String orderName : insuranceOrg.getOrdererNames()) {

			Properties ordererProperties = this.getOrdererProperties(orderName);
			ordererProperties.put("grpc.NettyChannelBuilderOption.enableKeepAlive",
					new Object[] { true, 1L, TimeUnit.SECONDS, 1L, TimeUnit.SECONDS });

			orderers.add(client.newOrderer(orderName, insuranceOrg.getOrdererLocation(orderName), ordererProperties));
		}

		// Just pick the first orderer in the list to create the channel.
		Orderer anOrderer = orderers.iterator().next();
		orderers.remove(anOrderer);

		ChannelConfiguration channelConfiguration = new ChannelConfiguration(
				loader.getResource("classpath:/keyStore/insurance/channel/foo.tx").getFile());

		// Only peer Admin org
		client.setUserContext(insuranceOrg.getPeerAdmin());

		// client.getChannel(name)
		Channel newChannel = null;
		try {
			newChannel = client.newChannel(channelName, anOrderer, channelConfiguration,
					client.getChannelConfigurationSignature(channelConfiguration, insuranceOrg.getPeerAdmin()));
		} catch (Exception ex) {
			ex.printStackTrace();
			newChannel = client.newChannel(channelName);
			newChannel.addOrderer(anOrderer);
		}

		// join insureance peer to channel
		this.joinPeerToChannel(insuranceOrg, newChannel);

		// join hospital peer to channel
		this.joinPeerToChannel(orgWapper.getHospitalOrgInfo(), newChannel);

		for (String eventHubName : insuranceOrg.getEventHubNames()) {
			EventHub eventHub = client.newEventHub(eventHubName, insuranceOrg.getEventHubLocation(eventHubName),
					this.getPeerProperties(insuranceOrg.getId(), "peer0"));
			newChannel.addEventHub(eventHub);
		}

		newChannel.initialize();

		return newChannel;
	}

	private void joinPeerToChannel(OrgInfo insuranceOrg, Channel newChannel)
			throws IOException, InvalidArgumentException {

		for (String peerName : insuranceOrg.getPeerNames()) {

			String peerLocation = insuranceOrg.getPeerLocation(peerName);

			Properties peerProperties = this.getPeerProperties(insuranceOrg.getId(), "peer0");
			if (peerProperties == null) {
				peerProperties = new Properties();
			}
			// Example of setting specific options on grpc's NettyChannelBuilder
			peerProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);

			Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
			insuranceOrg.addPeer(peer);

			try {
				newChannel.joinPeer(peer);
			} catch (Exception ex) {
				ex.printStackTrace();
				newChannel.addPeer(peer);
			}

		}

	}

	private Properties getPeerProperties(String orgName, String peerName) throws IOException {

		File cert = loader.getResource(String.format("classpath:/tls/peer/%s/%s/server.crt", orgName, peerName))
				.getFile();
		if (!cert.exists()) {
			throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s",
					peerName, cert.getAbsolutePath()));
		}

		Properties ret = new Properties();
		ret.setProperty("pemFile", cert.getAbsolutePath());
		ret.setProperty("hostnameOverride", peerName);
		ret.setProperty("sslProvider", "openSSL");
		ret.setProperty("negotiationType", "TLS");

		return ret;
	}

	private Properties getOrdererProperties(String name) throws IOException {

		File cert = loader.getResource("classpath:/tls/orderer/server.crt").getFile();
		if (!cert.exists()) {
			throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s", name,
					cert.getAbsolutePath()));
		}

		Properties ret = new Properties();
		ret.setProperty("pemFile", cert.getAbsolutePath());
		ret.setProperty("hostnameOverride", name);
		ret.setProperty("sslProvider", "openSSL");
		ret.setProperty("negotiationType", "TLS");

		return ret;
	}

}