package com.stocks.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.log4j.Logger;

import com.stocks.model.Stock;
import com.stocks.model.Trade;
import com.stocks.utils.SimpleStockUtils;
import com.stocks.view.StockView;


public class SimpleStockController {

	private Logger logger = Logger.getLogger(SimpleStockController.class);
	private StockView stockView = null;


	public SimpleStockController(StockView stockView) {
		this.stockView = stockView;
	}

	public double getDividendYieldValue(String stockSymbol) throws Exception {
		logger.debug("getDividendYieldValue for stock: "+stockSymbol);
		
		double dividendYield;
		try{
			Stock stock = stockView.getStockSymbol(stockSymbol);

			if(stock==null) // Exit if the stock is not supported exit
				throw new Exception("The " + stockSymbol + " is not supported by the application. Exiting...");

			if(stock.getCurrentPrice() <= 0.0) // Exit if the price is less than 0
				throw new Exception("The current price for " + stockSymbol + " should be greater than zero.");
			
			// since we are here get the actual value
			dividendYield = stock.getDividendYield();

		}catch(Exception exception){
			throw new Exception("Error during the calculation for the DividendYield for " + stockSymbol, exception);
		}
		
		logger.info("The DividendYieldValue is: " + SimpleStockUtils.round(dividendYield));
		return dividendYield;
	}


	public double getPERatioValue(String stockSymbol) throws Exception{
		logger.debug("Calculating P/E Ratio for " + stockSymbol);
		
		double peRatio = -1.0;
		try{
			Stock stock = stockView.getStockSymbol(stockSymbol);
			
			if(stock==null) // Exit if the stock is not supported exit
				throw new Exception("The " + stockSymbol + " is not supported by the application. Exiting...");

			if(stock.getCurrentPrice() <= 0.0) // Exit if the price is less than 0
				throw new Exception("The current price for " + stockSymbol + " should be greater than zero.");

			// since we are here get the actual value
			peRatio = stock.getPeRatio();

		}catch(Exception exception){
			throw new Exception("Error during the calculation for P/E Ratio for " + stockSymbol, exception);
		}
		
		logger.debug("The P/E Ratio Value is: " + SimpleStockUtils.round(peRatio) );
		return peRatio;
	}

	/*
	 *
	 */
	public boolean recordTradeValue(Trade trade) throws Exception{
		logger.debug("Entering recordTradeValue method.");
		
		boolean recordResult = false;
		try{
			if(trade==null) // Exit if it is null
				throw new Exception("Invalid trade.");

			// stock should be an object
			if(trade.getStock()==null)
				throw new Exception("The associated Stock must not be null.");
			
			// shares quantity should be greater than zero
			if(trade.getQuantityOfShares()<=0)
				throw new Exception("The Quantity of shares must not be less than zero.");
			else
				logger.debug("Quantity of shares is: " + trade.getQuantityOfShares());

			if(trade.getTradePrice()<=0.0)  // Exit if the traded price is less than 0
				throw new Exception("The Traded Price must not be less than zero.");
			 else
				logger.debug("Traded price is: " + trade.getTradePrice());

			recordResult = stockView.recordTrade(trade);

			// Update the price for the given stock
			if(recordResult)
				trade.getStock().setCurrentPrice(trade.getTradePrice());

		}catch(Exception exception){
			throw new Exception("Error during in recording the trade.", exception);
		}
		
		return recordResult;
	}


	@SuppressWarnings("unchecked")
	private double getPriceRange(String stock, int range) throws Exception{
		logger.debug("Entering getPriceRange method");

		double price = 0.0;

		Collection<Trade> trades = CollectionUtils.select(stockView.getTrades(), new FuturePrediction(stock, range));

		double totalSharesNum = 0.0;
		double currentTradePrice = 0.0;
		for(Trade trade : trades){ // sum up the trades
			currentTradePrice += (trade.getTradePrice() * trade.getQuantityOfShares());
			totalSharesNum += trade.getQuantityOfShares();
		}

		if(totalSharesNum >= 0.0)
			price = (currentTradePrice / totalSharesNum); // final price

		return price;
	}


	/*
	 *
	 */
	public double calculateStockPrice(String stockSymbol) throws Exception{
		logger.debug("Calculating Stock Price for "+stockSymbol);
		
		double stockPrice;
		try{
			Stock stock = stockView.getStockSymbol(stockSymbol);

			if(stock==null) // Exit if it is null
				throw new Exception("The " + stockSymbol + " is not supported by the application. Exiting...");

			stockPrice = getPriceRange(stockSymbol, 15); // get it for 15 minutes

		}catch(Exception ex){
			throw new Exception("Error calculating P/E Ratio for the stock symbol: "+stockSymbol+".", ex);
		}

		logger.info("The calculateStockPrice for " + stockSymbol + " is " + SimpleStockUtils.round(stockPrice));
		return stockPrice;
	}

	/*
	 *
	 */
	public double getGBCEAllShareIndex() throws Exception{
		double finalShareIndex = 0.0;
		
		// Calculate stock price for all stock in the system
		HashMap<String, Stock> stocks = stockView.getStocks();
		ArrayList<Double> stockPriceList = new ArrayList<Double>();
		for(String stockSymbol: stocks.keySet() ){
			double stockPrice = getPriceRange(stockSymbol, 0); // get the current
			if(stockPrice > 0){
				stockPriceList.add(stockPrice);
			}
		}
		
		if(stockPriceList.size()>=1){
			double[] stockPrices = new double[stockPriceList.size()];
			
			for(int i=0; i<=(stockPriceList.size()-1); i++){
				stockPrices[i] = stockPriceList.get(i).doubleValue();
			}
			
			finalShareIndex = StatUtils.geometricMean(stockPrices); // Retrieving from commons math jar
		}
		logger.info("The getGBCEAllShareIndex is " + SimpleStockUtils.round(finalShareIndex) );
		return finalShareIndex;
	}
	
	private class FuturePrediction implements Predicate {

		private Logger logger = Logger.getLogger(FuturePrediction.class);
		private String stock;
		private Calendar cal = null;

		public FuturePrediction(String stock, int range){
			this.stock = stock;
			if( range > 0 ){
				cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, -range);
				logger.debug(String.format("In past 15 minutes. Range time is %tF %tT", cal.getTime(), cal.getTime()));
			}

		}

		public boolean evaluate(Object obj) {
			Trade trade = (Trade) obj;
			boolean stockFound = trade.getStock().getStockSymbol().equals(stock);
			if(stockFound && cal != null){
				stockFound = cal.getTime().compareTo(trade.getTimeStamp())<=0;
			}
			return stockFound;
		}

	}


}

