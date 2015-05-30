package hr.heisenbug.worxapp.resources;

import com.google.gson.Gson;
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
            System.out.println(request.body());
            //create database entry
            bucketService.createNewBucket(request.body());

            //create bucket on Autodesk side
            String projectTitle = new Gson().fromJson(request.body(), Bucket.class).getTitle();

            BucketCreator bucketCreator = new BucketCreator(projectTitle, StaticData.getAuthorizationToken());
            /*
            TODO:
            1. make new fields in database. This is the response:
            {"key":"090399334051234","owner":"N8ffvGkDGg6gLJvniXdTXYRanm0irymv","createDate":1432933310200,"permissions":[{"serviceId":"N8ffvGkDGg6gLJvniXdTXYRanm0irymv","access":"full"}],"policyKey":"transient"}
            2. Catch exception or bucket creation fail. Example response:
            {"reason":"Bucket already exists"}

             */
            try {
                String bucketApiResponse = bucketCreator.createBucket();
                System.out.println("Bucket api response: \n" + bucketApiResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }


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
