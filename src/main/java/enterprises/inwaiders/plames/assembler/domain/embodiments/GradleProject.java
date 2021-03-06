package enterprises.inwaiders.plames.assembler.domain.embodiments;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.persistence.Entity;
import javax.persistence.Table;

import ch.qos.logback.classic.Logger;
import enterprises.inwaiders.plames.assembler.domain.build.BuildRequest;
import enterprises.inwaiders.plames.assembler.domain.parts.Compilable;
import enterprises.inwaiders.plames.assembler.domain.parts.HasSettingsLine;
import enterprises.inwaiders.plames.assembler.domain.providers.SrcProvider;
import enterprises.inwaiders.plames.assembler.dto.embodiments.GradleProjectDto;

@Entity(name = "GradleProject")
@Table(name = "gradle_projects")
public class GradleProject extends Embodiment<SrcProvider<?>, GradleProjectDto> implements HasSettingsLine, Compilable {

	public void load(BuildRequest request) throws Exception {
	
		File rootFolder = request.getRootFolder();
		
		File projectFolder = new File(rootFolder, name+"\\");
		
		provider.load(request, projectFolder);
	}
	
	@Override
	public void prepareToCompile(BuildRequest request) throws Exception {
		
		useDeployGradleFileIfExist(request);
	}
	
	public void useDeployGradleFileIfExist(BuildRequest request) throws IOException {
		
		Logger logger = request.getLogger();
		
		File rootFolder = request.getRootFolder();
		
		File projectFolder = new File(rootFolder, name+"\\");
		
		File deployFile = new File(projectFolder, "deploy.gradle");
		
		if(deployFile.exists()) {
		
			logger.info("Use deploy.gradle file instead build.gradle for project: "+name);
			Files.copy(deployFile.toPath(), new File(projectFolder, "build.gradle").toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	public File getJarFile(BuildRequest request) {
		
		File rootFolder = request.getRootFolder();
		
		File libs = new File(rootFolder, name+"\\build\\libs\\");
		
		File jar = null;
		
		for(File suspect : libs.listFiles()) {
				
			if(suspect.getName().endsWith(".jar")) {
				
				jar = suspect;
				break;
			}
		}
		
		return jar;
	}
	
	@Override
	public GradleProjectDto toDto() {
		
		GradleProjectDto dto = new GradleProjectDto();
			this.toDto(dto);
			
		return dto;
	}
	
	public void toDto(GradleProjectDto dto) {
		
		super.toDto(dto);
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
