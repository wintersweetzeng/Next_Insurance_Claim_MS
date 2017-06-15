package com.ac.common.fabric;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelWapper {

	@Autowired
	private InsuranceInfoConfig insuranceInfoConfig;

	@Autowired
	private HospitalInfoConfig hospitalInfoConfig;


	@PostConstruct
	private void init() {

	}
	
	

}
