package com.nipuna.stockadvisor.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MadMoneyStockExtractor {
	private static final String URL = "http://madmoney.thestreet.com/screener/index.cfm";

	public void process() throws IOException {

		Document doc = Jsoup.connect(URL).data("symbol", "").data("airdate", "2016-08-05").data("called", "%")
				.data("industry", "%").data("sector", "%").data("segment", "%").data("pricelow", "0")
				.data("pricehigh", "1000").data("sortby", "symbol")
				// and other hidden fields which are being passed in post
				// request.
				.userAgent("Mozilla").post();
		StringBuilder sb = new StringBuilder();

		// Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
		String callDate = doc.select("#leftPanel p").text();
		System.out.println(callDate.split(":")[1].trim());
		Elements rows = doc.select("#stockTable tr");
		for (Element row : rows) {
			Elements tdList = row.select("td");
			if (tdList.size() == 6) {
				String stockText = tdList.get(0).text();
				String[] stockTextTokens = stockText.split("\\(");
				String name = stockTextTokens[0].trim();
				String symbol = stockTextTokens[1].replaceFirst("\\)", "").trim();
				String date = tdList.get(1).text();
				String segment = tdList.get(2).select("img").attr("alt");
				String call = tdList.get(3).select("img").attr("alt");
				String price = tdList.get(4).text();

				sb.append(symbol).append(" ").append(name).append(" ").append(date).append(" ").append(segment)
						.append(" ").append(call).append(" ").append(price);

				System.out.println(sb.toString());
				sb.setLength(0);
				sb.append("\n");
			}
		}
	}

}