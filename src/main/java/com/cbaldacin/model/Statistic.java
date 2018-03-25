package com.cbaldacin.model;

/**
 * Statistics monitor for the last 60 seconds
 * 
 * @author carlos
 *
 */
public class Statistic {

	/**
	 * sum is a double specifying the total sum of transaction value in the last
	 * 60 seconds
	 */
	private double sum;
	/**
	 * avg is a double specifying the average amount of transaction value in the
	 * last 60 seconds
	 */
	private double avg;
	/**
	 * max is a double specifying single highest transaction value in the last
	 * 60 seconds
	 */
	private double max;
	/**
	 * min is a double specifying single lowest transaction value in the last 60
	 * seconds
	 */
	private double min;
	/**
	 * count is a long specifying the total number of transactions happened in
	 * the last 60 seconds
	 */
	private long count;

	public Statistic() {
	}

	public Statistic(final long count, final double sum, final double avg, final double max, final double min) {
		this.count = count;
		this.sum = sum;
		this.avg = avg;
		this.max = max;
		this.min = min;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(final double sum) {
		this.sum = sum;
	}

	public double getAvg() {
		return avg;
	}

	public void setAvg(final double avg) {
		this.avg = avg;
	}

	public double getMax() {
		return max;
	}

	public void setMax(final double max) {
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public void setMin(final double min) {
		this.min = min;
	}

	public long getCount() {
		return count;
	}

	public void setCount(final long count) {
		this.count = count;
	}

}
