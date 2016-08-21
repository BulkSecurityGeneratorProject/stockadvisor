package com.nipuna.stockadvisor.checkers;

import java.math.BigDecimal;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;

public class PricePctDecreseChecker extends BaseAlertChecker {

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		BigDecimal change = quote.getChangeInPercent();
		return change.doubleValue() < 0 && Math.abs(change.doubleValue()) >= Double.valueOf(getParamValue());
	}

	public String desc() {
		return getStock().getSymbol() + " DOWN by " + Math.abs(getStock().getQuote().getChangeInPercent().doubleValue())
				+ "%";
	}
}
