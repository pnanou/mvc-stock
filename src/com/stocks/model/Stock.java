package com.stocks.model;

import com.stocks.utils.Type;


public class Stock {

	private String stockSymbol = null;
	private Type stockType = Type.COMMON;
	private double lastDividend = 0.0;
	private double fixedDividend = 0.0;
	private double parValue = 0.0;
	private double currentPrice = 0.0; 
	
	/**
	 * 
	 * @return
	 */
	public String getStockSymbol() {
		return stockSymbol;
	}

	/**
	 * 
	 * @param stockSymbol
	 */
	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

	/**
	 * 
	 * @return
	 */
	public Type getStockType() {
		return stockType;
	}

	/**
	 * 
	 * @param stockType
	 */
	public void setStockType(Type type) {
		this.stockType = type;
	}

	/**
	 * 
	 * @return
	 */
	public double getLastDividend() {
		return lastDividend;
	}

	/**
	 * 
	 * @param lastDividend
	 */
	public void setLastDividend(double lastDividend) {
		this.lastDividend = lastDividend;
	}

	/**
	 * 
	 * @return
	 */
	public double getFixedDividend() {
		return fixedDividend;
	}

	/**
	 * 
	 * @param fixedDividend
	 */
	public void setFixedDividend(double fixedDividend) {
		this.fixedDividend = fixedDividend;
	}

	/**
	 * 
	 * @return
	 */
	public double getParValue() {
		return parValue;
	}

	/**
	 * 
	 * @param parValue
	 */
	public void setParValue(double parValue) {
		this.parValue = parValue;
	}

	/**
	 * 
	 * @return
	 */
	public double getDividendYield() {
		double dividendYield = -1.0;
		if(currentPrice > 0.0){
			if( stockType==Type.COMMON)
				dividendYield = lastDividend / currentPrice;
			else
				dividendYield = (fixedDividend * parValue ) / currentPrice;
		}
		return dividendYield;
	}


	/**
	 * 
	 * @return
	 */
	public double getPeRatio() {
		double peRatio = -1.0;
		
		if(currentPrice > 0.0)
			peRatio = currentPrice / getDividendYield();	
		
		return peRatio;
	}

	/**
	 * 
	 * @return
	 */
	public double getCurrentPrice() {
		return currentPrice;
	}

	/**
	 * 
	 * @param currentPrice
	 */
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}	
}
