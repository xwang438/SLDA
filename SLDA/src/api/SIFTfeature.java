package api;

import java.awt.image.BufferedImage;
import java.util.List;

/*
 * Given the image, get the sift descriptors and keypoints
 */
public interface SIFTfeature {
	List<Double[]>[] processSIFT(String fileLocation);
	List<Double[]> siftDescriptors(String fileLocation);
	List<Double[]> siftKeyPoints(String fileLocation);
	int[] toPixelsTab(BufferedImage image);
}
