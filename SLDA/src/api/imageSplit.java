package api;

import java.io.IOException;
import java.util.List;

/*
 * split an image into chunks
 */
public interface imageSplit {
	List<Double[]> split(String name, int row, int col) throws IOException;
	List<Double[]> splitFolder(String imageFolderLocation, int row, int col) throws IOException;
}
