package com.ac.common.fabric.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application-hospital.properties")
@ConfigurationProperties(prefix = "hospital", ignoreUnknownFields = false)
public class HospitalInfoConfig extends OrgCommonConfig {

}
