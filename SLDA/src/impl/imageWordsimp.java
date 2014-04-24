package impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import api.imageWords;

public class imageWordsimp implements imageWords{
	List<String> names = new ArrayList<String>();
	
	/*
	 * Assign the words to images based on the dictionary
	 * 
	 * input: 
	 *     String: image folder location
	 *     List<Double[]>: clustering centroids
	 * output:
	 *     List<Double[]>: list of words for the images in the folder
	 */
	public List<Integer[]> matchWords(String imageFolderLocation, List<Double[]> centroids, List<List<Double[]>> features) throws IOException {
		
		File folder = new File(imageFolderLocation);
		File[] files = folder.listFiles();
		List<Integer[]> list = new ArrayList<Integer[]>();
		int count = 0;
		
		for (File file : files) {
		    if (file.isFile() && !file.getName().equals(".DS_Store")) {
		        String name = imageFolderLocation + file.getName();
		        if(name.substring(name.length() - 4).equals(".jpg")) {
		            BufferedWriter writer = null;
		            try {
		        	    writer = new BufferedWriter(new FileWriter(name + "words.txt"));
		        	    Integer[] num = match(centroids, features.get(count++));
		        	    for(int i = 0; i < num.length; i++)
		        		    writer.write(num[i] + "\n");
		        	    list.add(num);
		        	    names.add(name);
		            } catch(IOException e) {
		        	    e.printStackTrace();
		            }
		            writer.close();
		        }
		    }
		}
		return list;
	}
	public Integer[] match(List<Double[]> centroids, List<Double[]> sublist) throws IOException {
		System.out.println("match: " + sublist.size());
		Integer[] list = new Integer[sublist.size()];
		for(int i = 0; i < sublist.size(); i++) {
			Double[] d = sublist.get(i);
			double min = Double.MAX_VALUE;
			int clusterk = 0;
			for(int j = 0; j < centroids.size(); j++) {
				double distance = KMeans.euclideanDistance(d, centroids.get(j));
				if(distance < min) {
					min = distance;
					clusterk = j;
				}
			}
			list[i] = clusterk;
		}
		return list;
	}
	//get the file names
	public List<String> getNames(String imageFolderLocation, List<Double[]> centroids, List<List<Double[]>> features) throws IOException {
		matchWords(imageFolderLocation, centroids, features);
		return names;
	}
}
