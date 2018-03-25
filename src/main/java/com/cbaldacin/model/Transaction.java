package com.cbaldacin.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

/**
 * Object representation of a Transaction which will be used for statistics
 * calculation purposes
 * 
 * @author carlos
 *
 */
public class Transaction {

	/**
	 * transaction amount
	 */
	@Positive
	private double amount;

	/**
	 * transaction time in epoch in millis in UTC time zone (this is not current
	 * timestamp)
	 * 
	 */
	@Min(1)
	private long timestamp;

	public double getAmount() {
		return amount;
	}

	public void setAmount(final double amount) {
		this.amount = amount;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

}
