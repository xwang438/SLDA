package api;
import java.io.IOException;
import java.util.List;

/*
 * Given the image folder location, get the SIFT descriptors for the all the images in the folder
 * Cluster the descriptors to get the centers, which will be the dictionary for the image set
 */

public interface codebookFactory {

	List<Double[]> getSIFT(String imageFolderLocation);
	List<Double[]> kmeans(List<Double[]> list) throws IOException;
	List<List<Double[]>> getFeature();
}
