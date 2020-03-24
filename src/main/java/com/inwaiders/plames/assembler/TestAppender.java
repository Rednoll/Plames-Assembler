package com.inwaiders.plames.assembler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class TestAppender extends AppenderBase<ILoggingEvent> {
	
	private PatternLayout layout = null;
	
	public TestAppender(LoggerContext context) {
		
		this.context = context;
		
		this.layout = new PatternLayout();
		this.layout.setPattern("%d %level{6} %logger{24} : %m%n");
		this.layout.setContext(context);
		this.layout.start();
		
		this.started = true;
	}
	
	@Override
	protected void append(ILoggingEvent eventObject) {
		
		System.out.print(this.layout.doLayout(eventObject));
	}
}