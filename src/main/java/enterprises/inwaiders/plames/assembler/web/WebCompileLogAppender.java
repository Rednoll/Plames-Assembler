package enterprises.inwaiders.plames.assembler.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class WebCompileLogAppender extends AppenderBase<ILoggingEvent> {
	
	private PatternLayout layout = null;
	private Queue<String> lines = new LinkedList<>();
	
	public WebCompileLogAppender(LoggerContext context, String name) {
		
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
		
		lines.add(this.layout.doLayout(eventObject));
	}
	
	public List<String> getLines(int count) {
	
		List<String> linkedList = new LinkedList<>();
	
		if(count == -1) {
			
			count = lines.size();
		}
		
		for(int i = 0; i < count && lines.size() > 0; i++) {
			
			linkedList.add(lines.poll());
		}
		
		return linkedList;
	}
}