package enterprises.inwaiders.plames.assembler.domain.build;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Logger;
import enterprises.inwaiders.plames.PlamesAssembler;
import enterprises.inwaiders.plames.assembler.domain.build.events.BuildRequestListener;
import enterprises.inwaiders.plames.assembler.domain.parts.PartApi;
import enterprises.inwaiders.plames.assembler.domain.parts.PartBootloader;
import enterprises.inwaiders.plames.assembler.domain.parts.PartCore;
import enterprises.inwaiders.plames.assembler.domain.parts.PartModule;
import enterprises.inwaiders.plames.assembler.dto.compile.CompileRequestDto;
import enterprises.inwaiders.plames.assembler.dto.parts.PartModuleDto;
import enterprises.inwaiders.plames.assembler.utils.LoggerUtils;
import enterprises.inwaiders.plames.assembler.utils.RamLogAppenger;
import enterprises.inwaiders.plames.assembler.web.WebCompileLogAppender;
import enterprises.inwaiders.plames.eco.domain.user.User;

@Component(value="CompileRequest")
@Scope("prototype")
public class BuildRequest {
	
	public static ExecutorService providersLoadService = null;
	
	private static AtomicLong idGen = new AtomicLong();
	
	private List<BuildStep> steps = new ArrayList<>();
	
	private BuildStep currentStep = null;
	
	private Long id = null;
	
	private Logger logger = null;

	User owner = null;
	
	PartApi api = null;
	PartBootloader bootloader = null;
	PartCore core = null;
	Set<PartModule> modules = new HashSet<>();

	String gradleBuildPattern = null;
	String gradleSettingsPattern = null;
	
	File projectPattern = null;
	
	File rootDir = null;
	
	File bundleBase = null;
	
	File jarFile = null;
	
	File bundle = null;
	
	private Status status = Status.NEW;
	
	private boolean builded = false;
	
	private Set<BuildRequestListener> completeListeners = new HashSet<>();
	private Set<Consumer<BuildStep>> currentStepChangeListeners = new HashSet<>();
	
	public BuildRequest() {
		
		this.id = idGen.getAndIncrement();
		
		this.api = PartApi.findByName("Plames API");
	}
	
	public BuildRequest(Logger logger, File rootFolder, File projectPattern) {
		this();
		
		this.logger = logger;
		this.projectPattern = projectPattern;
		this.rootDir = rootFolder;
	
		logger.addAppender(new RamLogAppenger(logger.getLoggerContext(), "ram"));
		logger.addAppender(new WebCompileLogAppender(logger.getLoggerContext(), "web"));
	}
	
	@PostConstruct
	private void postConstruct() {
		
		steps.add(PlamesAssembler.CONTEXT.getBean(StepReinitHibernateProxies.class));
		steps.add(PlamesAssembler.CONTEXT.getBean(StepInit.class));
		steps.add(PlamesAssembler.CONTEXT.getBean(StepLoad.class));
		steps.add(PlamesAssembler.CONTEXT.getBean(StepCompile.class));
		steps.add(PlamesAssembler.CONTEXT.getBean(StepObfuscate.class));
		steps.add(PlamesAssembler.CONTEXT.getBean(StepCreateBundle.class));
		steps.add(PlamesAssembler.CONTEXT.getBean(StepClear.class));
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
	
	public File getBundle() {
		
		return this.bundle;
	}
	
	public void setOwner(User user) {
		
		this.owner = user;
	}
	
	public User getOwner() {
		
		return this.owner;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public BuildReport build() {
		
		this.status = Status.BUILDING;
	
		this.jarFile = new File(this.rootDir, "build/libs/plames.jar");
		this.bundleBase = new File(this.rootDir, "bundle_base");
		
		for(BuildStep step : steps) {
			
			Logger logger = (Logger) LoggerFactory.getLogger(step.getName());

			LoggerUtils.setRoot(logger, this.logger);
			
			currentStep = step;
			
			currentStepChangeListeners.forEach(listener -> listener.accept(currentStep));
			
			if(step.isVisible()) {
				
				logger.info("Begin - "+step.getName());
			}
			
			boolean result = false; 
			
			try {
				
				result = step.run(this, logger);
			}
			catch(Exception e) {
				
				logger.error(e.getLocalizedMessage());
				
				for(StackTraceElement el : e.getStackTrace()) {

					logger.error(el.toString());
				}
			
				result = false;
			}
			
			if(step.isVisible()) {
				
				logger.info(step.getName()+" "+(result ? "- SUCCESS": "- FAILED"));
			}
			
			if(!result) {
				
				this.status = Status.FAIL;
				break;
			}
		}
		
		if(this.status == Status.BUILDING) {
			
			this.status = Status.SUCCESS;
		}
		
		BuildReport report = createReport();
		
		this.builded = true;
		
		completeListeners.forEach(listener -> listener.run(this));
		
		return report;
	}
	
	public void addCurrentStepChangeListener(Consumer<BuildStep> listener) {
		
		this.currentStepChangeListeners.add(listener);
	}
	
	/*
	@Transactional(propagation = Propagation.REQUIRED)
	public void reinitHibernateProxies() {
		
		logger.info("Re-init hibernate proxies...");
		
		this.api = PartApi.findById(this.api.getId());
		this.bootloader = PartBootloader.findById(this.bootloader.getId());
		this.core = PartCore.findById(this.core.getId());
		
		Set<PartModule> newModules = new HashSet<>();
		
			for(PartModule module : this.modules) {
				
				newModules.add(PartModule.findById(module.getId()));
			}

		this.modules = newModules;
			
		logger.info("Re-init complete.");
	}
	*/
	
	/*
	@Transactional(propagation = Propagation.REQUIRED)
	public BuildReport build() throws Exception {
		
		try {
			
			this.status = Status.BUILDING;
			
			long start = System.currentTimeMillis();
			
			logger.info("Begin build...");
			
			reinitHibernateProxies();
			
			create();
			load();
			boolean lastOperationResult = compile();
			
//			FileUtils.deleteDirectory(rootDir);
			
			if(lastOperationResult) {
				
				lastOperationResult = obfuscate();
			}
			
			long end = System.currentTimeMillis();
			
			if(lastOperationResult) {
				
				this.status = Status.SUCCESS;
			}
			else {
				
				this.status = Status.FAIL;
			}
			
			logger.info("Build complete in "+new DecimalFormat("##.00").format((end-start)/1000D)+" s");
		}
		catch(Exception e) {
	
			this.status = Status.FAIL;
			throw e;
		}
		
		BuildReport report = createReport();
		
		this.builded = true;
		
		completeListeners.forEach(listener -> listener.run(this));
		
		return report;
	}
	*/
	
	/*
	public boolean obfuscate() throws Exception {
		
		Logger logger = (Logger) LoggerFactory.getLogger("Obfuscate");

		LoggerUtils.setRoot(logger, this.logger);
		
		logger.info("Start obfuscate...");
		
		ZipFile jar = new ZipFile(new File(rootDir, "build/libs/plames.jar"));
			jar.setRunInThread(false);
			
		//Clean jar
		logger.info("Cleaning sources...");
		
		jar.removeFile("BOOT-INF/lib/"+api.getEmbodiment().getJarFile(this).getName());
		jar.removeFile("BOOT-INF/lib/"+core.getEmbodiment().getJarFile(this).getName());
		jar.removeFile("BOOT-INF/lib/"+bootloader.getEmbodiment().getJarFile(this).getName());
		
		for(PartModule module : modules) {
			
			String libraryName = module.getEmbodiment().getJarFile(this).getName();
		
			logger.info("Remove "+libraryName+".");
			
			jar.getProgressMonitor().setState(net.lingala.zip4j.progress.ProgressMonitor.STATE_READY);
			jar.removeFile("BOOT-INF/lib/"+libraryName);
		}
		
		logger.info("Cleaning complete.");
		
		//Extract jar
		logger.info("Extracting sources...");
		
		File unpacked = new File(rootDir, "/build/unpacked/");
			unpacked.mkdir();

		jar.extractAll(unpacked.getAbsolutePath());

		logger.info("Extracting complete.");
		
		//Run ProGuard
		logger.info("Run ProGuard...");
		
		File obfDir = new File(rootDir, "build/obf");
		
		String command = "-jar proguard/proguard.jar";
			command += " -include proguard/settings.proguard";
			command += " -injars \"";
			
				command += api.getEmbodiment().getJarFile(this).getAbsolutePath()+";";
				command += core.getEmbodiment().getJarFile(this).getAbsolutePath()+";";
				command += bootloader.getEmbodiment().getJarFile(this).getAbsolutePath();
			
				for(PartModule module : modules) {
					
					command += ";"+module.getEmbodiment().getJarFile(this).getAbsolutePath();
				}
			
			command += "\"";
			
			command += " -outjars \""+obfDir.getAbsolutePath()+"\"";
			command += " -libraryjars \""+System.getProperty("java.home")+"/lib/rt.jar;build/unpacked/BOOT-INF/lib";
			
				//additionals libraries
			
			command +="\"";
		
		System.out.println("args: "+command);
			
		Process gradleProcess = new ProcessBuilder("cmd", "/c", "java "+command)
			.directory(rootDir)
		.start();
		
		Logger proGuardLogger = (Logger) LoggerFactory.getLogger("ProGuard");

		LoggerUtils.setRoot(proGuardLogger, this.logger);
		
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
			
		logger.info("Obfuscate complete!");
		return true;
	}
	
	*/
	
	/*
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
	*/
	
	/*
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
	*/
	
	/*
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
		
		Logger logger = (Logger) LoggerFactory.getLogger("Gradle");

		LoggerUtils.setRoot(logger, this.logger);
		
		BufferedReader processOutput = new BufferedReader(new InputStreamReader(gradleProcess.getInputStream()));
		BufferedReader processError = new BufferedReader(new InputStreamReader(gradleProcess.getErrorStream()));
		
		while(gradleProcess.isAlive()) {
			
			if(processError.ready()) processError.lines().forEach(logger::error);
			if(processOutput.ready()) processOutput.lines().forEach(logger::info);
		}
			
		int result = gradleProcess.exitValue();
	
		if(result == 0) {
			
			logger.info("Compile SUCCESS!");
			return true;
		}
		else {
			
			logger.info("Compile FAILED!");
			if(processError.ready()) processError.lines().forEach(logger::error);
			return false;
		}
	}
	*/
	
	public BuildReport createReport() {
		
		logger.info("Create report...");
		
		BuildReport report = BuildReport.create();
			report.setLog(((RamLogAppenger) logger.getAppender("ram")).getLog());
			report.setModules(new HashSet<>(this.modules));
			report.setGradleBuildPattern(this.gradleBuildPattern);	
			report.setGradleSettingsPattern(this.gradleSettingsPattern);
			report.setBootloader(this.bootloader);
			report.setCore(this.core);
			report.setResultStatus(this.status);
			report.setSteps(this.steps);
			
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
	
	public String createGradleBuild(String compilePattern) {
		
		StringBuilder compiledDependencies = new StringBuilder();
		
			compiledDependencies.append(bootloader.getGradleDependencyLine()+"\n");
			compiledDependencies.append(core.getGradleDependencyLine()+"\n");
			
			for(PartModule module : modules) {
				
				compiledDependencies.append(module.getGradleDependencyLine()+"\n");
			}
		
		compilePattern = compilePattern.replace("<dependencies>", compiledDependencies.toString());
		
		return compilePattern;
	}
	
	public String createGradleSettings(String compilePattern) {
		
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
	
	public BuildStep getCurrentStep() {
		
		return this.currentStep;
	}
	
	public List<BuildStep> getSteps() {
		
		return this.steps;
	}
	
	public void addCompleteListener(BuildRequestListener listener) {
		
		this.completeListeners.add(listener);
	}
	
	public void removeCompleteListener(BuildRequestListener listener) {
		
		this.completeListeners.remove(listener);
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
