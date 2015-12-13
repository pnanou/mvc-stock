package com.stocks.view;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.stocks.model.Stock;
import com.stocks.model.Trade;

public class StockView {

	private Logger logger = Logger.getLogger(StockView.class);
	
	private HashMap<String, Stock> stockMap = null;
	private ArrayList<Trade> tradeList = null;

	public StockView(){
		tradeList = new ArrayList<Trade>();
		stockMap = new HashMap<String, Stock>();
	}

	public HashMap<String, Stock> getStocks() {
		return stockMap;
	}

	public void setStocks(HashMap<String, Stock> stocks) {
		this.stockMap = stocks;
	}

	public ArrayList<Trade> getTrades() {
		return tradeList;
	}

	public boolean recordTrade(Trade trade) throws Exception{
		boolean result = false;
		try{
			result = tradeList.add(trade);
		} catch(Exception exception){
			throw new Exception("Error recording a trade in the stock.", exception);
		}
		return result;
	}

	public Stock getStockSymbol(String stockSymbol){
		Stock stock = null;
		try{
			if(stockSymbol!=null)
				stock = stockMap.get(stockSymbol);
		} catch(Exception exception){
			logger.error("Error retrieving the object.", exception);
		}
		return stock;
	}

}
