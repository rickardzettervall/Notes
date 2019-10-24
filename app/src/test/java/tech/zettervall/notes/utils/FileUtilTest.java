package tech.zettervall.notes.utils;

import com.ibm.icu.impl.Assert;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * These tests require some initial setup, create the files and directories so they can
 * be copied correctly, and change paths so they represent your local directory tree.
 */
public class FileUtilTest {

    /**
     * Check that files are copied correctly.
     */
    @Test
    public void copyFile() {
        File source = new File("/home/zet/x.txt");
        File destination = new File("/home/zet/y.txt");

        try {
            FileUtil.copyFile(source, destination);
        } catch (IOException e) {
            Assert.fail(e);
        }
    }

    /**
     * Check that directory contents are copied correctly.
     */
    @Test
    public void copyDirectoryContent() {
        String sourceDir = "/home/zet/dir1";
        String destinationDir = "/home/zet/dir2";

        boolean result = FileUtil.copyDirectoryContent(sourceDir, destinationDir);
        assertTrue(result);
    }
}
