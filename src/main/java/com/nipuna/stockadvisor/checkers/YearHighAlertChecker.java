package com.nipuna.stockadvisor.checkers;

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

	@Override
	public String shortDesc() {
		return "52 WK high " + getStock().getQuote().getPrice().doubleValue();
	}
}
