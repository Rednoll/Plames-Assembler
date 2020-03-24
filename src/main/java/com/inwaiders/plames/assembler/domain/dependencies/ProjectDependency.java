package com.inwaiders.plames.assembler.domain.dependencies;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.LoggerFactory;

import com.inwaiders.plames.assembler.domain.CompileRequest;
import com.inwaiders.plames.assembler.domain.git.Credentials;
import com.inwaiders.plames.assembler.utils.LoggerUtils;

import ch.qos.logback.classic.Logger;

public class ProjectDependency extends Dependency implements HasSettingsLine {

	public static ExecutorService loadService;
	
	private String name = null;
	
	private String repositoryAddress = null;
	
	private Credentials credentials = null;
	
	public ProjectDependency() {
		
	}
	
	public ProjectDependency(String projectName, String repositoryAddress) {
		
		this.name = projectName;
		this.repositoryAddress = repositoryAddress;
	}
	
	public ProjectDependency(String projectName, String repositoryAddress, Credentials creds) {
		this(projectName, repositoryAddress);
		
		this.credentials = creds;
	}
	
	public void load(CompileRequest request) throws Exception {
		
		syncLoad(request);
	}
	
	public Future<?> asyncLoad(CompileRequest request) {
		
		return loadService.submit(() -> {
			
			try {
				
				syncLoad(request);
			}
			catch (IOException | GitAPIException e) {
				
				e.printStackTrace();
			}
		});
	}
	
	private void syncLoad(CompileRequest request) throws IOException, InvalidRemoteException, TransportException, GitAPIException {
		
		Logger logger = (Logger) LoggerFactory.getLogger("Git");
		
		LoggerUtils.setRoot(logger, request.getLogger());
		
		File rootFolder = request.getRootFolder();
		
		File projectFolder = new File(rootFolder, name+"\\");
			FileUtils.deleteDirectory(projectFolder);
			projectFolder.mkdir();
			
		CloneCommand cloneCommand = Git.cloneRepository();
			cloneCommand.setURI(repositoryAddress);
			cloneCommand.setDirectory(projectFolder);
			
			if(credentials != null) {
				
				cloneCommand.setCredentialsProvider(credentials.getProvider());
			}
		
		logger.info("Start clone("+Thread.currentThread().getId()+") repository "+repositoryAddress+"...");
		
		Git git = cloneCommand.call();
	
		logger.info("Clone("+Thread.currentThread().getId()+") complete!");
	}
	
	public void prepare(CompileRequest request) {
	
		try {
			
			useDeployGradleFileIfExist(request);
		}
		catch(IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public void useDeployGradleFileIfExist(CompileRequest request) throws IOException {
		
		Logger logger = request.getLogger();
		
		File rootFolder = request.getRootFolder();
		
		File projectFolder = new File(rootFolder, name+"\\");
		
		File deployFile = new File(projectFolder, "deploy.gradle");
		
		if(deployFile.exists()) {
		
			logger.info("Use deploy.gradle file instead build.gradle for project: "+name);
			Files.copy(deployFile.toPath(), new File(projectFolder, "build.gradle").toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	@Override
	public String getGradleDependencyLine() {
		
		return "compile project(\":"+name+"\")";
	}

	public String getSettingsLine() {
		
		return "include \":"+name+"\"";
	}
	
	public String getName() {
		
		return this.name;
	}
}
