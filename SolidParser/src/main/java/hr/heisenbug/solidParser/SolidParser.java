package hr.heisenbug.solidParser;

import org.apache.poi.poifs.filesystem.*;
import java.io.*;

/**
 * Created by Viktor on 24/05/2015.
 */
public class SolidParser {

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        System.out.println("SolidParser v0.1");
       /* SoldFileOpen sfo = new SoldFileOpen("C:\\Vrotor s utorom1.SLDASM");
        sfo.openFile();*/
        String solidFileName = "G:\\Vrotor s utorom1.SLDASM";
        String solidName = "Vrotor s utorom1.SLDASM";
        String solidPreviewStream = "PreviewPNG";
        String solidHeaderStream = "Header2";
        //List<String> solidEntries = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(solidFileName);
            POIFSFileSystem pfs = new POIFSFileSystem(fis);
            DirectoryEntry root = pfs.getRoot();
            System.out.println("Found Root");
            for(Entry entry : root){
                System.out.println("Found Entry: " + entry.getName());
                //solidEntries.add(entry.getName());
                if(entry instanceof DirectoryEntry){
                    //this is directory
                    //recurse into directory
                    System.out.println("Entry is directory: " + entry.getName());
                } else if(entry instanceof DocumentEntry){
                    //entry is document/stream and can be read
                    System.out.println("Entry is document/stream: " + entry.getName());
                    if(entry.getName().equals(solidPreviewStream)) {
                        System.out.println("SUCCESS: Preview stream found!");
                        //save preview stream to png file
                        DocumentInputStream stream = new DocumentInputStream((DocumentEntry) entry);
                        byte[] content = new byte[stream.available()];
                        stream.read(content);
                        stream.close();
                        String[] nameBase = solidName.split("\\.");
                        FileOutputStream fos = new FileOutputStream("G:\\"+nameBase[0]+"_PreviewPNG.png");
                        try {
                            fos.write(content);
                        }
                        finally {
                            fos.close();
                        }
                    }
                    if(entry.getName().equals(solidHeaderStream)){
                        System.out.println("SUCCESS: Header stream found!");
                        //save header2 file data
                        //DocumentInputStream stream = new DocumentInputStream((DocumentEntry) entry);
                        DocumentInputStream stream = new DocumentInputStream((DocumentEntry) entry);
                        byte[] content = new byte[stream.available()];
                        stream.read(content);
                        stream.close();

                        System.out.println("content: " + " (" + content.length + ") :");
                        System.out.printf("0x%02X", content[0]);
                        System.out.printf("0x%02X", content[1]);
                        System.out.printf("0x%02X", content[2]);
                        System.out.printf("0x%02X", content[3]);

                        String header2Content = new String(content);
                        System.out.println(header2Content);

                      /*  if(header2Content.contains("moCStringHandle")){
                            int start = header2Content.indexOf("moCStringHandle");
                            int end = header2Content.length();
                            System.out.println("start: " + start + " / end: " +end);
                            String partData = header2Content.substring(start, end);
                            System.out.println(partData);

                            List<String> partList = new ArrayList<String>();
                            String keyword = ":";
                            int indexStart = partData.indexOf(keyword);
                            int indexEnd;
                            while (indexStart >=0){
                                System.out.println("Index : " + indexStart);
                                System.out.println(indexStart-2);
                                System.out.println(partData.length());
                                String tmp = partData.substring(indexStart-2, partData.indexOf("S\\sL\\sD\\sP\\sR\\sT"));
                                partList.add(tmp);

                            }
                            System.out.println("Part List: ");
                            for(String p : partList){
                                System.out.println(p);
                            }
                        }*/


                        String[] nameBase = solidName.split("\\.");
                        FileOutputStream fos = new FileOutputStream("G:\\"+nameBase[0]+"_Header2");
                        try {
                            fos.write(content);
                        }
                        finally {
                            fos.close();
                        }
                    }
                } else{
                    // currently, either an Entry is a DirectoryEntry or a DocumentEntry,
                    // but in the future, there may be other entry subinterfaces. The
                    // internal data structure certainly allows for a lot more entry types.
                    System.out.println("Entry is something: " + entry.getName());
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
