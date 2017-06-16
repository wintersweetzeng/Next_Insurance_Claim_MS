package com.ac.common.fabric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ac.common.fabric.config.HospitalInfoConfig;

@Service
public class HospitalOrgWapper {

	@Autowired
	private HospitalInfoConfig hospitalInfoConfig;

}
