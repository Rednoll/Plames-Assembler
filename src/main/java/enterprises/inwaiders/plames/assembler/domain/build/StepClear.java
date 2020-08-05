package enterprises.inwaiders.plames.assembler.domain.build;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class StepClear extends BuildStepBase {
	
	@Override
	public boolean run(BuildRequest request, Logger logger) throws Exception {
		
		logger.info("Cleaning work directory...");
		
		FileUtils.deleteDirectory(request.rootDir);
		
		return true;
	}

	@Override
	public boolean isVisible() {
		
		return true;
	}

	@Override
	public String getName() {
		
		return "Clear";
	}
}
