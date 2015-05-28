package hr.heisenbug.worxapp;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by vlazar on 5/28/15.
 */
public class ModelResource {
    private static final String API_CONTEXT = "/api/v1";

    private final ModelService modelService;

    public ModelResource(ModelService modelService) {
        this.modelService = modelService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        post(API_CONTEXT + "/models", "application/json", (request, response) -> {
            modelService.createNewModel(request.body());
            response.status(201);
            return response;
        }, new JsonTransformer());

        get(API_CONTEXT + "/models/:id", "application/json", (request, response)

                -> modelService.find(request.params(":id")), new JsonTransformer());

        get(API_CONTEXT + "/models", "application/json", (request, response)

                -> modelService.findAll(), new JsonTransformer());
//todo add update route
//      put(API_CONTEXT + "/projects/:id", "application/json", (request, response)
//
//                -> bucketService.update(request.params(":id"), request.body()), new JsonTransformer());
    }
}
