package com.inwaiders.plames;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.inwaiders.plames.assembler.MainConfig;
import com.inwaiders.plames.assembler.domain.embodiments.GradleProject;
import com.inwaiders.plames.assembler.domain.parts.Part;
import com.inwaiders.plames.assembler.domain.parts.PartBootloader;
import com.inwaiders.plames.assembler.domain.parts.PartModule;
import com.inwaiders.plames.assembler.domain.providers.git.GitRepository;
import com.inwaiders.plames.assembler.domain.providers.git.GithubToken;
import com.inwaiders.plames.eco.domain.user.User;

@SpringBootApplication
public class PlamesAssembler {

	public static MainConfig CONFIG = null;
	
	public static void main(String[] args) {
		
		SpringApplication.run(PlamesAssembler.class, args);
		
		File testFolder = new File("./test");

		//new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")
		
		GithubToken token = GithubToken.create();
			token.setToken("6bba7779579bbf720aea43ac29cd577f4ae19284");
		
		token.save();
			
		User user = User.create();
			user.setNickname("test_user");
			user.addCredential(token);
		
		user.save();
			
		Part api = PartModule.create();
			api.setOwner(user);
		
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
			
		Part bootloader = PartBootloader.create();
			bootloader.setOwner(user);
		
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
			
		/*
		User user = new User();
		
		Part part = new PartBootloader();
		
		GradleProject project = new GradleProject();
		
		part.setEmbodiment(project);
		
		GitRepository repo = new GitRepository();
			repo.setCredential(new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284"));
			repo.setAddress("https://github.com/Rednoll/Plames-Bootloader.git");
			
		project.setProvider(repo);
		*/
		
		/*
		Logger logger = (Logger) LoggerFactory.getLogger("PlamesAssembler");
				
		logger.setAdditive(false);

		logger.addAppender(new TestAppender(logger.getLoggerContext()));
		
		CompileRequest request = new CompileRequest(logger, testFolder, new File("./common-prototype"));
			request.addPart(new ProjectPart("Plames-API", "https://github.com/Rednoll/Plames-API.git"));
			request.addPart(new ProjectPart("Plames-Bootloader", "https://github.com/Rednoll/Plames-Bootloader.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Core", "https://github.com/Rednoll/Plames-Core.git"));
			request.addPart(new ProjectPart("Plames-Cross-Chat-Module", "https://github.com/Rednoll/Plames-Cross-Chat-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Discord-Module", "https://github.com/Rednoll/Plames-Discord-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Management-Module", "https://github.com/Rednoll/Plames-Management-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Market-API", "https://github.com/Rednoll/Plames-Market-API.git"));
			request.addPart(new ProjectPart("Plames-Market-Module", "https://github.com/Rednoll/Plames-Market-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Minecraft-Module", "https://github.com/Rednoll/Plames-Minecraft-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-PaymentGateway-API", "https://github.com/Rednoll/Plames-PaymentGateway-API.git"));
			request.addPart(new ProjectPart("Plames-PaymentGateway-Base", "https://github.com/Rednoll/Plames-PaymentGateway-Base.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Python-API", "https://github.com/Rednoll/Plames-Python-API.git"));
			request.addPart(new ProjectPart("Plames-Python-Module", "https://github.com/Rednoll/Plames-Python-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Robokassa-Module", "https://github.com/Rednoll/Plames-Robokassa-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Telegram-Module", "https://github.com/Rednoll/Plames-Telegram-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Vk-Module", "https://github.com/Rednoll/Plames-Vk-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Wallet-API", "https://github.com/Rednoll/Plames-Wallet-API.git"));
			request.addPart(new ProjectPart("Plames-Wallet-Module", "https://github.com/Rednoll/Plames-Wallet-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Web-Control-API", "https://github.com/Rednoll/Plames-Web-Control-API.git"));
			request.addPart(new ProjectPart("Plames-Web-Control-Module", "https://github.com/Rednoll/Plames-Web-Control-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addPart(new ProjectPart("Plames-Whitelist-Module", "https://github.com/Rednoll/Plames-Whitelist-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			
		try {
			
			request.build();
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
		*/
	}
}
