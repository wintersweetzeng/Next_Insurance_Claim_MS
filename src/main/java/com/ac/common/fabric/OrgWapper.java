package com.ac.common.fabric;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hyperledger.fabric.sdk.helper.Utils;
import org.hyperledger.fabric.sdkintegration.SampleUser;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.ac.common.fabric.config.HospitalInfoConfig;
import com.ac.common.fabric.config.InsuranceInfoConfig;
import com.ac.common.fabric.config.OrgCommonConfig;
import com.ac.common.fabric.hfc.HFCKeyStore;
import com.ac.common.fabric.hfc.HFCUser;
import com.ac.common.fabric.hfc.OrgInfo;
import com.google.common.collect.Lists;

import lombok.Getter;

@Service
public class OrgWapper {

	@Autowired
	private HospitalInfoConfig hospitalInfoConfig;

	@Autowired
	private InsuranceInfoConfig insuranceInfoConfig;

	@Getter
	private OrgInfo hospitalOrgInfo;

	@Getter
	private OrgInfo insuranceOrgInfo;

	@Autowired
	private HFCKeyStore hfcKeyStore;

	@PostConstruct
	private void init() throws Exception {
		hospitalOrgInfo = this.initOrgInfo(hospitalInfoConfig);
		insuranceOrgInfo = this.initOrgInfo(insuranceInfoConfig);
	}

	public List<OrgInfo> getAllOrgInfo() {
		return Lists.newArrayList(hospitalOrgInfo, insuranceOrgInfo);
	}

	private OrgInfo initOrgInfo(OrgCommonConfig config) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

		hospitalOrgInfo = new OrgInfo();

		String[] ps = config.getPeerLocations().split("[ \t]*,[ \t]*");
		if (ArrayUtils.isEmpty(ps)) {
			throw new IllegalArgumentException("Peers is empty!");
		}

		for (String peer : ps) {
			String[] nl = peer.split("[ \t]*@[ \t]*");
			hospitalOrgInfo.addPeerLocation(nl[0], grpcTLSify(nl[1]));
		}

		hospitalOrgInfo.setDomainName(config.getDomname());

		ps = config.getOrdererLocations().split("[ \t]*,[ \t]*");
		if (ArrayUtils.isEmpty(ps)) {
			throw new IllegalArgumentException("Orderer is empty!");
		}

		for (String orderer : ps) {
			String[] nl = orderer.split("[ \t]*@[ \t]*");
			hospitalOrgInfo.addOrdererLocation(nl[0], grpcTLSify(nl[1]));
		}

		ps = config.getEventhubLocations().split("[ \t]*,[ \t]*");
		for (String event : ps) {
			String[] nl = event.split("[ \t]*@[ \t]*");
			hospitalOrgInfo.addEventHubLocation(nl[0], grpcTLSify(nl[1]));
		}

		hospitalOrgInfo.setCaLocation(httpTLSify(config.getCaLocation()));

		hospitalOrgInfo.setCaClient(
				HFCAClient.createNewInstance(hospitalOrgInfo.getCaLocation(), hospitalOrgInfo.getCaProperties()));
		
		
		// admin user
		HFCUser admin = hfcKeyStore.getMember(config.getAdmin().getName(), config.getName());
		admin.setEnrollmentSecret(config.getAdmin().getPassword());
		hospitalOrgInfo.setAdmin(admin);

		// peerAdmin user
		ResourceLoader loader = new DefaultResourceLoader();
		File privateKey = null;
		File cert = null;
		if (StringUtils.equalsIgnoreCase("insurance", config.getId())) {
			privateKey = loader
					.getResource(
							"classpath:keyStore/insurance/admin/f1022dfda62d66248343d3af08e7bb94270cda5162eae5ad587d36196054265f_sk")
					.getFile();

			cert = loader.getResource("classpath:keyStore/insurance/admin/Admin@org1.example.com-cert.pem").getFile();
		} else {
			privateKey = loader
					.getResource(
							"classpath:keyStore/insurance/hospital/11d9df1af05581b30566223d6d6180e7a8c4276a374fe7ced03e0d4569ea61c7_sk")
					.getFile();

			cert = loader.getResource("classpath:keyStore/hospital/admin/Admin@org2.example.com-cert.pem").getFile();
		}

		HFCUser peerOrgAdmin = hfcKeyStore.getMember(config.getName() + "Admin", config.getName(), config.getMspid(),
				privateKey, cert);
		hospitalOrgInfo.setPeerAdmin(peerOrgAdmin);

		return hospitalOrgInfo;

	}

	private String grpcTLSify(String location) {

		location = StringUtils.trim(location);
		Exception e = Utils.checkGrpcUrl(location);
		if (e != null) {
			throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
		}

		return location;
	}

	private String httpTLSify(String location) {
		return StringUtils.trim(location);
	}

}
