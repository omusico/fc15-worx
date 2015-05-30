package hr.heisenbug.worxapp.services;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import spark.Request;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by mathabaws on 5/23/15.
 */
public class ServerUploadService {

    public static final String UPLOAD_FOLDER = "target/classes/public/img/generated/";
    public String filePath = "";

    public Object uploadFile(String id, Request sparkRequest) {
        HttpServletRequest request = sparkRequest.raw();

// Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

// Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
//parse request
        try {
            List<FileItem> items = upload.parseRequest(request);

            FileItem item = items.get(0);
            try {
                String fileName = item.getName();
                System.out.println("Spremam datoteku: " + fileName + " sa id: " + id);

                if (fileName != null) {
                    File uploadedFile = new File(UPLOAD_FOLDER);
                    File datoteka = new File(UPLOAD_FOLDER+fileName);

                    filePath = datoteka.getPath();

                    if (uploadedFile.mkdirs()) {
                        System.out.println("Folderi kreirani");


                    } else {
                        System.out.println("Failed to create directory " + uploadedFile.getParent());
                    }
                    datoteka.createNewFile();
                    item.write(datoteka);

                    /* MongoDB stuff uncomment if needed
                    GridFS gridfs = new GridFS(db, "recordings");
                    gridfs.remove(id); // remove old recording
                    GridFSInputFile gfsFile = gridfs.createFile(uploadedFile);
                    gfsFile.setFilename(id);
                    gfsFile.save();
                    */
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

        } catch (FileUploadException e) {
            e.printStackTrace();
        }


        return null;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
