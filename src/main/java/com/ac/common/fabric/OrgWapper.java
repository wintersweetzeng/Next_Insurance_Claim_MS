package com.ac.common.fabric;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
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

	private OrgInfo initOrgInfo(OrgCommonConfig config) throws Exception {

		hospitalOrgInfo = new OrgInfo();
		
		hospitalOrgInfo.setId(config.getId());

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

		HFCAClient ca = HFCAClient.createNewInstance(hospitalOrgInfo.getCaLocation(),
				hospitalOrgInfo.getCaProperties());
		ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
		hospitalOrgInfo.setCaClient(ca);

		// admin user
		HFCUser admin = hfcKeyStore.getMember(config.getAdmin().getName(), config.getName());
		admin.setEnrollmentSecret(config.getAdmin().getPassword());
		if (!admin.isEnrolled()) {
			admin.setEnrollment(ca.enroll(admin.getName(), admin.getEnrollmentSecret()));
			admin.setMPSID(config.getMspid());
		}
		hospitalOrgInfo.setAdmin(admin);

		// peerAdmin user
		ResourceLoader loader = new DefaultResourceLoader();
		File privateKey = null;
		File cert = null;
		if (StringUtils.equalsIgnoreCase("insurance", config.getId())) {
			privateKey = loader.getResource(config.getAdmin().getPrivateKey()).getFile();
			cert = loader.getResource(config.getAdmin().getPublicKey()).getFile();
		} else {
			privateKey = loader.getResource(config.getAdmin().getPrivateKey()).getFile();
			cert = loader.getResource(config.getAdmin().getPublicKey()).getFile();
		}

		HFCUser peerOrgAdmin = hfcKeyStore.getMember(config.getName() + "Admin", config.getName(), config.getMspid(),
				privateKey, cert);
		hospitalOrgInfo.setPeerAdmin(peerOrgAdmin);

		return hospitalOrgInfo;
	}

	private String grpcTLSify(String location) {
		location = StringUtils.trim(location);
//		Exception e = SDKUtil.checkGrpcUrl(location);
//		if (e != null) {
//			throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
//		}
		return location;
	}

	private String httpTLSify(String location) {
		return StringUtils.trim(location);
	}

}
