package stock;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.stocks.controller.SimpleStockController;
import com.stocks.model.Stock;
import com.stocks.model.Trade;
import com.stocks.utils.SimpleStockUtils;
import com.stocks.utils.Type;
import com.stocks.view.StockView;

public class StockTest {

	Logger logger = Logger.getLogger(StockTest.class);

	String[] stockSymbols = {"TEA", "POP", "ALE", "GIN", "JOE"};
	
	SimpleStockController simpleStockController;
	StockView view = new StockView();
	HashMap<String, Stock> mapStock = new HashMap<String, Stock>();
	ArrayList<Trade> listOfTrades = new ArrayList<Trade>();

	/*
	 *  method that will be executed before the actual tests
	 *  Initialize the values from the stocks.xml 
	*/
	@Before
	public void loadStockValues(){
		try {
			File fXmlFile = new File("resources/stocks.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("stockSymbol");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				Stock stock = new Stock();

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					stock.setStockSymbol(eElement.getAttribute("id"));

					if (eElement.getElementsByTagName("stockType").item(0).getTextContent().equals("COMMON")) 
						stock.setStockType(Type.COMMON);
					else
						stock.setStockType(Type.PREFERRED);

					stock.setLastDividend(Double.parseDouble(eElement.getElementsByTagName("lastDividend").item(0).getTextContent()));
					stock.setFixedDividend(Double.parseDouble(eElement.getElementsByTagName("fixedDividend").item(0).getTextContent()));
					stock.setParValue(Double.parseDouble(eElement.getElementsByTagName("parValue").item(0).getTextContent()));

					mapStock.put(eElement.getAttribute("id"), stock);
				}
			}

			view.setStocks(mapStock);
			simpleStockController = new SimpleStockController(view);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 *  method that will be executed before the actual tests
	 *  Initialize the values from the trades.xml
	 *  The trades.xml file is configurable and it holds the trade price, the BUY and SELL indicator and the number of shares 
	*/
	@Before
	public void loadTradeValues(){
		try {
			File fXmlFile = new File("resources/trades.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("trade");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Trade trade = new Trade();
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					if (eElement.getElementsByTagName("tradeIndicator").item(0).getTextContent().equals("BUY")) 
						trade.setTradeType(Type.BUY);
					else
						trade.setTradeType(Type.SELL);

					trade.setTradePrice(Double.parseDouble(eElement.getElementsByTagName("tradedPrice").item(0).getTextContent()));
					trade.setQuantityOfShares(Integer.parseInt(eElement.getElementsByTagName("sharesQuantity").item(0).getTextContent()));

					for (Map.Entry<String, Stock> entry : mapStock.entrySet())
					{
						if (entry.getKey().equals(eElement.getElementsByTagName("stock").item(0).getTextContent()))
							trade.setStock(entry.getValue());
					}

					trade.setTimeStamp(new Date());

					simpleStockController.recordTradeValue(trade);
					listOfTrades.add(trade);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 *  Real JUnit test that calculates the Dividend Yield number
	 * 
	 */
	@Test
	public void calculateDividendYieldTest() {
		logger.info("Starting calculateDividendYieldTest...");
		try{
			for(String stockSymbol: stockSymbols){
				double dividendYieldNumber = simpleStockController.getDividendYieldValue(stockSymbol);
				logger.info("For " + stockSymbol + " the DividendYield number is " + SimpleStockUtils.round(dividendYieldNumber));
				Assert.assertTrue(dividendYieldNumber >= 0.0); // must be greater than zero
			}

		}catch(Exception ex){
			logger.error(ex);
			Assert.assertTrue(false);
		}

		logger.info("calculateDividendYieldTest is OK");
	}

	/*
	 *  Real JUnit test that calculates the P/E Ratio
	 * 
	 */
	@Test
	public void calculatePERatioTest(){
		logger.info("Starting calculatePERatioTest.");
		try{
			for(String stockSymbol: stockSymbols){
				double peRatioValue = simpleStockController.getPERatioValue(stockSymbol);
				logger.info("For " + stockSymbol + " the P/E Ratio value is " + SimpleStockUtils.round(peRatioValue));
				Assert.assertTrue(peRatioValue >= 0.0); // must be greater than zero
			}
		}catch(Exception ex){
			logger.error(ex);
			Assert.assertTrue(false);
		}

		logger.info("calculatePERatioTest is OK");
	}

	/*
	 *  Real JUnit test that calculates the Stock Price
	 * 
	 */
	@Test
	public void calculateStockPriceTest(){
		logger.info("Starting calculateStockPriceTest.");
		try{
			for(String stockSymbol: stockSymbols){
				double stockPrice = simpleStockController.calculateStockPrice(stockSymbol);
				logger.info("For " + stockSymbol + " the Stock Price is " + SimpleStockUtils.round(stockPrice));
				Assert.assertTrue(stockPrice >= 0.0); // must be greater than zero
			}

		}catch(Exception ex){
			logger.error(ex);
			Assert.assertTrue(false);
		}

		logger.info("calculateStockPriceTest is OK");
	}

	/*
	 *  Real JUnit test that calculates the GBCE Share Index
	 * 
	 */
	@Test
	public void calculateGBCEAllShareIndexTest(){
		logger.info("Starting calculateGBCEAllShareIndexTest.");
		try{
			double GBCEAllShareIndex = simpleStockController.getGBCEAllShareIndex();
			logger.info("The GBCE All Share Index is " + SimpleStockUtils.round(GBCEAllShareIndex));
			Assert.assertTrue(GBCEAllShareIndex > 0.0); // must be greater than zero

		}catch(Exception ex){
			logger.error(ex);
			Assert.assertTrue(false);
		}

		logger.info("calculateGBCEAllShareIndexTest is OK");
	}

	/*
	 *  Real JUnit test that calculates the records trades
	 * 
	 */
	@Test
	public void recordATradeTest(){
		logger.info("Starting recordATradeTest.");

		// Recover the trades configured from the trades.xml
		ArrayList<Trade> tradeList = new ArrayList<Trade>();
		tradeList.addAll(listOfTrades);
		Assert.assertNotNull(tradeList);
		logger.info("Trade List size: "+tradeList.size());

		try{
			int tradesNumber = view.getTrades().size();

			// Insert recording the trades in the stock system
			for(Trade trade: tradeList){
				boolean result = simpleStockController.recordTradeValue(trade);
				Assert.assertTrue(result);
			}

			// check that the trades are copied successfully
			logger.info("Trades number: "+tradesNumber);
			Assert.assertEquals(tradesNumber, tradeList.size());
		}catch(Exception ex){
			logger.error(ex);
			Assert.assertTrue(false);
		}

		logger.info("recordATradeTest is OK");
	}

}
