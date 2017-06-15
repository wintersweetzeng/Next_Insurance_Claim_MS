package com.ac.common.fabric;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@PropertySource(value = "classpath:application-insurance.properties")
@Getter
public class InsuranceInfoConfig {

	@Value("${insurance.name}")
	private String name;

	@Value("${insurance.domname}")
	private String domname;

	@Value("${insurance.ca_location}")
	private String ca_location;

	@Value("${insurance.peer_locations}")
	private String peerLocations;

	@Value("${insurance.orderer_locations}")
	private String ordererLocations;

	@Value("${insurance.eventhub_locations}")
	private String eventhubLocations;
}
