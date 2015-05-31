package hr.heisenbug.worxapp.helpers;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;

/**
 * Created by mathabaws on 5/23/15.
 */
public class FileUploader {

    /**
     * Static method for uploading file from server to bucket.
     *
     * @param bucketName bucket name to upload file to
     * @param authToken  previously generated authortization token
     * @param filePath   path to file
     * @return
     */
    public static String uploadFile(String bucketName, String authToken, String filePath) throws Exception {

        //TODO test this! check if works.
        String bucket = bucketName;
        String token = authToken;
        String response = "";

        String urn = null;

        System.out.println("\nBucket name: "+bucketName);
        System.out.println("Auth token: "+authToken);
        System.out.println("FilePat: "+filePath);

        File uploadFile = new File(filePath);


        InputStream input = null;
        BufferedReader buffer = null;

        // if token was not previously generated, send an error message
        if (token == null || token.length() <= 0) {
            throw new Exception("Token not set!");
        }

        try {

            File binaryFile = uploadFile;

            URL uploadurl = new URL(
                    "https://developer.api.autodesk.com/oss/v1/buckets/"
                            + bucketName + "/objects/" + binaryFile.getName());
            HttpsURLConnection connection = (HttpsURLConnection) uploadurl
                    .openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization",
                    "Bearer " + token);
            connection.setRequestProperty("Content-Type",
                    "application/octet-stream");
            connection.setRequestProperty("Content-Length",
                    Long.toString(binaryFile.length()));

            System.out.println("file length = " + binaryFile.length());

            connection.setDoOutput(true);
            connection.setDoInput(true);

            BufferedOutputStream bos = new BufferedOutputStream(
                    connection.getOutputStream());
            //TODO test this. Not sure is it binaryFile or uploadFile
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(binaryFile));
            int i;
            int j = 0;
            // read byte by byte until end of stream
            while ((i = bis.read()) != -1) {
                bos.write(i);
                j++;
            }
            bos.close();

            // parse the response
            //System.out.println("Server returned: " + connection.getResponseCode());
            if (connection.getResponseCode() >= 400) {
                input = connection.getErrorStream();
            } else {
                input = connection.getInputStream();
            }

            buffer = new BufferedReader(new InputStreamReader(input));

            String line = "";
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = buffer.readLine()) != null) {
                stringBuffer.append(line);
                //stringBuffer.append('\r');
            }
            response = stringBuffer.toString();

            System.out.println("StringBuffer: "+stringBuffer);

        } catch (IOException e) {
            System.out.println("Error occured during file upload: "+e.getMessage());
            throw new Exception("Network error occurred during upload.");
        }
        //System.out.println("\nRESPONSE FROM UPLOADER: "+response);
        return response;
    }
}


