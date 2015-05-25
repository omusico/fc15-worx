package hr.heisenbug.worxapp.helpers;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

/**
 * Created by mathabaws on 5/23/15.
 */
public class BucketCreator {

    public String bucketName;
    public String authToken;
    public String bucketPolicy = "transient";
    public String response = "";



    public BucketCreator(){

    }

    public BucketCreator(String bucketName, String authToken){
        this.bucketName = bucketName;
        this.authToken = authToken;
    }

    /**
     * Method to create a new bucket.
     *
     * @return JSON formatted response from Autodesk API
     * @throws Exception If there's no Authorization token or there's a network error exception
     * will be thrown.
     */
    public String createBucket() throws Exception {

        response = "";

        String bucketName = this.getBucketName();

        // set the authToken from AutodeskApiHelpers class
        String token = this.getAuthToken();

        DataOutputStream output = null;
        InputStream input = null;
        BufferedReader buffer = null;



        // if there's no authToken then just throw an exception
        if (token == null || token.length() <= 0) {
            throw new Exception("No authorization token");
        }

        // create the bucket
        try {

            URL bucketURL = new URL(
                    "https://developer.api.autodesk.com/oss/v1/buckets");
            HttpsURLConnection connection = (HttpsURLConnection) bucketURL
                    .openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes("{\"bucketKey\":\"" + bucketName
                    + "\",\"policy\":\""+this.getBucketPolicy()+"\"}");

            // parse the response
            if (connection.getResponseCode() >= 400) {
                input = connection.getErrorStream();
            } else {
                input = connection.getInputStream();
            }
            buffer = new BufferedReader(new InputStreamReader(input));

            String line;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = buffer.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append('\r');
            }


            System.out.println(stringBuffer);
            response = stringBuffer.toString();

        } catch (IOException e) {
            System.out.println("Network connection error");
            throw new Exception("Network connection error");
        }

        return response;
    }



    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getBucketPolicy() {
        return bucketPolicy;
    }

    /**
     * Method to set Bucket Policy.
     * From API documentation:
     *  Transient: cache-like storage that persists for only 24 hours, ideal for intermittent objects.
        Temporary: storage that persists for 30 days. Good for data that is uploaded and accessed, then not needed later. This type of bucket storage will save your service money.
        Persistent: storage that persists until it’s deleted. Items that have not been accessed for 2 years may be archived.
     * @param bucketPolicy parameter MUST be either transient, temporary or persistent.
     */
    public void setBucketPolicy(String bucketPolicy) {
        // ako je bucketPolicy jedno od navedenog
        if(bucketPolicy.toLowerCase().equals("transient") ||
                bucketPolicy.toLowerCase().equals("temporary") ||
                    bucketPolicy.toLowerCase().equals("persistent")){

            this.bucketPolicy = bucketPolicy;
        }
    }

    public String getResponse() {
        return response;
    }

}
