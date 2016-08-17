package com.nipuna.stockadvisor.checkers;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;

public class YearLowAlertChecker extends BaseAlertChecker {

	public YearLowAlertChecker(Stock stock) {
		super(stock);
	}

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		return quote.getPrice().doubleValue() <= quote.getYearLow().doubleValue();
	}

	public String desc() {
		return getStock().getSymbol() + " hit 52 WEEK low " + getStock().getQuote().getPrice().doubleValue();
	}

}
