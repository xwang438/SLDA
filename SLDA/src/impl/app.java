package impl;

import java.io.IOException;
import java.util.List;
public class app {
	public static void main(String[] args) throws IOException {
		String imageFolderLocation = "/Users/xwang/Documents/workspace/SLDA/imagesmall/";
		imageSplitImp split = new imageSplitImp();
		split.splitFolder(imageFolderLocation, 4, 4);
		String imageSplitLocation = "/Users/xwang/Documents/workspace/SLDA/imagesplit/";
		visualPage vp = new visualPage(imageSplitLocation);
	}
}
