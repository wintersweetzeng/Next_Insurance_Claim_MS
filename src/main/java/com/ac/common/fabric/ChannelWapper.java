package com.ac.common.fabric;

import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdkintegration.SampleStore;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ac.common.fabric.hfc.HFCKeyStore;
import com.ac.common.fabric.hfc.HFCUser;
import com.ac.common.fabric.hfc.OrgInfo;

import lombok.Getter;

@Service
public class ChannelWapper {

	@Autowired
	private OrgWapper orgWapper;

	@Getter
	private HFClient client;
	

	@PostConstruct
	private void init() throws Exception {
		
		client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

		for (OrgInfo orgInfo : orgWapper.getAllOrgInfo()) {

			HFCAClient ca = orgInfo.getCaClient();
			ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

			String orgName = orgInfo.getName();
			String mspid = orgInfo.getMspid();

			HFCUser admin = orgInfo.getAdmin();
			
			if (!admin.isEnrolled()) {
				admin.setEnrollment(ca.enroll(admin.getName(), admin.getpa));
				admin.setMPSID(mspid);
			}

			
			
			
			
		}

	}
	

}
