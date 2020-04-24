package enterprises.inwaiders.plames.assembler.domain.providers.git;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import enterprises.inwaiders.plames.assembler.dto.providers.git.GithubTokenDto;

@Entity(name = "GithubToken")
@Table(name = "github_tokens")
public class GithubToken extends GitCredential<GithubTokenDto> {

	@Column(name = "token")
	private String token = null;
	
	public GithubToken() {}
	
	public GithubToken(String token) {
		
		this.token = token;
	}
	
	public String getDisplayId() {
		
		if(token.length() > 8) {
			
			return token.substring(0, 4)+"..."+token.substring(token.length()-4);
		}
		else {
			
			return token; //TODO: Security exception!
		}
	}
	
	public void loadFromDto(GithubTokenDto dto) {
		
		this.token = dto.token;
	}
	
	public GithubTokenDto toDto() {
		
		GithubTokenDto dto = new GithubTokenDto();
			this.toDto(dto);
			
		return dto;
	}
	
	public void toDto(GithubTokenDto dto) {
		
		super.toDto(dto);
		
		dto.token = this.token;
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
	
	public static GithubToken create(String txt) {

		GithubToken token = new GithubToken(txt);
		
		token = repository.save(token);
		
		return token;
	}
}
