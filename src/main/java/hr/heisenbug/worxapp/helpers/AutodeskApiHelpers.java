package hr.heisenbug.worxapp.helpers;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Viktor on 17/05/2015.
 */
public class AutodeskApiHelpers {

    private String url = null;
    private String authToken = "";

    public AutodeskApiHelpers() {
    }


    public String authenticateApi(String key, String secret){

        DataOutputStream output = null;
        InputStream input = null;
        BufferedReader buffer = null;
        String token = null;
        try {
            //set connection
            url = "https://developer.api.autodesk.com/authentication/v1/authenticate";
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            //set request data
            output = new DataOutputStream(conn.getOutputStream());
            output.writeBytes("client_id="
                    + URLEncoder.encode(key, "UTF-8")
                    + "&client_secret="
                    + URLEncoder.encode(secret, "UTF-8")
                    + "&grant_type=client_credentials");

            //catch api resonse
            input = conn.getInputStream();
            buffer = new BufferedReader(new InputStreamReader(input));
            String line;
            StringBuffer stringBuffer = new StringBuffer();

            while ((line = buffer.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append('\r');
            }
            //debug - show raw api response
            System.out.println("authenticateApi-raw: "+ stringBuffer);
            //parse api response
            String responseString = stringBuffer.toString();
            int index = responseString.indexOf("\"access_token\":\"")
                    + "\"access_token\":\"".length();
            int index2 = responseString.indexOf("\"", index);
            token = responseString.substring(index, index2);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.authToken = token;
        return token;
    }


    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
