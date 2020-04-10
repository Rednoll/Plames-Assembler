package com.inwaiders.plames.assembler;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inwaiders.plames.assembler.domain.compile.CompileRequest;
import com.inwaiders.plames.assembler.domain.embodiments.GradleProject;
import com.inwaiders.plames.assembler.domain.parts.Part;
import com.inwaiders.plames.assembler.domain.parts.PartBootloader;
import com.inwaiders.plames.assembler.domain.parts.PartCore;
import com.inwaiders.plames.assembler.domain.parts.PartModule;
import com.inwaiders.plames.assembler.domain.providers.git.GitRepository;
import com.inwaiders.plames.assembler.domain.providers.git.GithubToken;
import com.inwaiders.plames.eco.domain.user.User;

import ch.qos.logback.classic.Logger;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CompileTest {
	
	@Test
	@Transactional(propagation = Propagation.REQUIRED)
	public void test() {
		
		GithubToken token = GithubToken.create("6bba7779579bbf720aea43ac29cd577f4ae19284");
		
		User user = User.create();
			user.setNickname("test_user");
			user.addCredential(token);
		
		user.save();
		
		Part api = PartModule.create();
			api.setOwner(user);
			api.setName("Plames API");
			
			GradleProject apiEmb = GradleProject.create();
				apiEmb.setName("Plames-API");
			
				GitRepository apiRepo = GitRepository.create();
					apiRepo.setAddress("https://github.com/Rednoll/Plames-API.git");
					apiRepo.setPublic(true);
					apiRepo.setOwner(user);
					
				apiRepo.save();
				
				apiEmb.setProvider(apiRepo);
	
			apiEmb.save();
			
			api.setEmbodiment(apiEmb);
		
		api.save();
			
		PartBootloader bootloader = PartBootloader.create();
			bootloader.setOwner(user);
			bootloader.setName("Plames Bootloader");
			
			GradleProject bootEmb = GradleProject.create();
				bootEmb.setName("Plames-Bootloader");
			
				GitRepository bootRepo = GitRepository.create();
					bootRepo.setAddress("https://github.com/Rednoll/Plames-Bootloader.git");
					bootRepo.setOwner(user);
					
				bootRepo.save();
				
				bootEmb.setProvider(bootRepo);
	
			bootEmb.save();
			
			bootloader.setEmbodiment(bootEmb);
		
		bootloader.save();
	
		PartCore core = PartCore.create();
			core.setOwner(user);
			core.setName("Plames Core");
			
			GradleProject project = new GradleProject();
				project.setName("Plames-Core");
				
				GitRepository repo = new GitRepository();
					repo.setAddress("https://github.com/Rednoll/Plames-Core.git");
					repo.setPublic(false);
					repo.setOwner(user);
					
				repo.save();
				
				project.setProvider(repo);
				
			project.save();
			
			core.setEmbodiment(project);
		
		core.save();
		
		File testFolder = new File("./factory/test");
		
		CompileRequest request = new CompileRequest((Logger) LoggerFactory.getLogger("PlamesAssembler"), testFolder, new File("./factory/common-prototype"));
			request.setBootloader((PartBootloader) PartBootloader.findByName("Plames Bootloader"));
			request.setCore((PartCore) PartBootloader.findByName("Plames Core"));
			
			request.addModule((PartModule) PartModule.findByName("Plames API"));
		
		try {
			
			request.build();
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
	}
}
