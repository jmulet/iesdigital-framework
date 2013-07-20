/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.framework.util;

/*
 * @author Josep
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Unzip {

    /**
     * Unzip it
     *
     * @param zipFile input zip file
     * @param output zip file output folder
     */
    public static void unzip(String inputZipFile, String outputFolder) {
        
        try {

            //create output directory is not exists
            File folder = new File(outputFolder.trim());
            if (!folder.exists()) {
                folder.mkdir();
            }

            ZipFile zipFile = new ZipFile(inputZipFile.trim());
            Enumeration e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze = (ZipEntry) e.nextElement();

                String fileName = ze.getName();

                File newFile = new File(outputFolder + File.separator + fileName);


                //create directories if required.
                newFile.getParentFile().mkdirs();

                //if the entry is directory, leave it. Otherwise extract it.
                if (ze.isDirectory()) {
                    continue;
                } else {
                    //System.out.println("Extracting " + newFile.getAbsoluteFile()+"\n");

                    /*
                     * Get the InputStream for current entry
                     * of the zip file using
                     *
                     * InputStream getInputStream(Entry entry) method.
                     */
                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(ze));

                    int b;
                    byte buffer[] = new byte[1024];

                    /*
                     * read the current entry from the zip file, extract it
                     * and write the extracted file.
                     */
                    FileOutputStream fos = new FileOutputStream(newFile.getAbsoluteFile());
                    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

                    while ((b = bis.read(buffer, 0, 1024)) != -1) {
                        bos.write(buffer, 0, b);
                    }

                    //flush the output stream and close it.
                    bos.flush();
                    bos.close();

                    //close the input stream.
                    bis.close();
                }
            }
        } catch (Exception ex) {
            {
                //System.out.println(ex);
            }
        }
    }
    
    public static void extract(InputStream inputStream, File newFile) throws FileNotFoundException, IOException
    {
        BufferedInputStream bis = new BufferedInputStream(inputStream);

        int b;
        byte buffer[] = new byte[1024];

        /*
         * read the current entry from the zip file, extract it
         * and write the extracted file.
         */
        FileOutputStream fos = new FileOutputStream(newFile.getAbsoluteFile());
        BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

        while ((b = bis.read(buffer, 0, 1024)) != -1) {
            bos.write(buffer, 0, b);
        }

        //flush the output stream and close it.
        bos.flush();
        bos.close();

        //close the input stream.
        bis.close();

    }
}