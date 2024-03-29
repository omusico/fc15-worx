package hr.heisenbug.worxapp.helpers;

import com.mongodb.DB;

/**
 * Created by mathabaws on 5/23/15.
 */
public class StaticData {

    private static String CONSUMER_KEY = "";
    private static String CONSUMER_SECRET = "";
    private static String authorizationToken ="";
    private static DB db = null;

    public static String getConsumerKey() {
        return CONSUMER_KEY;
    }

    public static void setConsumerKey(String consumerKey) {
        CONSUMER_KEY = consumerKey;
    }

    public static String getConsumerSecret() {
        return CONSUMER_SECRET;
    }

    public static void setConsumerSecret(String consumerSecret) {
        CONSUMER_SECRET = consumerSecret;
    }

    public static String getAuthorizationToken() {
        return authorizationToken;
    }

    public static void setAuthorizationToken(String authorizationToken) {
        StaticData.authorizationToken = authorizationToken;
    }

    public static DB getDb() {
        return db;
    }

    public static void setDb(DB db) {
        StaticData.db = db;
    }
}
