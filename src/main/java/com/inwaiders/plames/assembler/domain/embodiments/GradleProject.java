package com.inwaiders.plames.assembler.domain.embodiments;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.inwaiders.plames.assembler.domain.CompileRequest;
import com.inwaiders.plames.assembler.domain.parts.Compilable;
import com.inwaiders.plames.assembler.domain.parts.HasSettingsLine;
import com.inwaiders.plames.assembler.domain.providers.SrcProvider;

import ch.qos.logback.classic.Logger;

@Entity(name = "GradleProject")
@Table(name = "gradle_projects")
public class GradleProject extends Embodiment<SrcProvider> implements HasSettingsLine, Compilable {

	@Column(name = "name")
	private String name = null;
	
	public void load(CompileRequest request) throws Exception {
	
		File rootFolder = request.getRootFolder();
		
		File projectFolder = new File(rootFolder, name+"\\");
		
		provider.load(request, projectFolder);
	}
	
	@Override
	public void prepareToCompile(CompileRequest request) throws Exception {
		
		useDeployGradleFileIfExist(request);
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
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return this.name;
	}
	
	@Override
	public String getGradleDependencyLine() {
		
		return "compile project(\":"+name+"\")";
	}

	@Override
	public String getSettingsLine() {
		
		return "include ':"+name+"'";
	}
	
	public static GradleProject create() {
		
		GradleProject project = new GradleProject();
		
		project = repository.save(project);
		
		return project;
	}
}
