package edu.berkeley.cs160.ShengyunZhou.prog3.helper;

import java.util.Arrays;
import java.util.List;
 
public class AppConstant {
	// Request Code
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
 
    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;
 
    // Gridview image padding
    public static final int GRID_PADDING = 8; // in dp
    
    public static final double MIN_DISTANCE = 0.1;
 
    // SD card image directory
    public static final String PHOTO_ALBUM = "GetAwayCam";
 
    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg",
            "png");
}