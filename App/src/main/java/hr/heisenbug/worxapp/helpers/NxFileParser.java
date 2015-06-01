package hr.heisenbug.worxapp.helpers;

import com.google.common.base.CharMatcher;
import org.apache.poi.poifs.filesystem.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Siemens .prt file parser
 * Extracts images/preview and ExternalReferences stream
 * <p/>
 * Created by mathabaws on 01/06/2015.
 */
public class NxFileParser {
    String filePath;
    String solidName;
    String finalPreviewPath;
    final String solidPreviewStream = "preview";
    final String solidHeaderStream = "ExternalReferences";
    final String nxImgDir = "images";
    final String nxReferencesDir = "UG_PART";

    public NxFileParser() {
    }

    public NxFileParser(String fileName) {
        this.filePath = fileName;
    }

    /**
     * Method for parsing Siemens NX .prt file
     *
     * @return
     */
    public List<String> parseSolidFile() {
        System.out.println("NxParser v0.1 by Team Heisenbug IGNITED");
        solidName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        System.out.println("fileName: " + solidName);
        List<String> headerDataList = new ArrayList<String>();

        try {
            FileInputStream fis = new FileInputStream(filePath);
            POIFSFileSystem pfs = new POIFSFileSystem(fis);
            DirectoryEntry root = pfs.getRoot();
            for (Entry entry : root) {
                //find directories
                if (entry instanceof DirectoryEntry) {
                    DirectoryEntry folders = (DirectoryEntry) entry;
                    //if directory is image directory
                    if (folders.getName().equals(nxImgDir)) {
                        //run trough image dir and find image stream
                        for (Entry entry1 : folders) {
                            if (entry1.getName().equals(solidPreviewStream)) {
                                //save preview stream to png file
                                DocumentInputStream stream = new DocumentInputStream((DocumentEntry) entry1);
                                byte[] content = new byte[stream.available()];
                                stream.read(content);
                                stream.close();
                                String[] nameBase = filePath.split("\\.");
                                FileOutputStream fos = new FileOutputStream(nameBase[0] + "_PreviewPNG.jpg");
                                finalPreviewPath = nameBase[0] + "_PreviewPNG.jpg";
                                System.out.println(finalPreviewPath);
                                try {
                                    fos.write(content);
                                } finally {
                                    fos.close();
                                }
                            }
                        }

                    }
                    if (folders.getName().equals(nxReferencesDir)) {
                        //run trough references dir and find reference stream
                        for (Entry entry1 : folders) {
                            if (entry1.getName().equals(solidHeaderStream)) {
                                //save references file data
                                DocumentInputStream stream = new DocumentInputStream((DocumentEntry) entry1);
                                byte[] content = new byte[stream.available()];
                                stream.read(content);
                                stream.close();

                                String header2Content = new String(content);
                                String header2Content_ = header2Content.replace("\00", " ");


                                //header2Content_ = CharMatcher.WHITESPACE.replaceFrom(header2Content_, " ");
                                String regex = "([^\\s]+(.prt|.PRT))";
                                Pattern pattern = Pattern.compile(regex);
                                Matcher matcher = pattern.matcher(header2Content_);
                                if (matcher.groupCount() == 1) {
                                    headerDataList.add(matcher.group(1));
                                } else {
                                    while (matcher.find()) {
                                        headerDataList.add(matcher.group());
                                        //System.out.println("Grupa: "+matcher.group());
                                    }
                                }

                            }
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
        return filePath;
    }

    public void setFileName(String fileName) {
        this.filePath = fileName;
    }

    /**
     * Getter for PreviewPNG image
     *
     * @return
     */
    public String getFinalPreviewPath() {
        return finalPreviewPath;
    }
}
