package com.inwaiders.plames.assembler.domain.compile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

	private PartModule api = null;
	private PartBootloader bootloader = null;
	private PartCore core = null;
	private Set<PartModule> modules = new HashSet<>();

	private String gradleBuildPattern = null;
	private String gradleSettingsPattern = null;
	
	private File projectPattern = null;
	
	private File rootDir = null;
	
	private Status status = Status.NEW;
	
	private boolean builded = false;
	
	public CompileRequest() {
		
		this.id = idGen.getAndIncrement();
		
		this.api = PartModule.findByName("Plames API");
	}
	
	public CompileRequest(Logger logger, File rootFolder, File projectPattern) {
		this();
		
		this.logger = logger;
		this.projectPattern = projectPattern;
		this.rootDir = rootFolder;
	
		logger.addAppender(new RamLogAppenger(logger.getLoggerContext(), "ram"));
		logger.addAppender(new WebCompileLogAppender(logger.getLoggerContext(), "web"));
	}
	
	public CompileRequestDto toDto() {
		
		CompileRequestDto dto = new CompileRequestDto();
			dto.bootloader = this.bootloader.toDto();
			dto.core = this.core.toDto();
			dto.modules = this.modules.stream().map(PartModule::toDto).collect(Collectors.toList());
			
		return dto;
	}
	
	public void loadFromDto(CompileRequestDto dto) {
		
		this.bootloader = PartBootloader.findById(dto.bootloader.id);	
		this.core = PartCore.findById(dto.core.id);
	
		this.modules.clear();
	
		for(PartModuleDto moduleDto : dto.modules) {
			
			PartModule module = PartModule.findById(moduleDto.id);
			
			this.modules.add(module);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void reinitHibernateProxies() {
		
		logger.info("Re-init hibernate proxies...");
		
		this.api = PartModule.findById(this.api.getId());
		this.bootloader = PartBootloader.findById(this.bootloader.getId());
		this.core = PartCore.findById(this.core.getId());
		
		Set<PartModule> newModules = new HashSet<>();
		
			for(PartModule module : this.modules) {
				
				newModules.add(PartModule.findById(module.getId()));
			}

		this.modules = newModules;
			
		logger.info("Re-init complete.");
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public CompileReport build() throws Exception {
		
		try {
			
			this.status = Status.BUILDING;
			
			long start = System.currentTimeMillis();
			
			logger.info("Begin build...");
			
			reinitHibernateProxies();
			
			create();
			load();
			boolean compileResult = compile();
			
			FileUtils.deleteDirectory(rootDir);
			
			long end = System.currentTimeMillis();
			
			if(compileResult) {
				
				this.status = Status.SUCCESS;
			}
			else {
				
				this.status = Status.FAIL;
			}
			
			logger.info("Build complete in "+new DecimalFormat("##.00").format((end-start)/1000D)+" s");
		
			this.builded = true;
		}
		catch(Exception e) {
		
			this.builded = true;
			this.status = Status.FAIL;
			throw e;
		}
		
		CompileReport report = createReport();
	
		return report;
	}
	
	public void create() throws IOException {
		
		FileUtils.deleteDirectory(rootDir);
		
		logger.info("Start copy project pattern...");
		
		FileUtils.copyDirectory(projectPattern, rootDir);
		
		logger.info("Copy complete");
		
		File buildGradleFile = new File(rootDir, "build.gradle");
		
		if(gradleBuildPattern == null && buildGradleFile.exists()) {
			
			gradleBuildPattern = new String(Files.readAllBytes(buildGradleFile.toPath()));
		}
		
		File settingsGradleFile = new File(rootDir, "settings.gradle");
		
		if(gradleSettingsPattern == null && settingsGradleFile.exists()) {
			
			gradleSettingsPattern = new String(Files.readAllBytes(settingsGradleFile.toPath()));
		}
		
		String gradleBuildData = createGradlewBuild(this.gradleBuildPattern);
			writeGradleBuild(gradleBuildData);
	
		String gradleSettignsData = createGradlewSettings(this.gradleSettingsPattern);
			writeGradleSettings(gradleSettignsData);
		
		logger.info("Project creation complete!");
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void load() throws Exception {
		
		loadParts();
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void loadParts() throws Exception {
		
		api.load(this);
		bootloader.load(this);
		core.load(this);
		
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
		
		api.prepareToCompile(this);
		bootloader.prepareToCompile(this);
		core.prepareToCompile(this);
		
		for(PartModule module : modules) {
			
			module.prepareToCompile(this);
		}
		
		Process gradleProcess = new ProcessBuilder("cmd", "/c", "gradlew", "bootJar")
			.directory(rootDir)
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
			report.setModules(new HashSet<>(this.modules));
			report.setGradleBuildPattern(this.gradleBuildPattern);	
			report.setGradleSettingsPattern(this.gradleSettingsPattern);
			report.setBootloader(this.bootloader);
			report.setCore(this.core);
			report.setResultStatus(this.status);
			
		report.save();
			
		logger.info("Report create complete!");
			
		return report;
	}
	
	public void writeGradleBuild(String gradleBuildData) throws IOException {
		
		File gradleBuild = new File(rootDir, "build.gradle");
		
		logger.info("Write gradle build...");
		
		Files.write(gradleBuild.toPath(), gradleBuildData.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		
		logger.info("Write complete");
	}
	
	public void writeGradleSettings(String gradleSettingsData) throws IOException {
		
		File gradleSettings = new File(rootDir, "settings.gradle");
	
		logger.info("Write gradle settings...");
		
		Files.write(gradleSettings.toPath(), gradleSettingsData.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
	
		logger.info("Write complete");
	}
	
	public String createGradlewBuild(String compilePattern) {
		
		StringBuilder compiledDependencies = new StringBuilder();
		
			compiledDependencies.append(bootloader.getGradleDependencyLine()+"\n");
			compiledDependencies.append(core.getGradleDependencyLine()+"\n");
			
			for(PartModule module : modules) {
				
				compiledDependencies.append(module.getGradleDependencyLine()+"\n");
			}
		
		compilePattern = compilePattern.replace("<dependencies>", compiledDependencies.toString());
		
		return compilePattern;
	}
	
	public String createGradlewSettings(String compilePattern) {
		
		StringBuilder compiledSettings = new StringBuilder();
		
		String settignsLine = api.getSettingsLine();
		
		if(settignsLine != null && !settignsLine.isEmpty()) {
			
			compiledSettings.append(settignsLine+"\n");
		}
		
		settignsLine = bootloader.getSettingsLine();
		
		if(settignsLine != null && !settignsLine.isEmpty()) {
			
			compiledSettings.append(settignsLine+"\n");
		}
		
		settignsLine = core.getSettingsLine();
			
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
	
	public boolean isBuilded() {
		
		return this.builded;
	}
	
	public void setRootDir(File dir) {
		
		this.rootDir = dir; 
	}
	
	public void setProjectPattern(File pattern) {
		
		this.projectPattern = pattern;
	}
	
	public void setCore(PartCore core) {
		
		this.core = core;
	}
	
	public PartCore getCore() {
		
		return this.core;
	}

	public void setBootloader(PartBootloader boot) {

		this.bootloader = boot;
	}
	
	public PartBootloader getBootloader() {
		
		return this.bootloader;
	}
	
	public void addModule(PartModule module) {
		
		modules.add(module);
	}
	
	public File getRootFolder() {
		
		return this.rootDir;
	}
	
	public void setLogger(Logger logger) {
		
		logger.addAppender(new RamLogAppenger(logger.getLoggerContext(), "ram"));
		logger.addAppender(new WebCompileLogAppender(logger.getLoggerContext(), "web"));
		
		this.logger = logger;
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
		
		NEW, BUILDING, SUCCESS, FAIL;
	}
}
