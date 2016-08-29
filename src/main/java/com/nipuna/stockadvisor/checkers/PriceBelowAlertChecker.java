package com.nipuna.stockadvisor.checkers;

import yahoofinance.quotes.stock.StockQuote;

/**
 * @deprecated
 * 
 * @author vijay
 *
 */
public class PriceBelowAlertChecker extends BaseAlertChecker {

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		return quote.getDayLow().doubleValue() < Double.valueOf(getParamValue());
	}

	public String desc() {
		return getStock().getSymbol() + " below " + getParamValue() + " now  @ "
				+ getStock().getQuote().getPrice().doubleValue();
	}

	@Override
	public String shortDesc() {
		// TODO Auto-generated method stub
		return null;
	}

}
