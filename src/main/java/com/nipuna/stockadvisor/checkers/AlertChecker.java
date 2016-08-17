package com.nipuna.stockadvisor.checkers;

import com.nipuna.stockadvisor.domain.enumeration.ParamType;

import yahoofinance.Stock;

public interface AlertChecker {

	boolean check();

	String desc();

	void setStock(Stock stock);
	
	void setParam(ParamType type, String value);
}
