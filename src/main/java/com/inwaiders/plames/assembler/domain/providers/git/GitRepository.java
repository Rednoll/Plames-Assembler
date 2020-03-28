package com.inwaiders.plames.assembler.domain.providers.git;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.slf4j.LoggerFactory;

import com.inwaiders.plames.assembler.domain.CompileRequest;
import com.inwaiders.plames.assembler.domain.providers.ProviderBase;
import com.inwaiders.plames.assembler.domain.providers.SrcProvider;
import com.inwaiders.plames.assembler.utils.LoggerUtils;
import com.inwaiders.plames.eco.domain.user.User;

import ch.qos.logback.classic.Logger;

@Entity(name = "GitRepository")
@Table(name = "git_repositories")
public class GitRepository extends ProviderBase implements SrcProvider {

	@Column(name = "is_public")
	private boolean isPublic = false;
	
	@Column(name = "address")
	private String address = null;
	
	@JoinColumn(name = "credential_id")
	@OneToOne(targetEntity = GitCredential.class)
	private GitCredential credential = null;
	
	@JoinColumn(name = "owner_id")
	@OneToOne(targetEntity = User.class)
	private User owner = null;
	
	@Override
	public void load(CompileRequest request, File destination) throws Exception {
	
		Logger logger = (Logger) LoggerFactory.getLogger("Git");
		
		LoggerUtils.setRoot(logger, request.getLogger());
		
		CloneCommand cloneCommand = Git.cloneRepository();
			cloneCommand.setURI(address);
			cloneCommand.setDirectory(destination);
			
			if(!isPublic) {
				
				if(credential != null) {
					
					cloneCommand.setCredentialsProvider(credential.getProvider());
				}
				else {
					
					GitCredential ownerCredential = owner.getCredentials().getCredential(GitCredential.class);
					
					cloneCommand.setCredentialsProvider(ownerCredential.getProvider());
				}
			}
		
		logger.info("Start clone("+Thread.currentThread().getId()+") repository "+address+"...");
		
		Git git = cloneCommand.call();
	
		logger.info("Clone("+Thread.currentThread().getId()+") complete!");
	}
	
	public void setPublic(boolean isPublic) {
		
		this.isPublic = isPublic;
	}
	
	public boolean isPublic() {
		
		return this.isPublic;
	}
	
	public void setOwner(User owner) {
		
		this.owner = owner;
	}
	
	public User getOwner() {
		
		return this.owner;
	}
	
	public void setAddress(String address) {
		
		this.address = address;
	}
	
	public String getAddress() {
		
		return this.address;
	}
	
	public void setCredential(GitCredential cred) {
		
		this.credential = cred;
	}
	
	public GitCredential getCredential() {
		
		return this.credential;
	}
	
	public static GitRepository create() {
		
		GitRepository rep = new GitRepository();
		
		rep = repository.save(rep);
		
		return rep;
	}
}
