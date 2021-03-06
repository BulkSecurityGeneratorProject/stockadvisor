package com.nipuna.stockadvisor.checkers;

import java.math.BigDecimal;

import com.nipuna.stockadvisor.domain.enumeration.AlertPriority;

import yahoofinance.quotes.stock.StockQuote;

public class PricePctIncreaseChecker extends BaseAlertChecker {

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		BigDecimal change = quote.getChangeInPercent();
		return change.doubleValue() > 0 && change.doubleValue() >= Double.valueOf(getParamValue());
	}

	public String desc() {
		return getStock().getSymbol() + " UP  by " + getStock().getQuote().getChangeInPercent().doubleValue() + "%";
	}

	@Override
	public String shortDesc() {
		return "Price +" + getStock().getQuote().getChangeInPercent().doubleValue() + "%";
	}
	
	@Override
	public AlertPriority getPriority() {
		return AlertPriority.HIGH;
	}

}
