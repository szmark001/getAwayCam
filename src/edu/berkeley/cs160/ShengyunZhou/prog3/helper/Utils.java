package edu.berkeley.cs160.ShengyunZhou.prog3.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
 
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
 
public class Utils {
 
    private Context _context;
 
    // constructor
    public Utils(Context context) {
        this._context = context;
    }
 
    // Reading file paths from SDCard
    public ArrayList<String> getFilePaths() {
        ArrayList<String> filePaths = new ArrayList<String>();
 
        File directory = new File(
                android.os.Environment.getExternalStorageDirectory()
                        + File.separator + AppConstant.PHOTO_ALBUM);
 
        // check for directory
        if (directory.isDirectory()) {
            // getting list of file paths
            File[] listFiles = directory.listFiles();
 
            // Check for count
            if (listFiles.length > 0) {
 
                // loop through all files
                for (int i = 0; i < listFiles.length; i++) {
 
                    // get file path
                    String filePath = listFiles[i].getAbsolutePath();
 
                    // check for supported file extension
                    if (IsSupportedFile(filePath)) {
                        // Add image path to array list
                        filePaths.add(filePath);
                    }
                }
            } else {
                // image directory is empty
                Toast.makeText(
                        _context,
                        AppConstant.PHOTO_ALBUM
                                + " is empty. Please load some images in it !",
                        Toast.LENGTH_LONG).show();
            }
 
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(_context);
            alert.setTitle("Error!");
            alert.setMessage(AppConstant.PHOTO_ALBUM
                    + " has no photos. Start by taking new photos!");
            alert.setPositiveButton("OK", null);
            alert.show();
        }
 
        return filePaths;
    }
 
    // Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());
 
        if (AppConstant.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;
 
    }
 
    /*
     * getting screen width
     */
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
 
        final Point point = new Point();
        display.getSize(point);
        columnWidth = point.x;
        return columnWidth;
    }
    
    public double dmsToDecimal (String dms) {
    	double rtn, dnum, ddenom, mnum, mdenom, snum, sdenom;
    	String tempDms = dms;
    	dnum = Double.parseDouble(tempDms.substring(0, tempDms.indexOf('/')));
    	ddenom = Double.parseDouble(tempDms.substring((tempDms.indexOf('/') + 1), tempDms.indexOf(',')));
    	tempDms = dms.substring(tempDms.indexOf(",") + 1);
    	mnum = Double.parseDouble(tempDms.substring(0, tempDms.indexOf('/')));
    	mdenom = Double.parseDouble(tempDms.substring((tempDms.indexOf('/') + 1), tempDms.indexOf(',')));
    	tempDms = tempDms.substring(tempDms.indexOf(",") + 1);
    	snum = Double.parseDouble(tempDms.substring(0, tempDms.indexOf('/')));
    	sdenom = Double.parseDouble(tempDms.substring((tempDms.indexOf('/') + 1)));
    	tempDms = tempDms.substring(tempDms.indexOf(",") + 1);
    	
    	rtn = dnum/ddenom + mnum/mdenom/60 + snum/sdenom/3600;
    	return rtn;
    }
    
    public String decimalToDms (Double decimal) {
    	String d, m, s, dms;
    	d = String.valueOf((int) (Math.floor(decimal)));
    	decimal = (decimal - Math.floor(decimal)) * 60;
    	m = String.valueOf((int) Math.floor(decimal));
    	decimal = (decimal - Math.floor(decimal)) * 60 * 1000;
    	s = String.valueOf((int) Math.floor(decimal));
    	dms = d + "/1," + m + "/1," + s + "/1000";
    	return dms;
    }
}