package com.inwaiders.plames.assembler.domain.git;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GithubToken extends Credentials {

	private String token = null;
	
	public GithubToken() {}
	
	public GithubToken(String token) {
		
		this.token = token;
	}
	
	public UsernamePasswordCredentialsProvider getProvider() {
		
		return new UsernamePasswordCredentialsProvider(token, "");
	}
}
