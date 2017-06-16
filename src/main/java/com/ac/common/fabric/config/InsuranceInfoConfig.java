package com.ac.common.fabric.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application-insurance.properties")
@ConfigurationProperties(prefix = "insurance", ignoreUnknownFields = false)
public class InsuranceInfoConfig extends OrgCommonConfig {

}
