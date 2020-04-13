package enterprises.inwaiders.plames.assembler.domain.embodiments;

import java.io.File;

import javax.persistence.Entity;
import javax.persistence.Table;

import enterprises.inwaiders.plames.assembler.domain.build.BuildRequest;
import enterprises.inwaiders.plames.assembler.dto.embodiments.EmbodimentDto;

@Entity(name = "Artifact")
@Table(name = "artifacts")
public class Artifact extends Embodiment {

	@Override
	public void load(BuildRequest request) throws Exception {
		
	}
	
	public File getJarFile(BuildRequest request) {
		
		return null;
	}
	
	@Override
	public String getGradleDependencyLine() {
		
		return null;
	}

	@Override
	public EmbodimentDto toDto() {
	
		return null; //TODO
	}
}
