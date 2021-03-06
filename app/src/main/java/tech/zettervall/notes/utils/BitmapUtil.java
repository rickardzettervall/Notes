package tech.zettervall.notes.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public abstract class BitmapUtil {

    /**
     * Retrieves Bitmap from path.
     */
    public static Bitmap getBitmap(String absolutePath) {
        File imgFile = new File(absolutePath);
        return imgFile.exists() ? BitmapFactory.decodeFile(imgFile.getAbsolutePath()) : null;
    }

    /**
     * Check if File exists.
     */
    public static boolean doesFileExist(String absolutePath) {
        if(absolutePath == null) {
            return false;
        }
        File imgFile = new File(absolutePath);
        return imgFile.exists();
    }
}
