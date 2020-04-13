package enterprises.inwaiders.plames.assembler.domain.providers.git;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import enterprises.inwaiders.plames.assembler.dto.providers.git.GitCredentialDto;
import enterprises.inwaiders.plames.eco.domain.credential.Credential;

@Entity(name = "GitCredential")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class GitCredential<DTO extends GitCredentialDto> extends Credential<DTO> {

	public abstract UsernamePasswordCredentialsProvider getProvider();
}
