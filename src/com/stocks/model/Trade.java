package com.stocks.model;

import java.util.Date;

import com.stocks.utils.Type;

public class Trade {

	private Date timeStamp = null;
	private Stock stock = null;
	private Type tradeType;
	private int quantityOfShares = 0;
	private double tradePrice = 0.0;
	
	public Date getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public int getQuantityOfShares() {
		return quantityOfShares;
	}
	
	public void setQuantityOfShares(int quantityOfShares) {
		this.quantityOfShares = quantityOfShares;
	}
	
	public void setTradeType(Type tradeType) {
		this.tradeType = tradeType;
	}
	
	public Type getTradeType() {
		return tradeType;
	}

	public double getTradePrice() {
		return tradePrice;
	}
	
	public void setTradePrice(double tradePrice) {
		this.tradePrice = tradePrice;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

}

