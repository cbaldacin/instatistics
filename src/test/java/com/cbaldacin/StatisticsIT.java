package com.cbaldacin;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.cbaldacin.model.Statistic;
import com.cbaldacin.model.Transaction;
import com.cbaldacin.services.StatisticService;
import com.cbaldacin.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StatisticsIT {

	@LocalServerPort
	private int port;

	private String urlBase;

	@Autowired
	protected ObjectMapper mapper;
	
	@Autowired
	private StatisticService statisticService;
	
	@Autowired
	private TransactionService transactionService;

	@Before
	public void setUp() throws Exception {
		if (this.urlBase == null) {
			this.urlBase = "http://localhost:" + port;
		}
		//reset statistic for each test
		transactionService.reset();
		statisticService.update(new Statistic());
		
	}

	@Autowired
	private MockMvc mvc;

	@Test
	public void shouldComputeTransactionsWithinLastMinute() throws Exception {

		// Create 10 transaction with random timestamp within last minute
		final DoubleSummaryStatistics dss = createTransactionsWithinLastMinute(10);

		// Wait for scheduled job execution
		Thread.sleep(1000);

		final MvcResult result = mvc
				.perform(MockMvcRequestBuilders.get(urlBase + Config.STATISTICS_URL)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();

		final Statistic statistic = mapper.readValue(result.getResponse().getContentAsString(), Statistic.class);

		assertThat(statistic.getSum(), equalTo(dss.getSum()));
		assertThat(statistic.getAvg(), equalTo(dss.getAverage()));
		assertThat(statistic.getCount(), equalTo(dss.getCount()));
		assertThat(statistic.getMax(), equalTo(dss.getMax()));
		assertThat(statistic.getMin(), equalTo(dss.getMin()));

	}

	@Test
	public void shouldNotComputeOldTransactions() throws Exception {

		// Create 5 transaction with random timestamp within last minute
		final DoubleSummaryStatistics dss = createTransactionsWithinLastMinute(5);

		// Create 5 transactions out of last minute
		createTransactionsOutOfLastMinute(5);

		// Wait for scheduled job execution
		Thread.sleep(1000);

		final MvcResult result = mvc
				.perform(MockMvcRequestBuilders.get(urlBase + Config.STATISTICS_URL)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();

		final Statistic statistic = mapper.readValue(result.getResponse().getContentAsString(), Statistic.class);

		assertThat(statistic.getSum(), equalTo(dss.getSum()));
		assertThat(statistic.getAvg(), equalTo(dss.getAverage()));
		assertThat(statistic.getCount(), equalTo(dss.getCount()));
		assertThat(statistic.getMax(), equalTo(dss.getMax()));
		assertThat(statistic.getMin(), equalTo(dss.getMin()));
	}

	@Test
	public void shouldNotComputeExpiredTransactions() throws Exception {

		// Create 10 transaction with random timestamp within last minute
		final DoubleSummaryStatistics dss = createTransactionsWithinLastMinute(10);

		// create transaction to be expired in 2 seconds
		final double amount = 12.5;
		createTransaction(amount, System.currentTimeMillis() - 58000);

		// Wait for scheduled job execution
		Thread.sleep(1000);

		MvcResult result = mvc
				.perform(MockMvcRequestBuilders.get(urlBase + Config.STATISTICS_URL)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();

		Statistic statistic = mapper.readValue(result.getResponse().getContentAsString(), Statistic.class);

		assertThat(statistic.getSum(), equalTo(dss.getSum() + amount));
		assertThat(statistic.getCount(), equalTo(dss.getCount() + 1));

		// Wait for transaction to be expired
		Thread.sleep(2000);

		result = mvc
				.perform(MockMvcRequestBuilders.get(urlBase + Config.STATISTICS_URL)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();

		statistic = mapper.readValue(result.getResponse().getContentAsString(), Statistic.class);

		assertThat(statistic.getSum(), equalTo(dss.getSum()));
		assertThat(statistic.getAvg(), equalTo(dss.getAverage()));
		assertThat(statistic.getCount(), equalTo(dss.getCount()));
		assertThat(statistic.getMax(), equalTo(dss.getMax()));
		assertThat(statistic.getMin(), equalTo(dss.getMin()));

	}

	@Test
	public void shouldNotAcceptAmountZero() throws Exception {

		final Transaction tx = new Transaction();
		tx.setAmount(0D);
		tx.setTimestamp(System.currentTimeMillis());
		final String json = mapper.writeValueAsString(tx);
		mvc.perform(MockMvcRequestBuilders.post(urlBase + Config.TRANSACTIONS_URL).content(json)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andDo(MockMvcResultHandlers.print());

	}

	@Test
	public void shouldNotAcceptTimestampZero() throws Exception {

		final Transaction tx = new Transaction();
		tx.setAmount(10D);
		tx.setTimestamp(0);
		final String json = mapper.writeValueAsString(tx);
		mvc.perform(MockMvcRequestBuilders.post(urlBase + Config.TRANSACTIONS_URL).content(json)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andDo(MockMvcResultHandlers.print());

	}

	private void createTransaction(final double amount, final long timestamp) throws Exception {

		final Transaction tx = new Transaction();
		tx.setAmount(amount);
		tx.setTimestamp(timestamp);
		final String json = mapper.writeValueAsString(tx);
		mvc.perform(MockMvcRequestBuilders.post(urlBase + Config.TRANSACTIONS_URL).content(json)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andDo(MockMvcResultHandlers.print());

	}

	private void createTransactionsOutOfLastMinute(final int n) throws Exception {

		for (int i = 0; i < n; i++) {
			final Transaction tx = getTransactionOutOfLastMinute();
			final String json = mapper.writeValueAsString(tx);
			mvc.perform(MockMvcRequestBuilders.post(urlBase + Config.TRANSACTIONS_URL).content(json)
					.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent())
					.andDo(MockMvcResultHandlers.print());

		}

	}

	private DoubleSummaryStatistics createTransactionsWithinLastMinute(final int n) throws Exception {
		final List<Transaction> txs = new ArrayList<Transaction>();
		for (int i = 0; i < n; i++) {
			final Transaction tx = getTransactionInLastMinute();
			final String json = mapper.writeValueAsString(tx);
			mvc.perform(MockMvcRequestBuilders.post(urlBase + Config.TRANSACTIONS_URL).content(json)
					.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
					.andDo(MockMvcResultHandlers.print());

			txs.add(tx);
		}

		return txs.stream().collect(Collectors.summarizingDouble(Transaction::getAmount));

	}

	private Transaction getTransactionInLastMinute() {
		final Transaction tx = new Transaction();
		tx.setAmount(getRandom(1, 100));
		tx.setTimestamp(System.currentTimeMillis() - getRandom(0, 55000));

		return tx;
	}

	private Transaction getTransactionOutOfLastMinute() {
		final Transaction tx = new Transaction();
		tx.setAmount(getRandom(1, 100));
		tx.setTimestamp(System.currentTimeMillis() - getRandom(61000, 10000000));

		return tx;
	}

	private int getRandom(final int min, final int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}

}
