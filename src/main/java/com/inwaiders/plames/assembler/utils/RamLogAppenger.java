package com.inwaiders.plames.assembler.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class RamLogAppenger extends AppenderBase<ILoggingEvent> {
	
	private PatternLayout layout = null;
	
	private String log = "";
	
	public RamLogAppenger(LoggerContext context, String name) {
		
		this.context = context;
		this.name = name;
		
		this.layout = new PatternLayout();
		this.layout.setPattern("%d %level{6} %logger{24} : %m%n");
		this.layout.setContext(context);
		this.layout.start();
		
		this.started = true;
	}
	
	@Override
	protected void append(ILoggingEvent eventObject) {
		
		log += this.layout.doLayout(eventObject);
	}
	
	public String getLog() {
		
		return this.log;
	}
}
