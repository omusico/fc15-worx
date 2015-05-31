package hr.heisenbug.worxapp.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mathabaws on 5/31/15.
 */
public class ReferenceSetter {

    private String masterURN = "";
    private String parentPath = "";
    /*
    * 1. element of array is child urn
    * 2. element of array is child name
    * */
    private List<String[]> dependencies = new LinkedList<>();
    private String autkToken = "";

    public ReferenceSetter(String masterURN, String parentPath, List<String[]> dependencies, String authToken) {
        this.masterURN = masterURN;
        this.parentPath = parentPath;
        this.dependencies = dependencies;
        this.autkToken =  authToken;
    }


    public Boolean setReferences(){

        Boolean result = false;

        DataOutputStream output = null;
        InputStream input = null;
        BufferedReader buffer = null;

        try {

            URL bucketURL = new URL(
                    "https://developer.api.autodesk.com/references/v1/setreference");
            HttpsURLConnection connection = (HttpsURLConnection) bucketURL
                    .openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + autkToken);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            output = new DataOutputStream(connection.getOutputStream());

            String jsonQuery = "";
            jsonQuery += "{ \"master\": \""+masterURN+"\", ";
            jsonQuery += "\"dependencies\": [";
            for(String[] a : this.dependencies){

                jsonQuery += "{\n" +
                        "        \"file\": \""+a[0]+"\",\n" +
                        "        \"metadata\": {\n" +
                        "            \"childPath\": \""+a[1]+"\",\n" +
                        "            \"parentPath\": \""+parentPath+"\"\n" +
                        "        }\n" +
                        "    }";
                if(this.dependencies.indexOf(a)+1 < this.dependencies.size())
                    jsonQuery += ",";
            }
            jsonQuery += "]}";

            System.out.println("Json query: "+jsonQuery);

            //    modelObject.addProperty("dependencies", /*JSON array*/);


            output.writeBytes(jsonQuery);

            // parse the response
            System.out.println("Server response: "+connection.getResponseCode() + " "+connection.getResponseMessage());
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
            System.out.println("reference response: "+response);
            result = true;
        } catch (IOException e) {
            System.out.println("Network connection error");
        }

        return result;
    }

    public String getMasterURN() {
        return masterURN;
    }

    public void setMasterURN(String masterURN) {
        this.masterURN = masterURN;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public List<String[]> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String[]> dependencies) {
        this.dependencies = dependencies;
    }
}
