package com.nipuna.stockadvisor.checkers;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;

public class YearHighAlertChecker extends BaseAlertChecker {

	public YearHighAlertChecker(Stock stock) {
		super(stock);
	}

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		return quote.getPrice().doubleValue() > quote.getYearLow().doubleValue();
	}

	public String desc() {
		return getStock().getSymbol() + " hit 52 WEEK high " + getStock().getQuote().getPrice().doubleValue();
	}

}
