package impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageIO;

import mpi.cbg.fly.Feature;
import mpi.cbg.fly.SIFT;

import api.SIFTfeature;

public class SIFTfeatureImp implements SIFTfeature {
	 /*
	  * input:
	  *     String: image file location
	  * output:
	  *     List<Double[]>[]: sift descriptors for the given image, sift keypoints for the given image
	  *
	  */
	  public List<Double[]>[] processSIFT(String fileLocation) {
	        
	      @SuppressWarnings("unchecked")
		  List<Double[]>[] list = new ArrayList [2];
	      for(int i=0;i<list.length;i++)
	    	  list[i] = new ArrayList<Double[]>();
	      try {
	    	  
	       	  BufferedImage image = ImageIO.read(new File(fileLocation));
	          int pixels[] = toPixelsTab(image);
	          System.out.println(fileLocation);
	 
	          Vector<Feature> vector = SIFT.getFeatures(image.getWidth(), image.getHeight(), pixels);
              Feature[] features = vector.toArray(new Feature[vector.size()]);
              ArrayList<Double[]> subdes = new ArrayList<Double[]>();
              ArrayList<Double[]> subpoint = new ArrayList<Double[]>();
              for(int i = 0; i < features.length; i++) {
            	  float[] descriptor = features[i].descriptor;
            	  float[] keypoint = features[i].location;
                  Double[] des = new Double[descriptor.length];
                  for(int j = 0; j < descriptor.length; j++) 
                	  des[j] = (double)descriptor[j];
                  subdes.add(des);
                  
                  Double[] key = new Double[keypoint.length];
                  for(int j = 0; j < key.length; j++) 
                	  key[j] = (double)keypoint[j];
                  subpoint.add(key);
              }
              list[0] = subdes;
              list[1] = subpoint;
           } catch (Exception e) {
	          e.printStackTrace();
	       } catch (OutOfMemoryError e) {
	       }
	       return list;
	  }
	  /*
	   * get the SIFT descriptors and key points
	   */
	  public List<Double[]> siftDescriptors(String fileLocation) {
		  return processSIFT(fileLocation)[0];
	  }
	  public List<Double[]> siftKeyPoints(String fileLocation) {
		  return processSIFT(fileLocation)[1];
	  }
	  
	  /*
	   * input:
	   *     BufferedImage: image
	   *     output:
	   *     int[]: pixel tab for the image
	   */
	  public int[] toPixelsTab(BufferedImage image) {
	      int width = image.getWidth();
	      int height = image.getHeight();
	      int[] pixels = new int[width * height];
	      // copy pixels of picture into the tab
	      image.getRGB(0, 0,width, height, pixels, 0, width);
	      return pixels;
	  }
}
