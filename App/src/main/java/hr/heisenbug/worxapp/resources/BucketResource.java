package hr.heisenbug.worxapp.resources;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hr.heisenbug.worxapp.JsonTransformer;
import hr.heisenbug.worxapp.helpers.BucketCreator;
import hr.heisenbug.worxapp.helpers.StaticData;
import hr.heisenbug.worxapp.models.Bucket;
import hr.heisenbug.worxapp.services.BucketService;
import spark.Spark;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

/**
 * Created by Viktor on 17/05/2015.
 */
public class BucketResource {
    private static final String API_CONTEXT = "/api/v1";

    private final BucketService bucketService;

    public BucketResource(BucketService bucketService) {
        this.bucketService = bucketService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        Spark.post(API_CONTEXT + "/projects", "application/json", (request, response) -> {
            //create bucket on Autodesk side
            String projectTitle = new Gson().fromJson(request.body(), Bucket.class).getTitle();


            //replace spaces and all non-alphanumeric chars
            projectTitle = projectTitle.toLowerCase();
            projectTitle = projectTitle.replace(" ","_");
            projectTitle = projectTitle.replaceAll("[^-_.a-z0-9]", "");


            BucketCreator bucketCreator = new BucketCreator(projectTitle, StaticData.getAuthorizationToken());
            /*
            TODO:
            2. Catch exception or bucket creation fail. Example response:
            {"reason":"Bucket already exists"}

             */
            JsonParser parser = new JsonParser();
            JsonObject dataObj = (JsonObject)parser.parse(request.body());
            JsonObject apiObj =  null;

            try {

                String bucketApiResponse = bucketCreator.createBucket();
                apiObj = (JsonObject)parser.parse(bucketApiResponse);
                System.out.println("Bucket api response: \n" + bucketApiResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String data = request.body();
            dataObj.add("bucketKey", apiObj.get("key"));
            String requestData = dataObj.toString();
            System.out.println(requestData);

            //create database entry
            bucketService.createNewBucket(requestData);
            response.status(201);
            return response;

        }, new JsonTransformer());

        get(API_CONTEXT + "/projects/:id", "application/json", (request, response)

                -> bucketService.find(request.params(":id")), new JsonTransformer());

        get(API_CONTEXT + "/projects", "application/json", (request, response)

                -> bucketService.findAll(), new JsonTransformer());

        get(API_CONTEXT + "/lastProject", "application/json", (request, response)

                -> bucketService.latestId(), new JsonTransformer());
//todo add update route
//      put(API_CONTEXT + "/projects/:id", "application/json", (request, response)
//
//                -> bucketService.update(request.params(":id"), request.body()), new JsonTransformer());
    }
}
