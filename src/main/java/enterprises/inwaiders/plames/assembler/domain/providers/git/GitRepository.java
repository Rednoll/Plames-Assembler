package enterprises.inwaiders.plames.assembler.domain.providers.git;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import enterprises.inwaiders.plames.assembler.domain.build.BuildRequest;
import enterprises.inwaiders.plames.assembler.domain.providers.ProviderBase;
import enterprises.inwaiders.plames.assembler.domain.providers.SrcProvider;
import enterprises.inwaiders.plames.assembler.dto.providers.git.GitRepositoryDto;
import enterprises.inwaiders.plames.assembler.utils.LoggerUtils;
import enterprises.inwaiders.plames.eco.domain.user.User;

@Entity(name = "GitRepository")
@Table(name = "git_repositories")
public class GitRepository extends ProviderBase<GitRepositoryDto> implements SrcProvider<GitRepositoryDto> {

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
	
	public void loadFromDto(GitRepositoryDto dto) {
		
		super.loadFromDto(dto);
		
		this.isPublic = dto.isPublic;
		this.address = dto.address;
		this.owner = User.findById(owner.getId());
	}
	
	public GitRepositoryDto toDto() {
		
		GitRepositoryDto dto = new GitRepositoryDto();
			this.toDto(dto);
		
		return dto;
	}
	
	public void toDto(GitRepositoryDto dto) {
		
		super.toDto(dto);
		
		dto.address = this.getAddress();
		dto.isPublic = this.isPublic();	
		dto.owner = this.getOwner().toDto();
	}
	
	@Override
	public void load(BuildRequest request, File destination) throws Exception {

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
		
		git.close();
		
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
