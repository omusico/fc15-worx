package hr.heisenbug.worxapp.helpers;

import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mathabaws on 5/31/15.
 */
public class RegisterService {

    private String modelURN;
    private String authToken;

    public static Boolean registerModel(String modelURN, String authToken) {
        Boolean result = false;

        String autkToken = authToken;

        DataOutputStream output = null;
        InputStream input = null;
        BufferedReader buffer = null;

        URL bucketURL = null;
        try {
            bucketURL = new URL(
                    "https://developer.api.autodesk.com/viewingservice/v1/register");


            HttpsURLConnection connection = (HttpsURLConnection) bucketURL
                    .openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + autkToken);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            output = new DataOutputStream(connection.getOutputStream());
            byte[] modelEncoded = Base64.encodeBase64(modelURN.getBytes());
            String jsonQuery = "";
            jsonQuery += "{\n" +
                    "  \"urn\":\""+new String(modelEncoded)+"\"\n" +
                    "}";

            System.out.println("Json query: " + jsonQuery);

            //    modelObject.addProperty("dependencies", /*JSON array*/);


            output.writeBytes(jsonQuery);

            // parse the response
            System.out.println("Server response: " + connection.getResponseCode() + " " + connection.getResponseMessage());
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
                //stringBuffer.append('\r');
            }


            System.out.println(stringBuffer);
            String response = stringBuffer.toString();
            System.out.println("reference response: " + response);
            result = true;
        } catch (IOException e) {
            System.out.println("Network connection error");
        }

        return result;
    }
}
