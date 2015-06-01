package hr.heisenbug.worxapp.resources;

import com.google.gson.*;
import hr.heisenbug.worxapp.JsonTransformer;
import hr.heisenbug.worxapp.helpers.*;
import hr.heisenbug.worxapp.models.Bucket;
import hr.heisenbug.worxapp.models.Model;
import hr.heisenbug.worxapp.services.BucketService;
import hr.heisenbug.worxapp.services.ModelService;
import hr.heisenbug.worxapp.services.ServerUploadService;
import jdk.nashorn.internal.parser.JSONParser;
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static spark.Spark.post;

/**
 * Created by mathabaws on 5/23/15.
 */
public class ServerUploadResource {

    private static final String API_CONTEXT = "/api/v1";
    private static final String NO_PREVIEW_IMG = "no-preview.jpg";

    private final ServerUploadService serverUploadService;

    public ServerUploadResource(ServerUploadService serverUploadService) {
        this.serverUploadService = serverUploadService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        Spark.post(API_CONTEXT + "/uploadFile/:id", "application/json", (request, response) -> {
            System.out.println("\nInternal upload!\n");

            serverUploadService.uploadFile(request.params(":id"), request);
            //parser
            //parser
            List<String> modelDependencies = new LinkedList<String>();
            String previewImage = "";
            String fileName = "";

            if (serverUploadService.getFilePath().toLowerCase().contains(".sldprt")
                    || serverUploadService.getFilePath().toLowerCase().contains(".sldasm")) {

                SolidFileParser sfp = new SolidFileParser(serverUploadService.getFilePath());
                modelDependencies = sfp.parseSolidFile();
                previewImage = sfp.getFinalPreviewPath();
                fileName = sfp.getFileName();//file path actually

            } else if (serverUploadService.getFilePath().toLowerCase().contains(".iam")
                    || serverUploadService.getFilePath().toLowerCase().contains(".ipt")) {

                InventorFileParser sfp = new InventorFileParser(serverUploadService.getFilePath());
                modelDependencies = sfp.parseSolidFile();
                previewImage = sfp.getFinalPreviewPath();
                fileName = sfp.getFilePath();
            } else if (serverUploadService.getFilePath().toLowerCase().contains(".prt")) {

                NxFileParser sfp = new NxFileParser(serverUploadService.getFilePath());
                modelDependencies = sfp.parseSolidFile();
                previewImage = sfp.getFinalPreviewPath();
                fileName = sfp.getFileName(); //file path actually
            }

            if (previewImage.equals("")) {

                previewImage = "/img/generated/" + NO_PREVIEW_IMG;
                System.out.println("Image is none: " + previewImage);
            }

            String previewUrl = "/img/generated/" + previewImage.substring(previewImage.lastIndexOf("/") + 1);
            System.out.println(
                    previewUrl
            );

            //response.redirect(previewUrl);
            response.header("previewUrl", "/img/generated/" + previewImage.substring(previewImage.lastIndexOf("/") + 1));

            /* TODO: connect bucket with child models
            BucketService bs = new BucketService(StaticData.getDb());
            Bucket bucket = bs.find(request.params(":id"));
            bucket.set
            */
            ModelService ms = new ModelService(StaticData.getDb());

            //Create json definition of model
            //ms.createNewModel();

            return response;

        }, new JsonTransformer());

        Spark.post(API_CONTEXT + "/uploadFileExternal/:id", "application/json", (request, response) -> {
            System.out.println("\nExternal upload!\n");

            serverUploadService.uploadFile(request.params(":id"), request);

            //parser
            List<String> modelDependencies = new LinkedList<String>();
            String previewImage = "";
            String fileName = "";

            if (serverUploadService.getFilePath().toLowerCase().contains(".sldprt")
                    || serverUploadService.getFilePath().toLowerCase().contains(".sldasm")) {

                SolidFileParser sfp = new SolidFileParser(serverUploadService.getFilePath());
                modelDependencies = sfp.parseSolidFile();
                previewImage = sfp.getFinalPreviewPath();
                fileName = sfp.getFileName();

            } else if (serverUploadService.getFilePath().toLowerCase().contains(".iam")
                    || serverUploadService.getFilePath().toLowerCase().contains(".ipt")) {

                InventorFileParser sfp = new InventorFileParser(serverUploadService.getFilePath());
                modelDependencies = sfp.parseSolidFile();
                previewImage = sfp.getFinalPreviewPath();
                fileName = sfp.getFilePath();
            } else if (serverUploadService.getFilePath().toLowerCase().contains(".prt")) {

                NxFileParser sfp = new NxFileParser(serverUploadService.getFilePath());
                modelDependencies = sfp.parseSolidFile();
                previewImage = sfp.getFinalPreviewPath();
                fileName = sfp.getFileName(); //file path actually
            }

            if (previewImage.equals("")) {
                previewImage = "/img/generated/static/" + NO_PREVIEW_IMG;
            }

            System.out.println("Model dependencies: " + modelDependencies);

            //preview image path
            String previewImagePath = previewImage;
            System.out.println("Image preview: " + previewImagePath);

            String previewUrl = "http://localhost:8080/img/generated/" + previewImagePath.substring(previewImagePath.lastIndexOf("/") + 1);
            System.out.println(
                    previewUrl
            );

            //response.redirect(previewUrl);
            response.header("previewUrl", "/img/generated/" + previewImagePath.substring(previewImagePath.lastIndexOf("/") + 1));

            /* TODO: connect bucket with child models*/
            BucketService bs = new BucketService(StaticData.getDb());
            Bucket bucket = bs.find(request.params(":id"));

            //System.out.println("upload file data:\n"+bucket.getBucketKey() + "\n"+sfp.getFileName());
            String resp = "";

            //remove spaces and non alphanumeric chars
            String alternateKey = fileName;
            //parse name from filepath
            alternateKey = alternateKey.substring(alternateKey.lastIndexOf("/") + 1);
            alternateKey = alternateKey.toLowerCase();
            alternateKey = alternateKey.replace(" ", "_");
            alternateKey = alternateKey.replaceAll("[^-_.a-z0-9]", "");
            //get path where file is located
            String oldPath = fileName;
            oldPath = oldPath.substring(0, oldPath.lastIndexOf("/") + 1);
            System.out.println(oldPath);

            //files for rename
            File modelOldName = new File(fileName);
            File modelNewName = new File(oldPath + alternateKey);

            System.out.println("Novo ime modela: " + modelNewName.getPath() + "\nStaro ime modela: " + fileName);
            //renamne them
            modelOldName.renameTo(modelNewName);

            try {
                resp = FileUploader.uploadFile(bucket.getBucketKey(), StaticData.getAuthorizationToken(), modelNewName.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("\n\n" + resp);

            JsonParser jsonParser = new JsonParser();
            JsonObject rootJsonObject = jsonParser.parse(resp).getAsJsonObject();

            //data
            String bucketKey = rootJsonObject.get("bucket-key").getAsString();
            JsonArray objects = rootJsonObject.getAsJsonArray("objects");
            String id = objects.get(0).getAsJsonObject().get("id").getAsString();
            String key = objects.get(0).getAsJsonObject().get("key").getAsString();
            String sha1 = objects.get(0).getAsJsonObject().get("sha-1").getAsString();
            String size = objects.get(0).getAsJsonObject().get("size").getAsString();
            String location = objects.get(0).getAsJsonObject().get("location").getAsString();

            System.out.println("\n"
                            + "Bucket-key" + bucketKey + "\n"
                            + "ID: " + id + "\n"
                            + "key: " + key + "\n"
                            + "sha1: " + sha1 + "\n"
                            + "size: " + size + "\n"
                            + "location: " + location + "\n"

            );


            ModelService ms = new ModelService(StaticData.getDb());
            Gson gson = new Gson();
            /*String childModels = gson.toJson(modelDependencies);
            JsonObject children = new JsonObject();
            for(int i=0; i<modelDependencies.size(); i++){
                children.addProperty("name", modelDependencies.get(i));
            }*/
            String childModels = gson.toJson(modelDependencies);
            JsonObject children = new JsonObject();
            String c = "";
            for (int i = 0; i < modelDependencies.size(); i++) {
                c += modelDependencies.get(i);
                c += ", ";
            }


            System.out.println("childModels: " + children);

            //check fileType
            String extension = alternateKey.substring(alternateKey.lastIndexOf(".") + 1);
            String fileType = null;
            if (extension.toLowerCase().equals("sldasm")) {
                fileType = "sldasm";
            } else if (extension.toLowerCase().equals("sldprt")) {
                fileType = "sldprt";
            } else if (extension.toLowerCase().equals("iam")) {
                fileType = "iam";
            } else if (extension.toLowerCase().equals("ipt")) {
                fileType = "ipt";
            } else if (extension.toLowerCase().equals("prt")) {
                fileType = "prt";
            }
            System.out.println("fileType " + extension.toLowerCase());
            System.out.println("fileType " + fileType);

            JsonObject modelObject = new JsonObject();
            modelObject.addProperty("title", key);
            modelObject.addProperty("alternateTitle", alternateKey);
            modelObject.addProperty("urn", id);
            modelObject.addProperty("parentBucket", request.params(":id"));
            modelObject.addProperty("localPreviewPath", "/img/generated/" + previewImagePath.substring(previewImagePath.lastIndexOf("/") + 1));
            modelObject.addProperty("localModelPath", modelNewName.getPath());
            modelObject.addProperty("externalModelPath", location);
            modelObject.addProperty("childModels", c);
            modelObject.addProperty("modelType", fileType);

            Boolean registered = RegisterService.registerModel(id, StaticData.getAuthorizationToken());
            System.out.println("Model registered: " + registered);

            //Create json definition of model
            ms.createNewModel(modelObject.toString());
            System.out.println("Model Spremljen u bazu.");

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
