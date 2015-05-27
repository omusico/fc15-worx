package hr.heisenbug.worxapp;

import com.mongodb.*;
import hr.heisenbug.worxapp.helpers.AutodeskApiHelpers;
import hr.heisenbug.worxapp.helpers.StaticData;

import static spark.Spark.setIpAddress;
import static spark.Spark.setPort;
import static spark.SparkBase.externalStaticFileLocation;
import static spark.SparkBase.staticFileLocation;

/**
 * Created by shekhargulati on 09/06/14.
 */
public class Bootstrap {
    private static final String IP_ADDRESS = System.getenv("OPENSHIFT_DIY_IP") != null ? System.getenv("OPENSHIFT_DIY_IP") : "localhost";
    private static final int PORT = System.getenv("OPENSHIFT_DIY_IP") != null ? Integer.parseInt(System.getenv("OPENSHIFT_DIY_IP")) : 8080;

    public static void main(String[] args) throws Exception {
        setIpAddress(IP_ADDRESS);
        setPort(PORT);
        staticFileLocation("/public");

        //set api key and secret
        //TODO get api key and secret from settings files
        String key = "N8ffvGkDGg6gLJvniXdTXYRanm0irymv";
        String secret = "j2EATorbHKTsKr7N";
        //authenticate application with Autodesk api
        AutodeskApiHelpers aah = new AutodeskApiHelpers();
        String token = aah.authenticateApi(key, secret);
        System.out.println("Authentication: " + token);

        //set static data
        StaticData.setConsumerKey(key);
        StaticData.setConsumerSecret(secret);
        StaticData.setAuthorizationToken(token);


        new BucketResource(new BucketService(mongo()));
        new ServerUploadResource(new ServerUploadService());
    }

    //todo refactor
    private static DB mongo() throws Exception {
        String host = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
        if (host == null) {
            MongoClient mongoClient = new MongoClient("localhost");
            return mongoClient.getDB("todoapp");
        }
        int port = Integer.parseInt(System.getenv("OPENSHIFT_MONGODB_DB_PORT"));
        String dbname = System.getenv("OPENSHIFT_APP_NAME");
        String username = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
        String password = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");
        MongoClientOptions mongoClientOptions = MongoClientOptions.builder().connectionsPerHost(20).build();
        MongoClient mongoClient = new MongoClient(new ServerAddress(host, port), mongoClientOptions);
        mongoClient.setWriteConcern(WriteConcern.SAFE);
        DB db = mongoClient.getDB(dbname);
        if (db.authenticate(username, password.toCharArray())) {
            return db;
        } else {
            throw new RuntimeException("Not able to authenticate with MongoDB");
        }
    }
}
