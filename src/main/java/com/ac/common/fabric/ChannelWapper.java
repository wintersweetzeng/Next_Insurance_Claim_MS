package com.ac.common.fabric;

import javax.annotation.PostConstruct;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelWapper {

	@Autowired
	private OrgWapper orgWapper;

	private HFClient client;

	@PostConstruct
	private void init() throws Exception {

		client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
	}

	private Channel constructChannel() {
		return null;
	}

}
