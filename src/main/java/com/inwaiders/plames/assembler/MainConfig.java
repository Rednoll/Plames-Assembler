package com.inwaiders.plames.assembler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:main.properties")
public class MainConfig {

	@Value("${git.loaders.count}")
	public int gitLoadersCount;
}