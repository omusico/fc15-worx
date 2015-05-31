package hr.heisenbug.worxapp;

import com.mongodb.*;
import hr.heisenbug.worxapp.helpers.AuthTokenGenerator;
import hr.heisenbug.worxapp.helpers.AutodeskApiHelpers;
import hr.heisenbug.worxapp.helpers.ReferenceSetter;
import hr.heisenbug.worxapp.helpers.StaticData;
import hr.heisenbug.worxapp.resources.BucketResource;
import hr.heisenbug.worxapp.resources.ModelResource;
import hr.heisenbug.worxapp.resources.ServerUploadResource;
import hr.heisenbug.worxapp.services.BucketService;
import hr.heisenbug.worxapp.services.ModelService;
import hr.heisenbug.worxapp.services.ServerUploadService;

import java.util.LinkedList;
import java.util.List;

import static spark.Spark.setIpAddress;
import static spark.Spark.setPort;
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
        String secret = "G90a3029b039b420";

        //authenticate application with Autodesk api
        AutodeskApiHelpers aah = new AutodeskApiHelpers();
        String token = aah.authenticateApi(key, secret);
        System.out.println("Authentication: " + token);

        //set static data
        StaticData.setConsumerKey(key);
        StaticData.setConsumerSecret(secret);
        StaticData.setAuthorizationToken(token);

        DB mongo = mongo();
        StaticData.setDb(mongo);

        //start token generator
        (new Thread(new AuthTokenGenerator())).start();


        new BucketResource(new BucketService(mongo));
        new ServerUploadResource(new ServerUploadService());
        new ModelResource(new ModelService(mongo));

        testReference();
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

    private static void testReference(){

        String masterURN = "urn:adsk.objects:os.object:referencetesst/vrotor_s_utorom1.sldasm";
        String child1 = "urn:adsk.objects:os.object:referencetesst/vzenska_osovina1.sldprt";
        String child2 = "urn:adsk.objects:os.object:referencetesst/vsipka_rotora1.sldprt";
        String child3 = "urn:adsk.objects:os.object:referencetesst/vmuska_osovina1.sldprt";
        List<String[]> dependencies = new LinkedList<>();

        String[] child11 = new String[2];
        child11[0] = child1;
        child11[1] = child1.substring(child1.lastIndexOf("/")+1);
        dependencies.add(child11);

        String[] child22 = new String[2];
        child11[0] = child2;
        child11[1] = child2.substring(child2.lastIndexOf("/")+1);
        dependencies.add(child22);

        String[] child33 = new String[2];
        child11[0] = child3;
        child11[1] = child3.substring(child3.lastIndexOf("/")+1);
        dependencies.add(child33);


        String parentPath = masterURN.substring(masterURN.lastIndexOf("/")+1);
        ReferenceSetter rs = new ReferenceSetter(masterURN, parentPath,dependencies,StaticData.getAuthorizationToken());
        Boolean success = rs.setReferences();
        System.out.println("Successfull;: "+success);

    }
}
