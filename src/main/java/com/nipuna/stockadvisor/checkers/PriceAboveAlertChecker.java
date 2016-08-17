package com.nipuna.stockadvisor.checkers;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;

public class PriceAboveAlertChecker extends BaseAlertChecker {

	public PriceAboveAlertChecker(Stock stock) {
		super(stock);
	}

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		return quote.getDayHigh().doubleValue() > Double.valueOf(getParamValue());
	}

	public String desc() {
		return getStock().getSymbol() + " crossed "+getParamValue() +" now  @ "  + getStock().getQuote().getPrice().doubleValue();
	}

}
