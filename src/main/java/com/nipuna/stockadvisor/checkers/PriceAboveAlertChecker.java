package com.nipuna.stockadvisor.checkers;

import yahoofinance.quotes.stock.StockQuote;

public class PriceAboveAlertChecker extends BaseAlertChecker {

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		return quote.getDayHigh().doubleValue() > Double.valueOf(getParamValue());
	}

	public String desc() {
		return getStock().getSymbol() + " crossed "+getParamValue() +" now  @ "  + getStock().getQuote().getPrice().doubleValue();
	}

	@Override
	public String shortDesc() {
		return "52 WK low " + getStock().getQuote().getPrice().doubleValue();
	}
}
