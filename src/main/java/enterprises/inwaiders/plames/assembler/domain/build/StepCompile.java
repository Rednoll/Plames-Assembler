package enterprises.inwaiders.plames.assembler.domain.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import enterprises.inwaiders.plames.PlamesAssembler;
import enterprises.inwaiders.plames.assembler.domain.parts.PartModule;
import enterprises.inwaiders.plames.assembler.utils.LoggerUtils;

@Component
@Scope("singleton")
public class StepCompile extends BuildStepBase {

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean run(BuildRequest request, Logger logger) throws Exception {
		
		ch.qos.logback.classic.Logger gradleLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("Gradle");
		LoggerUtils.setRoot(gradleLogger, (ch.qos.logback.classic.Logger) logger);

		request.api.prepareToCompile(request);
		request.bootloader.prepareToCompile(request);
		request.core.prepareToCompile(request);
		
		for(PartModule module : request.modules) {
			
			module.prepareToCompile(request);
		}
		
		List<String> command = new ArrayList<>();
			
			command.add("docker");
			command.add("run");
			command.add("--rm");
			command.add("-v \""+request.rootDir.getCanonicalPath().toLowerCase()+":/home/gradle/project\"");
			command.add("-v \""+new File(PlamesAssembler.CONFIG.gradleHome).getCanonicalPath().toLowerCase()+":/home/gradle/home\"");
			command.add("-w /home/gradle/project");
			command.add("plames-gradle-build");
		
		//Типичные костыли для винды, почему бы и нет
		if(SystemUtils.IS_OS_WINDOWS) {
			
			String winCommand = "";
			
			for(String com : command) {
				
				 winCommand += com+" ";
			}
			
			Files.write(new File(request.rootDir, "docker.bat").toPath(), winCommand.trim().getBytes());

			command.clear();
			command.add("cmd");
			command.add("/c");
			command.add("\""+new File(request.rootDir, "docker.bat").getCanonicalPath()+"\"");
		}
			
		Process gradleProcess = new ProcessBuilder(command)
			.directory(request.rootDir.getParentFile())
		.start();
	
		BufferedReader processOutput = new BufferedReader(new InputStreamReader(gradleProcess.getInputStream()));
		BufferedReader processError = new BufferedReader(new InputStreamReader(gradleProcess.getErrorStream()));
		
		while(gradleProcess.isAlive()) {
			
			if(processError.ready()) processError.lines().forEach(gradleLogger::error);
			if(processOutput.ready()) processOutput.lines().forEach(gradleLogger::info);
		}
			
		int result = gradleProcess.exitValue();
		
		if(result == 0) {
			
			return true;
		}
		else {
			
			if(processError.ready()) processError.lines().forEach(gradleLogger::error);
			return false;
		}
	}

	@Override
	public boolean isVisible() {
		
		return true;
	}

	@Override
	public String getName() {
		
		return "Compile";
	}
	
	
}

// FUCK DOCKER-JAVA LIBRARY, "win path no suported cause it have : in path, really? good reason for not support win paths, really fucking good"
/*
CreateContainerResponse response = PlamesAssembler.DOCKER_CLIENT.createContainerCmd("plames-gradle-build")
	.withBinds(new Bind(request.rootDir.getCanonicalPath(), new Volume("/home/gradle/project"), true))
	.withWorkingDir("/home/gradle/project")
.exec();

String containerId = response.getId();

System.out.println("containerId: "+containerId);

PlamesAssembler.DOCKER_CLIENT.startContainerCmd(containerId).exec();

Thread logThread = new Thread(()-> {
	
	AtomicInteger fullLogSize = new AtomicInteger(0);
	
	while(true) {
	
		AtomicInteger logSize = new AtomicInteger();
		
		try {
			
			PlamesAssembler.DOCKER_CLIENT.logContainerCmd(containerId)
				.withStdErr(true)
				.withStdOut(true)
				.withTailAll()
			.exec(new LogContainerResultCallback() {
				
			    @Override
			    public void onNext(Frame item) {
			    	
			    	String log = new String(item.getPayload());
			    	
			    	logSize.addAndGet(1);
			    	
			    	if(logSize.get() <= fullLogSize.get()) return;
			    	
			    	if(item.getStreamType() == StreamType.STDOUT) {
			    	
			    		gradleLogger.info(log);
			    	}
			    	
			    	if(item.getStreamType() == StreamType.STDERR) {
				    	
			    		gradleLogger.error(log);
			    	}
			    	
			    	fullLogSize.addAndGet(1);
			    }
			    
			}).awaitCompletion();
			
			Thread.sleep(500);
			
		} catch (InterruptedException e1) {
			
			e1.printStackTrace();
		}
	}
});

logThread.start();

WaitContainerResultCallback waitCallback = new WaitContainerResultCallback() {

	@Override
	public void onComplete() {
		super.onComplete();
		
		logThread.stop();
	}
};

int result = PlamesAssembler.DOCKER_CLIENT.waitContainerCmd(containerId).exec(waitCallback).awaitStatusCode();

if(result == 0) {
	
	return true;
}
else {
	
	return false;
}
*/