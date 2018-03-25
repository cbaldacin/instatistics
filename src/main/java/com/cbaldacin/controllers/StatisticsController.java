package com.cbaldacin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cbaldacin.Config;
import com.cbaldacin.model.Statistic;
import com.cbaldacin.services.StatisticService;

/**
 * Controller responsible for handling statistics requests
 * 
 * @author carlos
 *
 */
@RestController
@RequestMapping(Config.STATISTICS_URL)
public class StatisticsController {

	@Autowired
	private StatisticService statisticService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Statistic getStatistic() {

		return statisticService.getStatistic();

	}
}
