package com.inwaiders.plames.assembler.domain;

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
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import com.inwaiders.plames.PlamesAssembler;
import com.inwaiders.plames.assembler.domain.parts.Part;
import com.inwaiders.plames.assembler.utils.LoggerUtils;
import com.inwaiders.plames.assembler.utils.RamLogAppenger;

import ch.qos.logback.classic.Logger;

public class CompileRequest {
	
	private Logger logger = null;
	
	private List<Part> parts = new ArrayList<>();

	private String gradleBuildPattern = null;
	private String gradleSettingsPattern = null;
	
	private File projectPattern = null;
	
	private File rootFolder = null;
	
	private Set<Future<?>> asyncLoadersFutures = null;
	
	public CompileRequest() {
		
		if(PlamesAssembler.CONFIG.providersAsyncLoading) {
			
			this.asyncLoadersFutures = new HashSet<>();
		}
	}
	
	public CompileRequest(Logger logger, File rootFolder, File projectPattern) {
		this();
		
		this.logger = logger;
		this.projectPattern = projectPattern;
		this.rootFolder = rootFolder;
	
		logger.addAppender(new RamLogAppenger(logger.getLoggerContext(), "ram"));
	}
	
	public CompileReport build() throws Exception {
		
		long start = System.currentTimeMillis();
		
		logger.info("Begin build...");
		
		create();
		load();
		compile();
		
		long end = System.currentTimeMillis();
		
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
		
		for(Part part : parts) {

			part.load(this);
		}
		
		for(Future<?> future : asyncLoadersFutures) {
			
			future.get();
		}
	}
	
	public void compile() throws Exception {
		
		for(Part part : parts) {
			
			part.prepareToCompile(this);
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
		}
		else {
			
			logger.info("Compile FAILED!");
			if(processError.ready()) processError.lines().forEach(gradleLogger::error);
		}
	}
	
	public CompileReport createReport() {
		
		logger.info("Create report...");
		
		CompileReport report = CompileReport.create();
			report.setLog(((RamLogAppenger) logger.getAppender("ram")).getLog());
			report.setParts(new HashSet<>(this.parts));
			report.setGradleBuildPattern(this.gradleBuildPattern);	
			report.setGradleSettingsPattern(this.gradleSettingsPattern);
			
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
		
			for(Part dep : parts) {
				
				compiledDependencies.append(dep.getGradleDependencyLine()+"\n");
			}
		
		compilePattern = compilePattern.replace("<dependencies>", compiledDependencies.toString());
		
		return compilePattern;
	}
	
	public String createGradlewSettings(String compilePattern) {
		
		StringBuilder compiledSettings = new StringBuilder();
		
		for(Part dep : parts) {

			String settignsLine = dep.getSettingsLine();
		
			if(settignsLine != null && !settignsLine.isEmpty()) {
				
				compiledSettings.append(settignsLine+"\n");
			}
		}
		
		compilePattern = compilePattern.replace("<settings>", compiledSettings.toString());
		
		return compilePattern;
	}

	public Set<Future<?>> getAsyncLoadersFutures() {

		return this.asyncLoadersFutures;
	}
	
	public void addPart(Part dep) {
		
		parts.add(dep);
	}
	
	public File getRootFolder() {
		
		return this.rootFolder;
	}
	
	public Logger getLogger() {
		
		return this.logger;
	}
}
