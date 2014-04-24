package impl;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.IOException;

import api.codebookFactory;

public class codebook implements codebookFactory{
	
	 List<List<Double[]>> features = new ArrayList<List<Double[]>>();
	
	 /*
	  * input:
	  *     String: image folder location
	  * output:
	  *     List<Double[]>: SIFT descriptors
	  *     
	  */
	  public List<Double[]> getSIFT(String imageFolderLocation) {
		  List<Double[]> list = new ArrayList<Double[]> ();
		  File folder = new File(imageFolderLocation);
		  File[] listOfFiles = folder.listFiles();
		  SIFTfeatureImp sift = new SIFTfeatureImp();
		  for (File file : listOfFiles) {
			  if (file.isFile() && file.getName().substring(file.getName().length() - 4).equals(".jpg")) {
				  String name = imageFolderLocation + file.getName();
				  List<Double[]> sublist = sift.siftDescriptors(name);
				  list.addAll(sublist);
				  features.add(sublist);
			  }
		  }
		  return list;
	  }
	  
	  /*
	   * kmeans process to cluster the sift descriptors
	   */
	  public List<Double[]> kmeans(List<Double[]> list) throws IOException {
		  KMeans km = new KMeans(list);
		  return km.run();
	  }
	  public List<List<Double[]>> getFeature() {
		  return features;
	  }
}
