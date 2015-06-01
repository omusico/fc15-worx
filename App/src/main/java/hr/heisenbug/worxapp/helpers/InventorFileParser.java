package hr.heisenbug.worxapp.helpers;

import org.apache.poi.poifs.filesystem.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Inventor .iam and .ipt file parser.
 * Extracts data from UFRxDoc stream
 *
 * Created by mathabaws on 31/05/2015.
 */
public class InventorFileParser {
    
    String filePath;
    //solidname = name of file
    String fileName;
    String finalPreviewPath;
    final String fileHeaderStream = "UFRxDoc";

    public InventorFileParser() {
    }

    public InventorFileParser(String fileName) {
        this.filePath = fileName;
    }

    /**
     * Method for parsing Inventor .iam or .ipt file
     *
     * @return
     */
    public List<String> parseSolidFile() {
        System.out.println("InventorParser v0.1 by Team Heisenbug IGNITED");
        fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        System.out.println("InventorFile: " + fileName);
        List<String> headerDataList = new ArrayList<String>();

        try {
            FileInputStream fis = new FileInputStream(filePath);
            POIFSFileSystem pfs = new POIFSFileSystem(fis);
            DirectoryEntry root = pfs.getRoot();
            for (Entry entry : root) {
                if (entry instanceof DocumentEntry) {
                    //entry is document/stream and can be read

                    if (entry.getName().equals(fileHeaderStream)) {
                        //save header2 file data
                        DocumentInputStream stream = new DocumentInputStream((DocumentEntry) entry);
                        byte[] content = new byte[stream.available()];
                        stream.read(content);
                        stream.close();

                        String header2Content = new String(content);
                        String header2Content_ = header2Content.replace("\00", "");

                        //find all child with .iam or .ipt extension
                        String regex = "([^\\\\]+(.IAM|.iam|.IPT|.ipt))";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(header2Content_);
                        while (matcher.find()) {
                            headerDataList.add(matcher.group());
                        }
                       /* String[] nameBase = solidName.split("\\.");
                        FileOutputStream fos = new FileOutputStream("G:\\" + nameBase[0] + "_Header2");
                        try {
                            fos.write(content);
                        } finally {
                            fos.close();
                        }*/
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String fileName) {
        this.filePath = fileName;
    }

    /**
     * Getter for PreviewPNG image
     * @return
     */
    public String getFinalPreviewPath() {
        return "";
    }
}
