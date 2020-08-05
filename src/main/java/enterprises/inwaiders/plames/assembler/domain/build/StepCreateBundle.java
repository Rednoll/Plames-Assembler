package enterprises.inwaiders.plames.assembler.domain.build;

import java.io.File;

import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import enterprises.inwaiders.plames.PlamesAssembler;
import enterprises.inwaiders.plames.assembler.utils.ZipUtils;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

@Component
@Scope("singleton")
public class StepCreateBundle extends BuildStepBase {
	
	@Override
	public boolean run(BuildRequest request, Logger logger) throws Exception {
		
		ZipParameters zipParams = new ZipParameters();
			zipParams.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);
			zipParams.setCompressionMethod(Zip4jConstants.COMP_STORE);
		
		ZipFile bundle = new ZipFile(request.bundle);
			bundle.setRunInThread(false);
		
		bundle.addFile(request.jarFile, zipParams);
		
		for(File file : request.bundleBase.listFiles()) {
			
			if(file.isDirectory()) {
				
				ZipUtils.addFolder(bundle, file, (ZipParameters) zipParams.clone());
			}
			else {
				
				bundle.getProgressMonitor().setState(net.lingala.zip4j.progress.ProgressMonitor.STATE_READY);
				bundle.addFile(file, zipParams);
			}
		}

		return true;
	}

	@Override
	public boolean isVisible() {
		
		return true;
	}

	@Override
	public String getName() {
		
		return "Create bundle";
	}
}