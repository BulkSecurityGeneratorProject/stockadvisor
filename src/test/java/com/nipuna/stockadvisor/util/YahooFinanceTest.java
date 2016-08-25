package com.nipuna.stockadvisor.util;

import java.io.IOException;
import java.util.Calendar;

import org.junit.Test;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

public class YahooFinanceTest {

	@Test
	public void callYahoo() throws IOException{
		Stock stock = YahooFinance.get("LYG");
		System.out.println(getStockInfo(stock));
	}
	
	private String getStockInfo(Stock stock) {

		StringBuffer sb = new StringBuffer();

		sb.append("Name:\n");
		sb.append("\t" + stock.getName() + "\n\n");
		
		sb.append("Price:\n");
		sb.append("\t" + stock.getQuote().getPrice() + "\n\n");
		
		sb.append("% Change in Price:\n");
		sb.append("\t" + stock.getQuote().getChangeInPercent() + "%\n\n");

		sb.append("Day Range:\n");
		sb.append("\t" + stock.getQuote().getDayLow() + "-" + stock.getQuote().getDayHigh() + "\n\n");

		sb.append("Year Range:\n");
		sb.append("\t" + stock.getQuote().getYearLow() + "-" + stock.getQuote().getYearHigh() + "\n\n");

		sb.append("Quote:\n");
		sb.append("\t" + stock.getQuote() + "\n\n");

		sb.append("Book Value per share:\n");
		sb.append("\t" + stock.getStats().getBookValuePerShare().doubleValue() + "\n\n");

		sb.append("1 Year price Target:\n");
		sb.append("\t" + stock.getStats().getOneYearTargetPrice().doubleValue() + "\n\n");

		sb.append("Market Cap:\n");
		sb.append("\t" + NumerToWordUtil.format(stock.getStats().getMarketCap().longValue()) + "\n\n");

		sb.append("Volume:\n");
		sb.append("\t" + NumerToWordUtil.format(stock.getQuote().getVolume()) + "\n\n");

		sb.append("Avg Volume(3m):\n");
		sb.append("\t" + NumerToWordUtil.format(stock.getQuote().getAvgVolume()) + "\n\n");

		sb.append("50 DMA:\n");
		sb.append("\t" + stock.getQuote().getPriceAvg50() + "\n\n");

		sb.append("200 DMA:\n");
		sb.append("\t" + stock.getQuote().getPriceAvg200() + "\n\n");

		return sb.toString();
	}
	
	@Test
	public void apiTEst() throws IOException{
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.YEAR, -5); // from 5 years ago
		 
		Stock google = YahooFinance.get("GOOG", from, to, Interval.WEEKLY);
		System.out.println(getStockInfo(google));
		Stock tesla = YahooFinance.get("TSLA", true);
		System.out.println(tesla.getHistory());
		
	}
}
