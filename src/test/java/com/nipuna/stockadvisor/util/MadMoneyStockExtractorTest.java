package com.nipuna.stockadvisor.util;

import java.io.IOException;

import org.junit.Test;

public class MadMoneyStockExtractorTest {

	@Test
	public void extract() throws IOException{
		MadMoneyStockExtractor extractor = new MadMoneyStockExtractor();
		extractor.process();
	}
}
