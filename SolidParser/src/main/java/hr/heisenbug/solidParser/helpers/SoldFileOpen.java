package hr.heisenbug.solidParser.helpers;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Viktor on 24/05/2015.
 */
public class SoldFileOpen {

    private String solidFileName;


    public SoldFileOpen(String solidFileName){
        System.out.println("INPUT FILE: " + solidFileName);
        this.solidFileName = solidFileName;

    }

    public void openFile(){
        File solidFile = new File(solidFileName);
        try {
            //open SolidWorks file
            FileInputStream fis = null;
            fis = new FileInputStream(solidFileName);
            if(fis == null){
                System.out.println("ERROR1");
            }
            ZipInputStream zis = null;
            zis = new ZipInputStream(fis);
            if(zis == null){
                System.out.println("ERROR2");
            }
            //read entries from SolidWorks file
            ZipEntry zEntry = zis.getNextEntry();
            if (zEntry == null){
                System.out.println("ERROR: empty file or unsupported format!");
            } else {
                while (zEntry != null) {
                    System.out.println("Entry: " + zEntry.getName());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
