package enterprises.inwaiders.plames.assembler.utils;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

public class ZipUtils {

	public static void addFolder(ZipFile zip, File folder, ZipParameters pars) throws Exception {
	
		addFolder(zip, folder, "", pars);
	}
	
	public static void addFolder(ZipFile zip, File folder, String path, ZipParameters pars) throws Exception {
		
		zip.addFile(folder, pars);
		
		path += folder.getName()+"/";

		pars.setRootFolderInZip(path);
	
		for(File file : folder.listFiles()) {
			
			if(file.isDirectory()) {
				
				addFolder(zip, file, path, pars);
			}
			else {
				
				zip.getProgressMonitor().setState(net.lingala.zip4j.progress.ProgressMonitor.STATE_READY);
				zip.addFile(file, pars);
			}
		}
	}
}
