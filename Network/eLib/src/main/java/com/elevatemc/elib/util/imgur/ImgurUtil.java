package com.elevatemc.elib.util.imgur;

import com.google.common.io.ByteStreams;
import org.bson.internal.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 * Yoinked from https://github.com/DV8FromTheWorld/Imgur-Uploader-Java
 * Reworked to just do what we need thats all
 */
public class ImgurUtil {

    public static final String UPLOAD_API_URL = "https://api.imgur.com/3/image";
    private static final String CLIENT_ID = "b6c07d0048fda79";

    /**
     * Takes a file and uploads it to Imgur.
     * Does not check to see if the file is an image, this should be done
     * before the file is passed to this method.
     *
     * @param file The image to be uploaded to Imgur.
     * @return The JSON response from Imgur.
     */
    public static String upload(File file) {
        HttpURLConnection conn = getHttpConnection(UPLOAD_API_URL);
        writeToConnection(conn, "image=" + toBase64(file));
        return getResponse(conn);
    }

    /**
     * Converts a file to a Base64 String.
     *
     * @param file The file to be converted.
     * @return The file as a Base64 String.
     */
    private static String toBase64(File file) {
        try {
            FileInputStream fs = new FileInputStream(file);
            byte[] b = ByteStreams.toByteArray(fs);
            fs.close();
            return URLEncoder.encode(Base64.encode(b), "UTF-8");
        } catch (IOException e) {
            throw new ImgurException(ImgurStatusCode.UNKNOWN_ERROR, e);
        }
    }

    /**
     * Creates and sets up an HttpURLConnection for use with the Imgur API.
     *
     * @param url The URL to connect to. (check Imgur API for correct URL).
     * @return The newly created HttpURLConnection.
     */
    private static HttpURLConnection getHttpConnection(String url) {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Client-ID " + CLIENT_ID);
            conn.setReadTimeout(100000);
            conn.connect();
            return conn;
        } catch (UnknownHostException e) {
            throw new ImgurException(ImgurStatusCode.UNKNOWN_HOST, e);
        } catch (IOException e) {
            throw new ImgurException(ImgurStatusCode.UNKNOWN_ERROR, e);
        }
    }

    /**
     * Sends the provided message to the connection as uploaded data.
     *
     * @param conn    The connection to send the data to.
     * @param message The data to upload.
     */
    private static void writeToConnection(HttpURLConnection conn, String message) {
        OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(message);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new ImgurException(ImgurStatusCode.UNKNOWN_ERROR, e);
        }
    }

    /**
     * Gets the response from the connection, Usually in the format of a JSON string.
     *
     * @param conn The connection to listen to.
     * @return The response, usually as a JSON string.
     */
    private static String getResponse(HttpURLConnection conn) {
        StringBuilder str = new StringBuilder();
        BufferedReader reader;
        try {
            if (conn.getResponseCode() != ImgurStatusCode.SUCCESS.getHttpCode()) {
                throw new ImgurException(conn.getResponseCode());
            }
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new ImgurException(ImgurStatusCode.UNKNOWN_ERROR, e);
        }
        if (str.toString().equals("")) {
            throw new ImgurException(ImgurStatusCode.UNKNOWN_ERROR);
        }
        return str.toString();
    }
}
