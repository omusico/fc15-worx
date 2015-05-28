package hr.heisenbug.worxapp.helpers;

import com.sun.org.apache.xpath.internal.operations.*;
import org.apache.poi.poifs.filesystem.*;

import java.io.*;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SolidWorks .sldprt and .sldasm file parser
 * Extracts PreviewPNG and Header2 stream
 *
 * Created by Viktor on 27/05/2015.
 */
public class SolidFileParser {
    String fileName;
    String solidName;
    String finalPreviewPath;
    final String solidPreviewStream = "PreviewPNG";
    final String solidHeaderStream = "Header2";

    public SolidFileParser() {
    }

    public SolidFileParser(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Method for parsing SolidWorks .sldasm or .sldprt file
     *
     * @return
     */
    public List<String> parseSolidFile() {
        System.out.println("SolidParser v0.1 by Team Heisenbug IGNITED");
        solidName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
        System.out.println("solidName: " + solidName);
        List<String> headerDataList = new ArrayList<String>();

        try {
            FileInputStream fis = new FileInputStream(fileName);
            POIFSFileSystem pfs = new POIFSFileSystem(fis);
            DirectoryEntry root = pfs.getRoot();
            for (Entry entry : root) {
                if (entry instanceof DocumentEntry) {
                    //entry is document/stream and can be read
                    if (entry.getName().equals(solidPreviewStream)) {
                        //save preview stream to png file
                        DocumentInputStream stream = new DocumentInputStream((DocumentEntry) entry);
                        byte[] content = new byte[stream.available()];
                        stream.read(content);
                        stream.close();
                        String[] nameBase = fileName.split("\\.");
                        FileOutputStream fos = new FileOutputStream(nameBase[0] + "_PreviewPNG.png");
                        finalPreviewPath = nameBase[0] + "_PreviewPNG.png";
                        try {
                            fos.write(content);
                        } finally {
                            fos.close();
                        }
                    }
                    if (entry.getName().equals(solidHeaderStream)) {
                        //save header2 file data
                        DocumentInputStream stream = new DocumentInputStream((DocumentEntry) entry);
                        byte[] content = new byte[stream.available()];
                        stream.read(content);
                        stream.close();

                        String header2Content = new String(content);
                        String header2Content_ = header2Content.replace("\00", "");

                        String regex = "([^\\\\]+(.SLDPRT|.sldprt|.SLDASM|.sldasm))";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(header2Content_);
                        while (matcher.find()) {
                            headerDataList.add(matcher.group());
                        }
                        String[] nameBase = solidName.split("\\.");
                        FileOutputStream fos = new FileOutputStream("G:\\" + nameBase[0] + "_Header2");
                        try {
                            fos.write(content);
                        } finally {
                            fos.close();
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return headerDataList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Getter for PreviewPNG image
     * @return
     */
    public String getFinalPreviewPath() {
        return finalPreviewPath;
    }
}
