package hr.heisenbug.worxapp;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static spark.Spark.post;

/**
 * Created by mathabaws on 5/23/15.
 */
public class ServerUploadResource {

    private static final String API_CONTEXT = "/api/v1";

    private final ServerUploadService serverUploadService;

    public ServerUploadResource(ServerUploadService serverUploadService) {
        this.serverUploadService = serverUploadService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        post(API_CONTEXT + "/uploadFile/:id", "application/json", (request, response) ->
                serverUploadService.uploadFile(request.params(":id"),request), new JsonTransformer());
    }

}

/*
* DUMMY CODE FOR UPLOAD TESTING TODO delete this
*  //set server directory to upload
            MultipartConfigElement multipartConfigElement =
                    new MultipartConfigElement(UPLOAD_FOLDER);
            request.raw().setAttribute("org.eclipse.multipartConfig", multipartConfigElement);

            try {
                //get file
                Part file = request.raw().getPart("uploadFile"); //ime upload forme.
                //filename
                String fName = file.getName();
                System.out.println("Filename: " + fName);

                Part uploadedFile = request.raw().getPart("uploadFile");
                Path outputFile = Paths.get(UPLOAD_FOLDER + fName);

                try(final InputStream in = uploadedFile.getInputStream()){
                    Files.copy(in,outputFile);
                    uploadedFile.delete();
                }
                multipartConfigElement = null;
                uploadedFile = null;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServletException e) {
                e.printStackTrace();
            }
*
*
*
* */
