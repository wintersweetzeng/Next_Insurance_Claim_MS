package com.ac.common.fabric;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelWapper {

	@Autowired
	private OrgWapper orgWapper;

	@PostConstruct
	private void init() {

	}

}
