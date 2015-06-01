package hr.heisenbug.worxapp.resources;

import hr.heisenbug.worxapp.JsonTransformer;
import hr.heisenbug.worxapp.helpers.StaticData;
import hr.heisenbug.worxapp.services.ModelService;
import spark.Spark;

import static spark.Spark.get;

/**
 * Created by vlazar on 6/1/15.
 */
public class StaticDataResource {
    private static final String API_CONTEXT = "/api/v1";

    public StaticDataResource() {
        setupEndpoints();
    }

    private void setupEndpoints() {
       get(API_CONTEXT + "/authToken", "application/json", (request, response)

                -> StaticData.getAuthorizationToken(), new JsonTransformer());
    }
}