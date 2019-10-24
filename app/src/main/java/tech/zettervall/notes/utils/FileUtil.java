package tech.zettervall.notes.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    /**
     * Copy file from source to destination.
     *
     * @param source      Source File
     * @param destination Destination File
     */
    public static void copyFile(File source, File destination) throws IOException {
        InputStream in = new FileInputStream(source);
        try {
            OutputStream out = new FileOutputStream(destination);
            try {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    /**
     * Copy all files from source to destination directory.
     *
     * @param source      Source directory path, with or without ending '/'
     * @param destination Destination directory path, with or without ending '/'
     * @return False if source directory is empty
     */
    public static boolean copyDirectoryContent(String source, String destination) {
        File originDir = new File(source);
        File[] originFiles = originDir.listFiles();

        // Return early if source directory is empty
        if (originFiles == null) {
            return false;
        }

        // Ensure paths are correctly formatted
        source = source.endsWith("/") ? source : source + "/";
        destination = destination.endsWith("/") ? destination : destination + "/";

        // Assign destination paths
        File[] destFiles = new File[originFiles.length];
        for (int i = 0; i < originFiles.length; i++) {
            // Extract filename from path
            StringBuilder fileName = new StringBuilder(originFiles[i].toString());
            int fileNameIndex = fileName.lastIndexOf("/") + 1;
            fileName.delete(0, fileNameIndex);

            // Add destination path
            destFiles[i] = new File(destination + fileName.toString());
        }

        // Copy files
        for (int i = 0; i < originFiles.length; i++) {
            try {
                FileUtil.copyFile(originFiles[i], destFiles[i]);
            } catch (IOException e) {
                Log.w(TAG, "Could not copy file: " + originFiles[i].toString());
            }
        }

        return true;
    }
}
