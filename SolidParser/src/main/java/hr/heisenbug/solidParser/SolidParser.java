package hr.heisenbug.solidParser;

import org.apache.poi.poifs.filesystem.*;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Viktor on 24/05/2015.
 */
public class SolidParser {

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
       /* SoldFileOpen sfo = new SoldFileOpen("C:\\Vrotor s utorom1.SLDASM");
        sfo.openFile();*/
        String solidFileName = "G:\\Vrotor s utorom1.SLDASM";
        String solidName = "Vrotor s utorom1.SLDASM";
        String solidPreviewStream = "PreviewPNG";
        String solidHeaderStream = "Header2";

        SolidFileParser sfp = new SolidFileParser(solidFileName);
        sfp.parseSolidFile();

        //List<String> solidEntries = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(solidFileName);
            POIFSFileSystem pfs = new POIFSFileSystem(fis);
            DirectoryEntry root = pfs.getRoot();
            //System.out.println("Found Root");
            for (Entry entry : root) {
                //System.out.println("Found Entry: " + entry.getName());
                //solidEntries.add(entry.getName());
                if (entry instanceof DirectoryEntry) {
                    //this is directory
                    //recurse into directory
                    //System.out.println("Entry is directory: " + entry.getName());
                } else if (entry instanceof DocumentEntry) {
                    //entry is document/stream and can be read
                    //System.out.println("Entry is document/stream: " + entry.getName());
                    if (entry.getName().equals(solidPreviewStream)) {
                        //System.err.println("SUCCESS: Preview stream found!");
                        //save preview stream to png file
                        DocumentInputStream stream = new DocumentInputStream((DocumentEntry) entry);
                        byte[] content = new byte[stream.available()];
                        stream.read(content);
                        stream.close();
                        String[] nameBase = solidName.split("\\.");
                        FileOutputStream fos = new FileOutputStream("G:\\" + nameBase[0] + "_PreviewPNG.png");
                        try {
                            fos.write(content);
                        } finally {
                            fos.close();
                        }
                    }
                    if (entry.getName().equals(solidHeaderStream)) {
                        //System.err.println("SUCCESS: Header stream found!");
                        //save header2 file data
                        //DocumentInputStream stream = new DocumentInputStream((DocumentEntry) entry);
                        DocumentInputStream stream = new DocumentInputStream((DocumentEntry) entry);
                        byte[] content = new byte[stream.available()];
                        stream.read(content);
                        stream.close();

                        String header2Content = new String(content);
                        String header2Content_ = header2Content.replace("\00", "");
                        //System.out.println(header2Content_);

                        String regex = "([^\\\\]+(.SLDPRT|.sldprt|.SLDASM|.sldasm))";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(header2Content_);
                        while (matcher.find()) {
                            //System.out.println(matcher.group());
                        }
                        String[] nameBase = solidName.split("\\.");
                        FileOutputStream fos = new FileOutputStream("G:\\" + nameBase[0] + "_Header2");
                        try {
                            fos.write(content);
                        } finally {
                            fos.close();
                        }
                    }
                } else {
                    // currently, either an Entry is a DirectoryEntry or a DocumentEntry,
                    // but in the future, there may be other entry subinterfaces. The
                    // internal data structure certainly allows for a lot more entry types.
                    //System.out.println("Entry is something: " + entry.getName());
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
