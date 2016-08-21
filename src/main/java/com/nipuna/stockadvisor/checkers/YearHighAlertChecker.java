package com.nipuna.stockadvisor.checkers;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;

public class YearHighAlertChecker extends BaseAlertChecker {

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		boolean dayHigh = quote.getDayHigh().doubleValue() > quote.getYearHigh().doubleValue();
		boolean priceHigh = quote.getPrice().doubleValue() >= quote.getYearHigh().doubleValue();
		return dayHigh || priceHigh;
	}

	public String desc() {
		return getStock().getSymbol() + " hit 52 WEEK high " + getStock().getQuote().getPrice().doubleValue();
	}

}
