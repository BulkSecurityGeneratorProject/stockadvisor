package com.nipuna.stockadvisor.checkers;

import com.nipuna.stockadvisor.domain.enumeration.ParamType;

import yahoofinance.Stock;

public abstract class BaseAlertChecker implements AlertChecker {

	private Stock stock;
	private String paramValue;
	private ParamType paramType;

	public BaseAlertChecker(Stock stock) {
		this.stock = stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	@Override
	public void setParam(ParamType type, String value) {
		this.paramValue = value;
		this.paramType = type;
	}

	protected Stock getStock() {
		return stock;
	}

	protected String getParamValue() {
		return paramValue;
	}

	protected ParamType getParamType() {
		return paramType;
	}

}
