package com.cbaldacin.services;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.cbaldacin.model.Statistic;

/**
 * Service responsible for holding the Statistic result to be sent back as
 * response of HTTP GET /statistics.
 * 
 * It uses the default scope which is singleton.
 * 
 * @author carlos
 *
 */
@Service
public class StatisticService {

	/**
	 * Statistics result
	 */
	private Statistic statistic;

	@PostConstruct
	private void init() {
		statistic = new Statistic();
	}

	/**
	 * Refresh statistic with a new object
	 * 
	 * @param statistic
	 */
	public void update(final Statistic statistic) {
		this.statistic = statistic;
	}

	/**
	 * Sends the current statistic
	 * 
	 * @return the statistic
	 */
	public Statistic getStatistic() {
		return statistic;
	}

}
