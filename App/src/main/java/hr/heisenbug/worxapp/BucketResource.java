package hr.heisenbug.worxapp;

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
        post(API_CONTEXT + "/projects", "application/json", (request, response) -> {
            bucketService.createNewBucket(request.body());
            response.status(201);
            return response;
        }, new JsonTransformer());

        get(API_CONTEXT + "/projects/:id", "application/json", (request, response)

                -> bucketService.find(request.params(":id")), new JsonTransformer());

        get(API_CONTEXT + "/projects", "application/json", (request, response)

                -> bucketService.findAll(), new JsonTransformer());
//todo add update route
//      put(API_CONTEXT + "/projects/:id", "application/json", (request, response)
//
//                -> bucketService.update(request.params(":id"), request.body()), new JsonTransformer());
    }
}
