package com.nipuna.stockadvisor.checkers;

import yahoofinance.quotes.stock.StockQuote;

public class YearLowAlertChecker extends BaseAlertChecker {

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		boolean dayLow = quote.getDayLow().doubleValue() <= quote.getYearLow().doubleValue();
		boolean priceLow = quote.getPrice().doubleValue() <= quote.getYearLow().doubleValue();
		return dayLow || priceLow;
	}

	public String desc() {
		return getStock().getSymbol() + " hit 52 WEEK low " + getStock().getQuote().getPrice().doubleValue();
	}

}
