package com.nipuna.stockadvisor.util;

import java.io.IOException;

import org.junit.Test;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class YahooFinanceTest {

	@Test
	public void callYahoo() throws IOException{
		Stock stock = YahooFinance.get("LYG");
		
		StringBuffer sb = new StringBuffer();
		sb.append("Quote:\n");
		sb.append("\t" +stock.getQuote()+"\n\n");
		
		sb.append("Year Range:\n");
		sb.append("\t" + stock.getQuote().getYearLow() +"-"+ stock.getQuote().getYearHigh() +"\n\n");
		
		sb.append("Book Value per share:\n");
		sb.append("\t" +stock.getStats().getBookValuePerShare().doubleValue()+"\n\n");
		
		sb.append("1 Year price Target:\n");
		sb.append("\t" +stock.getStats().getOneYearTargetPrice().doubleValue()+"\n\n");
		
		sb.append("Market Cap:\n");
		sb.append("\t" +NumerToWordUtil.format(stock.getStats().getMarketCap().longValue())+"\n\n");
		
		sb.append("Volume:\n");
		sb.append("\t" +NumerToWordUtil.format(stock.getQuote().getVolume())+"\n\n");
		
		sb.append("Avg Volume(3m):\n");
		sb.append("\t" +NumerToWordUtil.format(stock.getQuote().getAvgVolume())+"\n\n");
		
		sb.append("50 DMA:\n");
		sb.append("\t" + stock.getQuote().getPriceAvg50() +"\n\n");
		
		sb.append("200 DMA:\n");
		sb.append("\t" + stock.getQuote().getPriceAvg200() +"\n\n");
		
		System.out.println(sb.toString());
	}
}
