package com.nipuna.stockadvisor.checkers;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;

public class PriceBelowAlertChecker extends BaseAlertChecker {

	public PriceBelowAlertChecker(Stock stock) {
		super(stock);
	}

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		return quote.getDayLow().doubleValue() < Double.valueOf(getParamValue());
	}

	public String desc() {
		return getStock().getSymbol() + " below "+getParamValue() +" now  @ "  + getStock().getQuote().getPrice().doubleValue();
	}

}
