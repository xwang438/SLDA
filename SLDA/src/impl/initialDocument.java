package impl;

import api.initialDocDesign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * initialize document design, assign keypoints and descriptors based on their location and relative distance to the centroids of the clusters
 *
 */

public class initialDocument implements initialDocDesign{
	List<Integer> [] desc = null;
	List<Double[]> [] keys = null;
	
	 /*
	  * input:
	  *     String: image file location
	  *     List<Double[]> : dictionary for the image set
	  *     int: row number for image segmentation
	  *     int: column number for image segmentation
	  * output:
	  *     update desc and keys with the assigned descriptors and key points in each small document
	  *
	  */
	@SuppressWarnings("unchecked")
	public void initialArray(String imageFileLocation, List<Double[]> dict, int row, int col, List<Double[]> feature) throws IOException {
		int splitNum = row * col;
		desc = new ArrayList[splitNum];
		keys = new ArrayList[splitNum];
		imageSplitImp is = new imageSplitImp();
		is.split(imageFileLocation, row, col);
		imageWordsimp iw = new imageWordsimp();
		Integer[] words = iw.match(feature, dict);
		SIFTfeatureImp sift = new SIFTfeatureImp();
		List<Double[]> keypoints = sift.siftKeyPoints(imageFileLocation);
		for(int m = 0; m < desc.length; m++) {
			desc[m] = new ArrayList<Integer> ();
			keys[m] = new ArrayList<Double[]> ();
			double rowleft = m / row * is.getHeight();
			double colleft = m % col * is.getWidth();
			for(int i = 0; i < keypoints.size(); i++) {
				if(keypoints.get(i)[0] >= rowleft && keypoints.get(i)[0] <= rowleft + is.getHeight() && keypoints.get(i)[1] >= colleft && keypoints.get(i)[1] <= colleft + is.getWidth()) {
					desc[m].add(words[i]);
					keys[m].add(keypoints.get(i));
				}
			}
		}
	}
	public List<Integer> [] getDesc() {
		return desc;
	}
	public List<Double[]> [] getKeys() {
		return keys;
	}
}
