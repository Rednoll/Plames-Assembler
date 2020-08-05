package enterprises.inwaiders.plames.assembler.domain.build;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

public interface BuildStep {
	
	static final Map<String, BuildStep> steps = new HashMap<>();
	
	public boolean run(BuildRequest request, Logger logger) throws Exception;
	public boolean isVisible();
	public String getName();
	
	public BaseDto toDto();
	
	public static void addStep(BuildStep step) {
		
		steps.put(step.getName(), step);
	}
	
	public static BuildStep findByName(String name) {
		
		return steps.get(name);
	}
	
	public static class BaseDto {
		
		public String name;
		public boolean visible;
	}
}
