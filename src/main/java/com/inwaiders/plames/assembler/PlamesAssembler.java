package com.inwaiders.plames.assembler;

import java.io.File;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.servlet.ApplicationContextRequestMatcher;

import com.inwaiders.plames.assembler.domain.CompileRequest;
import com.inwaiders.plames.assembler.domain.dependencies.ProjectDependency;
import com.inwaiders.plames.assembler.domain.git.GithubToken;

import ch.qos.logback.classic.Logger;

@SpringBootApplication
public class PlamesAssembler {

	public static MainConfig CONFIG = null;
	
	public static void main(String[] args) {
		
		SpringApplication.run(PlamesAssembler.class, args);
		
		File testFolder = new File("./test");

		//new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")
		
		Logger logger = (Logger) LoggerFactory.getLogger("PlamesAssembler");
				
		logger.setAdditive(false);

		logger.addAppender(new TestAppender(logger.getLoggerContext()));
		
		CompileRequest request = new CompileRequest(logger, testFolder, new File("./common-prototype"));
			request.addDependency(new ProjectDependency("Plames-API", "https://github.com/Rednoll/Plames-API.git"));
			request.addDependency(new ProjectDependency("Plames-Bootloader", "https://github.com/Rednoll/Plames-Bootloader.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Core", "https://github.com/Rednoll/Plames-Core.git"));
			request.addDependency(new ProjectDependency("Plames-Cross-Chat-Module", "https://github.com/Rednoll/Plames-Cross-Chat-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Discord-Module", "https://github.com/Rednoll/Plames-Discord-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Management-Module", "https://github.com/Rednoll/Plames-Management-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Market-API", "https://github.com/Rednoll/Plames-Market-API.git"));
			request.addDependency(new ProjectDependency("Plames-Market-Module", "https://github.com/Rednoll/Plames-Market-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Minecraft-Module", "https://github.com/Rednoll/Plames-Minecraft-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-PaymentGateway-API", "https://github.com/Rednoll/Plames-PaymentGateway-API.git"));
			request.addDependency(new ProjectDependency("Plames-PaymentGateway-Base", "https://github.com/Rednoll/Plames-PaymentGateway-Base.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Python-API", "https://github.com/Rednoll/Plames-Python-API.git"));
			request.addDependency(new ProjectDependency("Plames-Python-Module", "https://github.com/Rednoll/Plames-Python-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Robokassa-Module", "https://github.com/Rednoll/Plames-Robokassa-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Telegram-Module", "https://github.com/Rednoll/Plames-Telegram-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Vk-Module", "https://github.com/Rednoll/Plames-Vk-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Wallet-API", "https://github.com/Rednoll/Plames-Wallet-API.git"));
			request.addDependency(new ProjectDependency("Plames-Wallet-Module", "https://github.com/Rednoll/Plames-Wallet-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Web-Control-API", "https://github.com/Rednoll/Plames-Web-Control-API.git"));
			request.addDependency(new ProjectDependency("Plames-Web-Control-Module", "https://github.com/Rednoll/Plames-Web-Control-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			request.addDependency(new ProjectDependency("Plames-Whitelist-Module", "https://github.com/Rednoll/Plames-Whitelist-Module.git", new GithubToken("6bba7779579bbf720aea43ac29cd577f4ae19284")));
			
		try {
			
			request.build();
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
	}
}
