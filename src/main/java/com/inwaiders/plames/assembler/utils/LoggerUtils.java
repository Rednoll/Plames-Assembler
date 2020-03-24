package com.inwaiders.plames.assembler.utils;

import java.lang.reflect.Field;

import ch.qos.logback.classic.Logger;


public class LoggerUtils {

	private static Field loggerParentField = null;
	
	static {
		
		try {
			
			loggerParentField = Logger.class.getDeclaredField("parent");
		}
		catch (NoSuchFieldException e) {
			
			e.printStackTrace();
		}
		catch (SecurityException e) {
			
			e.printStackTrace();
		}
		
		loggerParentField.setAccessible(true);
	}
	
	public static void setRoot(Logger child, Logger parent) {
				
		try {
			
			loggerParentField.set(child, parent);
		}
		catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			
			e.printStackTrace();
		}
	}
}
