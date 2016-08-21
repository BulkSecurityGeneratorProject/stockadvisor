package com.nipuna.stockadvisor.checkers;

import yahoofinance.quotes.stock.StockQuote;

public class AvgVolumeIncreaseAlertChecker extends BaseAlertChecker {

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		long avgVolume = quote.getVolume();
		long volume = quote.getVolume();
		return volume >= avgVolume;
	}

	public String desc() {
		return getStock().getSymbol() + " crossed AVG Volume " + getStock().getQuote().getVolume();
	}

}
