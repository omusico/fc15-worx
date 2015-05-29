package hr.heisenbug.worxapp.resources;

import hr.heisenbug.worxapp.JsonTransformer;
import hr.heisenbug.worxapp.services.ServerUploadService;
import hr.heisenbug.worxapp.helpers.SolidFileParser;
import spark.Spark;

import java.util.List;

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
        Spark.post(API_CONTEXT + "/uploadFile/:id", "application/json", (request, response) -> {


            serverUploadService.uploadFile(request.params(":id"), request);
            //parser
            SolidFileParser sfp = new SolidFileParser(serverUploadService.getFilePath());
            List<String> modelDependencies = sfp.parseSolidFile();
            System.out.println("Model dependencies: " + modelDependencies);


            //preview image path
            String previewImagePath = sfp.getFinalPreviewPath();
            System.out.println("Image preview: " + previewImagePath);

            String previewUrl = "http://localhost:8080/img/generated/" + previewImagePath.substring(previewImagePath.lastIndexOf("/") + 1);
            System.out.println(
                    previewUrl
            );

            response.redirect(previewUrl);
            return response;

        }, new JsonTransformer());

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
