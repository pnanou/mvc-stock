package com.stocks.utils;

public class SimpleStockUtils {

	// static method that retrieves a double value and it returns the same value with only 3 decimals
	public static double round(double value) {

	    long factor = (long) Math.pow(10, 3); // get only 3 decimals
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
