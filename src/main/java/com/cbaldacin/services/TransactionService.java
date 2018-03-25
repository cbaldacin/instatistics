package com.cbaldacin.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cbaldacin.exceptions.ExpiredTransactionException;
import com.cbaldacin.model.Statistic;
import com.cbaldacin.model.Transaction;

/**
 * Service responsible for transactions related rules implementation.
 * 
 * It uses the default scope which is singleton.
 * 
 * @author carlos
 *
 */
@Service
public class TransactionService {

	@Value("${statistic.timeRange}")
	private int statisticTimeRange;
	

	/**
	 * List of transactions for the last 60 seconds
	 */
	private List<Transaction> transactions;

	@Autowired
	private StatisticService statisticService;

	@PostConstruct
	private void init() {
		transactions = Collections.synchronizedList(new ArrayList<Transaction>());
	}

	/**
	 * Add a new transaction to the list.
	 * 
	 * @param transaction
	 */
	public void addTransaction(final Transaction transaction) {

		// if the transaction coming is too old for statistics purposes, throw
		// an exception which will send HTTP204 back to client
		if (transaction.getTimestamp() < (System.currentTimeMillis() - statisticTimeRange)) {
			throw new ExpiredTransactionException();
		}

		transactions.add(transaction);
	}

	/**
	 * Evict expired transactions regularly on a fixedRate.
	 * 
	 */
	@Scheduled(fixedRateString = "${expiredTransactions.removalRate.in.milliseconds}")
	public void removeExpiredTransactions() {

		synchronized (transactions) {

			if (!transactions.isEmpty()) {
				transactions.removeIf(tx -> tx.getTimestamp() < (System.currentTimeMillis() - statisticTimeRange));

				updateStatistic();
			}
		}
	}
	
	/**
	 * Clear all transactions, useful for testing 
	 */
	public void reset() {
		transactions.clear();
	}

	/**
	 * Calculate a new statistic and update it in statisticService
	 */
	private void updateStatistic() {

		final DoubleSummaryStatistics dss = transactions.stream()
				.collect(Collectors.summarizingDouble(Transaction::getAmount));

		final Statistic newStatistic = new Statistic(dss.getCount(), dss.getSum(), dss.getAverage(), dss.getMax(),
				dss.getMin());

		statisticService.update(newStatistic);
	}
	
	

}
