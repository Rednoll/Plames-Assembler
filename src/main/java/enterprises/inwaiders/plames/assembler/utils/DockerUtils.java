package enterprises.inwaiders.plames.assembler.utils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Image;

import enterprises.inwaiders.plames.PlamesAssembler;

public class DockerUtils {
	
	public static void initDockerImages() throws InterruptedException {
		
		Logger logger = LoggerFactory.getLogger("Docker");
		
		File dockerImagesFolder = new File(PlamesAssembler.CONFIG.dockerPath);
		
		Semaphore semaphore = new Semaphore(dockerImagesFolder.listFiles().length);
			semaphore.acquire(dockerImagesFolder.listFiles().length);
		
		for(File imageFolder : dockerImagesFolder.listFiles()) {
			
			String imageName = imageFolder.getName();
			
			if(!checkImageExistByTag(imageName)) {
				
				Set<String> tags = new HashSet<>();
					tags.add(imageName);
				
				PlamesAssembler.DOCKER_CLIENT.buildImageCmd(imageFolder)
					.withTags(tags)
				.exec(new BuildImageResultCallback() {
					
				    @Override
				    public void onNext(BuildResponseItem item) {
				    	
				    	if(item.isBuildSuccessIndicated()) {
				    		
				    		logger.info("Create image \""+imageName+"\" complete!");
				    		semaphore.release();
				    	}
				    }
				});
			}
			else {
				
				semaphore.release();
			}
		}
		
		semaphore.acquire(dockerImagesFolder.listFiles().length);
	}

	public static boolean checkImageExistByTag(String tag) {
		
		List<Image> images = PlamesAssembler.DOCKER_CLIENT.listImagesCmd().exec();
		
		for(Image image : images) {

			for(String suspectTag : image.getRepoTags()) {
				
				if(suspectTag.startsWith(tag)) {
					
					return true;
				}
			}
		}
		
		return false;
	}
}
