package enterprises.inwaiders.plames.assembler.domain.build;

import java.io.File;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import enterprises.inwaiders.plames.PlamesAssembler;

@Component
@Scope("singleton")
public class StepInit extends BuildStepBase {
	
	@Override
	public boolean run(BuildRequest request, Logger logger) throws Exception {
		
		logger.info("Clear root directory...");
		
		FileUtils.deleteDirectory(request.rootDir);
		
		logger.info("Start copy project pattern...");
		
		FileUtils.copyDirectory(request.projectPattern, request.rootDir);
		
		logger.info("Copy complete");
		
		File buildGradleFile = new File(request.rootDir, "build.gradle");
		
		if(request.gradleBuildPattern == null && buildGradleFile.exists()) {
			
			request.gradleBuildPattern = new String(Files.readAllBytes(buildGradleFile.toPath()));
		}
		
		File settingsGradleFile = new File(request.rootDir, "settings.gradle");
		
		if(request.gradleSettingsPattern == null && settingsGradleFile.exists()) {
			
			request.gradleSettingsPattern = new String(Files.readAllBytes(settingsGradleFile.toPath()));
		}
		
		String gradleBuildData = request.createGradleBuild(request.gradleBuildPattern);
			request.writeGradleBuild(gradleBuildData);
	
		String gradleSettignsData = request.createGradleSettings(request.gradleSettingsPattern);
			request.writeGradleSettings(gradleSettignsData);
			
		
		String bundleFileName = request.owner.getProductKey().toString()+".zip";
		request.bundle = new File(PlamesAssembler.CONFIG.storagePath, bundleFileName);
		
		if(request.bundle.exists()) {
			
			request.bundle.delete();
		}
			
		return true;
	}

	@Override
	public boolean isVisible() {
		
		return true;
	}

	@Override
	public String getName() {
		
		return "Init";
	}
}
