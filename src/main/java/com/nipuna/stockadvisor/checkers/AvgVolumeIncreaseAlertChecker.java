package com.nipuna.stockadvisor.checkers;

import com.nipuna.stockadvisor.domain.enumeration.ParamType;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockQuote;

public class AvgVolumeIncreaseAlertChecker implements AlertChecker {

	private Stock stock;
	
	public AvgVolumeIncreaseAlertChecker(Stock stock) {
		this.stock = stock;
	}

	public AvgVolumeIncreaseAlertChecker() {
		// TODO Auto-generated constructor stub
	}

	public boolean check() {
		StockQuote quote = stock.getQuote();
		long avgVolume = quote.getVolume();
		long volume = quote.getVolume();
		return volume >= avgVolume;
	}

	public String desc() {
		return stock.getSymbol() + " crossed AVG Volume " + stock.getQuote().getVolume();
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	@Override
	public void setParam(ParamType type, String value) {
		// TODO Auto-generated method stub
		
	}
}
