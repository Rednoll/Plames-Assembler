package enterprises.inwaiders.plames.assembler.domain.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import enterprises.inwaiders.plames.assembler.domain.parts.PartModule;
import enterprises.inwaiders.plames.assembler.utils.LoggerUtils;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

@Component
@Scope("singleton")
public class StepObfuscate extends BuildStepBase {
	
	@Override
	public boolean run(BuildRequest request, Logger logger) throws Exception {
		
		ZipFile jar = new ZipFile(request.jarFile);
			jar.setRunInThread(false);
			
		//Clean jar
		logger.info("Cleaning sources...");
		
		jar.removeFile("BOOT-INF/lib/"+request.api.getEmbodiment().getJarFile(request).getName());
		jar.removeFile("BOOT-INF/lib/"+request.core.getEmbodiment().getJarFile(request).getName());
		jar.removeFile("BOOT-INF/lib/"+request.bootloader.getEmbodiment().getJarFile(request).getName());
		
		for(PartModule module : request.modules) {
			
			String libraryName = module.getEmbodiment().getJarFile(request).getName();
		
			logger.info("Remove "+libraryName+".");
			
			jar.getProgressMonitor().setState(net.lingala.zip4j.progress.ProgressMonitor.STATE_READY);
			jar.removeFile("BOOT-INF/lib/"+libraryName);
		}
		
		logger.info("Cleaning complete.");
		
		//Extract jar
		logger.info("Extracting sources...");
		
		File unpacked = new File(request.rootDir, "/build/unpacked/");
			unpacked.mkdir();

		jar.extractAll(unpacked.getAbsolutePath());

		logger.info("Extracting complete.");
		
		//Run ProGuard
		logger.info("Run ProGuard...");
		
		File obfDir = new File(request.rootDir, "build/obf");
		
		String command = "-jar proguard/proguard.jar";
			command += " -include proguard/settings.proguard";
			command += " -injars \"";
			
				command += request.api.getEmbodiment().getJarFile(request).getAbsolutePath()+";";
				command += request.core.getEmbodiment().getJarFile(request).getAbsolutePath()+";";
				command += request.bootloader.getEmbodiment().getJarFile(request).getAbsolutePath();
			
				for(PartModule module : request.modules) {
					
					command += ";"+module.getEmbodiment().getJarFile(request).getAbsolutePath();
				}
			
			command += "\"";
			
			command += " -outjars \""+obfDir.getAbsolutePath()+"\"";
			command += " -libraryjars \""+System.getProperty("java.home")+"/lib/rt.jar;build/unpacked/BOOT-INF/lib";
			
				//additionals libraries
			
			command +="\"";
			
			command += " -dump \""+new File(request.bundle.getParent(), request.owner.getProductKey()+".pgmap").getAbsolutePath()+"\"";
			
		Process gradleProcess = new ProcessBuilder("cmd", "/c", "java "+command)
			.directory(request.rootDir)
		.start();
		
		ch.qos.logback.classic.Logger proGuardLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("ProGuard");

		LoggerUtils.setRoot(proGuardLogger, (ch.qos.logback.classic.Logger) logger);
		
		BufferedReader processOutput = new BufferedReader(new InputStreamReader(gradleProcess.getInputStream()));
		BufferedReader processError = new BufferedReader(new InputStreamReader(gradleProcess.getErrorStream()));
		
		while(gradleProcess.isAlive()) {
			
			if(processError.ready()) processError.lines().forEach(proGuardLogger::error);
			if(processOutput.ready()) processOutput.lines().forEach(proGuardLogger::info);
		}
			
		int result = gradleProcess.exitValue();
	
		if(result == 0) {
			
			proGuardLogger.info("ProGuard SUCCESS!");
			
			logger.info("Injecting obfuscated jars...");
			
			ZipParameters addParameters = new ZipParameters();
				addParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);
				addParameters.setCompressionMethod(Zip4jConstants.COMP_STORE);
				addParameters.setRootFolderInZip("BOOT-INF/lib/");
			
			for(File obfJar : obfDir.listFiles()) {
				
				logger.info("Inject "+obfJar.getName());
				
				jar.getProgressMonitor().setState(net.lingala.zip4j.progress.ProgressMonitor.STATE_READY);
				jar.addFile(obfJar, addParameters);
			}
			
			logger.info("Injecting complete.");
		}
		else {
			
			proGuardLogger.info("ProGuard FAILED!");
			if(processError.ready()) processError.lines().forEach(proGuardLogger::error);
			return false;
		}
		
		return true;
	}

	@Override
	public boolean isVisible() {
		
		return true;
	}

	@Override
	public String getName() {
		
		return "Obfuscate";
	}
}