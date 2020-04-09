package com.inwaiders.plames.assembler.domain.compile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.hibernate.Hibernate;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inwaiders.plames.PlamesAssembler;
import com.inwaiders.plames.assembler.domain.parts.PartBootloader;
import com.inwaiders.plames.assembler.domain.parts.PartCore;
import com.inwaiders.plames.assembler.domain.parts.PartModule;
import com.inwaiders.plames.assembler.dto.compile.CompileRequestDto;
import com.inwaiders.plames.assembler.dto.parts.PartModuleDto;
import com.inwaiders.plames.assembler.utils.LoggerUtils;
import com.inwaiders.plames.assembler.utils.RamLogAppenger;
import com.inwaiders.plames.assembler.web.WebCompileLogAppender;

import ch.qos.logback.classic.Logger;

@Component(value="CompileRequest")
@Scope("prototype")
public class CompileRequest {
	
	public static ExecutorService providersLoadService = null;
	
	private static AtomicLong idGen = new AtomicLong();
	
	private Long id = null;
	
	private Logger logger = null;
	
	private PartBootloader partBootloader = null;
	private PartCore partCore = null;
	private List<PartModule> modules = new ArrayList<>();

	private String gradleBuildPattern = null;
	private String gradleSettingsPattern = null;
	
	private File projectPattern = null;
	
	private File rootFolder = null;
	
	private Status status = Status.NEW;
	
	public CompileRequest() {
		
		this.id = idGen.getAndIncrement();
	}
	
	public CompileRequest(Logger logger, File rootFolder, File projectPattern) {
		this();
		
		this.logger = logger;
		this.projectPattern = projectPattern;
		this.rootFolder = rootFolder;
	
		logger.addAppender(new RamLogAppenger(logger.getLoggerContext(), "ram"));
		logger.addAppender(new WebCompileLogAppender(logger.getLoggerContext(), "web"));
	}
	
	public void loadFromDto(CompileRequestDto dto) {
		
		this.partBootloader = (PartBootloader) Hibernate.unproxy(PartBootloader.findById(dto.partBootloader.id));	
		this.partCore = (PartCore) Hibernate.unproxy(PartBootloader.findById(dto.partCore.id));
	
		this.modules.clear();
	
		for(PartModuleDto moduleDto : dto.modules) {
			
			PartModule module = (PartModule) Hibernate.unproxy(PartModule.findById(moduleDto.id));
			
			this.modules.add(module);
		}
	}
	
	public CompileReport build() throws Exception {
		
		this.status = Status.BUILDING;
		
		long start = System.currentTimeMillis();
		
		logger.info("Begin build...");
		
		create();
		load();
		boolean compileResult = compile();
		
		long end = System.currentTimeMillis();
		
		if(compileResult) {
			
			this.status = Status.COMPLETE;
		}
		else {
			
			this.status = Status.FAIL;
		}
		
		logger.info("Build complete in "+new DecimalFormat("##.00").format((end-start)/1000D)+" s");
	
		CompileReport report = createReport();
	
		return report;
	}
	
	public void create() throws IOException {
		
		FileUtils.deleteDirectory(rootFolder);
		
		logger.info("Start copy project pattern...");
		
		FileUtils.copyDirectory(projectPattern, rootFolder);
		
		logger.info("Copy complete");
		
		File buildGradleFile = new File(rootFolder, "build.gradle");
		
		if(gradleBuildPattern == null && buildGradleFile.exists()) {
			
			gradleBuildPattern = new String(Files.readAllBytes(buildGradleFile.toPath()));
		}
		
		File settingsGradleFile = new File(rootFolder, "settings.gradle");
		
		if(gradleSettingsPattern == null && settingsGradleFile.exists()) {
			
			gradleSettingsPattern = new String(Files.readAllBytes(settingsGradleFile.toPath()));
		}
		
		String gradleBuildData = createGradlewBuild(this.gradleBuildPattern);
			writeGradleBuild(gradleBuildData);
	
		String gradleSettignsData = createGradlewSettings(this.gradleSettingsPattern);
			writeGradleSettings(gradleSettignsData);
		
		logger.info("Project creation complete!");
	}
	
	public void load() throws Exception {
		
		loadParts();
	}
	
	public void loadParts() throws Exception {
		
		partBootloader.load(this);
		partCore.load(this);
		
		if(PlamesAssembler.CONFIG.providersAsyncLoading) {
			
			Set<Future<?>> asyncLoadersFutures = new HashSet<>();
			
			for(PartModule module : modules) {
				
				Future<?> future = providersLoadService.submit(()-> {
					
					try {
						
						module.load(this);
					}
					catch(Exception e) {
						
						e.printStackTrace();
					}
				});
				
				asyncLoadersFutures.add(future);
			}
			
			for(Future<?> future : asyncLoadersFutures) {
				
				future.get();
			}
		}
		else {
			
			for(PartModule module : modules) {
				
				module.load(this);
			}
		}
	}
	
	public boolean compile() throws Exception {
		
		partBootloader.prepareToCompile(this);
		partCore.prepareToCompile(this);
		
		for(PartModule module : modules) {
			
			module.prepareToCompile(this);
		}
		
		Process gradleProcess = new ProcessBuilder("cmd", "/c", "gradlew", "bootJar")
			.directory(rootFolder)
		.start();
		
		Logger gradleLogger = (Logger) LoggerFactory.getLogger("Gradle");

		LoggerUtils.setRoot(gradleLogger, logger);
		
		BufferedReader processOutput = new BufferedReader(new InputStreamReader(gradleProcess.getInputStream()));
		BufferedReader processError = new BufferedReader(new InputStreamReader(gradleProcess.getErrorStream()));
		
		while(gradleProcess.isAlive()) {
			
			if(processError.ready()) processError.lines().forEach(gradleLogger::error);
			if(processOutput.ready()) processOutput.lines().forEach(gradleLogger::info);
		}
			
		int result = gradleProcess.exitValue();
	
		if(result == 0) {
			
			logger.info("Compile SUCCESS!");
			return true;
		}
		else {
			
			logger.info("Compile FAILED!");
			if(processError.ready()) processError.lines().forEach(gradleLogger::error);
			return false;
		}
	}
	
	public CompileReport createReport() {
		
		logger.info("Create report...");
		
		CompileReport report = CompileReport.create();
			report.setLog(((RamLogAppenger) logger.getAppender("ram")).getLog());
			report.setParts(new HashSet<>(this.modules));
			report.setGradleBuildPattern(this.gradleBuildPattern);	
			report.setGradleSettingsPattern(this.gradleSettingsPattern);
			
		report.save();
			
		logger.info("Report create complete!");
			
		return report;
	}
	
	public void writeGradleBuild(String gradleBuildData) throws IOException {
		
		File gradleBuild = new File(rootFolder, "build.gradle");
		
		logger.info("Write gradle build...");
		
		Files.write(gradleBuild.toPath(), gradleBuildData.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		
		logger.info("Write complete");
	}
	
	public void writeGradleSettings(String gradleSettingsData) throws IOException {
		
		File gradleSettings = new File(rootFolder, "settings.gradle");
	
		logger.info("Write gradle settings...");
		
		Files.write(gradleSettings.toPath(), gradleSettingsData.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
	
		logger.info("Write complete");
	}
	
	public String createGradlewBuild(String compilePattern) {
		
		StringBuilder compiledDependencies = new StringBuilder();
		
			compiledDependencies.append(partBootloader.getGradleDependencyLine()+"\n");
			compiledDependencies.append(partCore.getGradleDependencyLine()+"\n");
			
			for(PartModule module : modules) {
				
				compiledDependencies.append(module.getGradleDependencyLine()+"\n");
			}
		
		compilePattern = compilePattern.replace("<dependencies>", compiledDependencies.toString());
		
		return compilePattern;
	}
	
	public String createGradlewSettings(String compilePattern) {
		
		StringBuilder compiledSettings = new StringBuilder();
		
		String settignsLine = partBootloader.getSettingsLine();
		
		if(settignsLine != null && !settignsLine.isEmpty()) {
			
			compiledSettings.append(settignsLine+"\n");
		}
		
		settignsLine = partCore.getSettingsLine();
			
		if(settignsLine != null && !settignsLine.isEmpty()) {
			
			compiledSettings.append(settignsLine+"\n");
		}
		
		for(PartModule module : modules) {

			settignsLine = module.getSettingsLine();
		
			if(settignsLine != null && !settignsLine.isEmpty()) {
				
				compiledSettings.append(settignsLine+"\n");
			}
		}
		
		compilePattern = compilePattern.replace("<settings>", compiledSettings.toString());
		
		return compilePattern;
	}
	
	public void setPartCore(PartCore core) {
		
		this.partCore = core;
	}
	
	public PartCore getPartCore() {
		
		return this.partCore;
	}

	public void setPartBootloader(PartBootloader boot) {
		
		this.partBootloader = boot;
	}
	
	public PartBootloader getPartBootloader() {
		
		return this.partBootloader;
	}
	
	public void addModule(PartModule module) {
		
		modules.add(module);
	}
	
	public File getRootFolder() {
		
		return this.rootFolder;
	}
	
	public Logger getLogger() {
		
		return this.logger;
	}
	
	public Status getStatus() {
		
		return this.status;
	}
	
	public Long getId() {
		
		return this.id;
	}
	
	public static enum Status {
		
		NEW, BUILDING, COMPLETE, FAIL;
	}
}
