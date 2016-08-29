package com.nipuna.stockadvisor.checkers;

import com.nipuna.stockadvisor.util.NumerToWordUtil;

import yahoofinance.quotes.stock.StockQuote;

public class AvgVolumeIncreaseAlertChecker extends BaseAlertChecker {

	public boolean check() {
		StockQuote quote = getStock().getQuote();
		return quote.getVolume() > quote.getAvgVolume();
	}

	public String desc() {
		StockQuote quote = getStock().getQuote();
		long increase = quote.getVolume() - quote.getAvgVolume();
		long pct = (increase * 100) / quote.getVolume();
		String vol = NumerToWordUtil.format(quote.getVolume().longValue());
		String avg = NumerToWordUtil.format(quote.getAvgVolume().longValue());
		return getStock().getSymbol() + " crossed AVG Volume(3m) by " + pct + "%. vol=" + vol + ", avg vol(3m)=" + avg;
	}

	@Override
	public String shortDesc() {
		StockQuote quote = getStock().getQuote();
		long increase = quote.getVolume() - quote.getAvgVolume();
		long pct = (increase * 100) / quote.getVolume();
		return "VOL +" + pct + "%";
	}

}
