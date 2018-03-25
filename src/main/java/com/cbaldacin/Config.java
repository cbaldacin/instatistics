package com.cbaldacin;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Everything which is related to configuration must be placed here
 * 
 * @author carlos
 *
 */
@Configuration
@EnableScheduling
public class Config {

	public static final String STATISTICS_URL = "/statistics";

	public static final String TRANSACTIONS_URL = "/transactions";

}