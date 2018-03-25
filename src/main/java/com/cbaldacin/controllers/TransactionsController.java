package com.cbaldacin.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cbaldacin.Config;
import com.cbaldacin.model.Transaction;
import com.cbaldacin.services.TransactionService;

/**
 * Controller responsible for handling transaction requests
 * 
 * @author carlos
 *
 */
@RestController
@RequestMapping(Config.TRANSACTIONS_URL)
public class TransactionsController {

	@Autowired
	private TransactionService transactionService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void registerTransaction(@Valid @RequestBody Transaction transaction) {

		transactionService.addTransaction(transaction);

	}
}
