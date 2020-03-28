package com.inwaiders.plames.assembler.domain.providers.git;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

@Entity(name = "GithubToken")
@Table(name = "github_tokens")
public class GithubToken extends GitCredential {

	@Column(name = "token")
	private String token = null;
	
	public GithubToken() {}
	
	public GithubToken(String token) {
		
		this.token = token;
	}
	
	public void setToken(String token) {
		
		this.token = token;
	}
	
	public String getToken() {
		
		return this.token;
	}
	
	public UsernamePasswordCredentialsProvider getProvider() {
		
		return new UsernamePasswordCredentialsProvider(token, "");
	}
	
	public static GithubToken create() {
		
		GithubToken token = new GithubToken();
		
		token = repository.save(token);
		
		return token;
	}
}
