package com.bmcarr.unperishable.util;

import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * The ApiRequestTask is responsible for issuing all HTTP requests to the
 * server. It is intended to be run on a separate thread so as not to interfere
 * with the main UI thread.
 *
 * Two things are important when utilizing this class.
 *
 * Firstly, understand that, although it does run on a separate thread, one
 * will have to block the thread that called it in order to accept a return
 * value from the server and check for exceptions caused by the HTTP request.
 *
 * Secondly, prepare to catch exceptions. The request objects have a
 * threwException() method, and since any exceptions thrown here will not be
 * returned to the calling method, this needs to be checked after the task has
 * finished running.
 *
 * The Exception type is a ServerApiException, which will hold the original
 * Exception (accessible by the getCause() method) and an HTTP response code,
 * if the request processed successfully. The two main causes are IOExceptions
 * and JSONExceptions.
 */
public class ApiRequestTask implements Runnable {

    public static final String TAG = "APIRequestTask";
    public static enum RequestType {
        JSON_OBJECT, JSON_ARRAY, JSON_READER, PLAIN_TEXT, SAVE_TO_DISK, POST_ONLY
    }

    private URL url;
    private HttpURLConnection connection;
    private List<String> headers;
    private JSONObject bodyContent;
    private RequestType requestType;
    private RequestMethod requestMethod;
    private File file;
    private boolean isFinished = false;
    private int responseCode;

    private JSONArray retrievedArray = null;
    private JSONObject retrievedObject = null;
    private JsonReader retrievedReader = null;
    private String plainText = "";

    public static enum RequestMethod {
        POST, GET
    }

    private static final String SERVER_URL = "http://unperishables.bmcarr.com";



    /**
     * Creates an HTTP GET request task.
     *
     * @param requestType Determines how the response body will be interpreted
     * @param headers Headers to attach to the request
     *
     * @return A new ApiRequestTask object
     */
    public static ApiRequestTask createGetRequest(String relativeUrl, RequestType requestType, List<String> headers) throws MalformedURLException {
        URL url = new URL(SERVER_URL);
        return new ApiRequestTask(new URL(url, relativeUrl), requestType, RequestMethod.GET, headers, null, null);
    }

    /**
     * Creates an HTTP POST request task.
     *
     * @param requestType Determines how the response body will be interpreted
     * @param headers Headers to attach to the request
     * @param bodyContent Body to attach to the POST request.
     *
     * @return A new ApiRequestTask object
     */
    public static ApiRequestTask createPostRequest(String relativeUrl, RequestType requestType, List<String> headers, JSONObject bodyContent) throws MalformedURLException {
        URL url = new URL(SERVER_URL);
        return new ApiRequestTask(new URL(url, relativeUrl), requestType, RequestMethod.POST, headers, bodyContent, null);
    }


    /**
     * Creates an HTTP GET request task that saves a file to disk
     *
     * @param url URL object to which to issue the request
     * @param headers Headers to attach to the request
     * @param file File object to which to save HTTP response
     *
     * @return A new ApiRequestTask object
     */
    public static ApiRequestTask createFileDownloadRequest(URL url, List<String> headers, File file) {
        return new ApiRequestTask(url, RequestType.SAVE_TO_DISK, RequestMethod.GET, headers, null, file);
    }

    /**
     * Private constructor accessed by the static creation methods
     *
     * @param url URL object to which to issue the request
     * @param requestType Determines how the response body will be interpreted
     * @param requestMethod HTTP method by which the request should be issued
     * @param headers Headers to attach to the request
     * @param bodyContent Body to attach to the POST request.
     * @param file File object to which to save HTTP response
     */
    private ApiRequestTask(URL url, RequestType requestType, RequestMethod requestMethod, List<String> headers, JSONObject bodyContent, File file) {
        this.url = url;
        this.headers = headers;
        this.bodyContent = bodyContent;
        this.requestType = requestType;
        this.requestMethod = requestMethod;
        this.file = file;
    }

    /**
     * Issues the HTTP request. This method is intended to be called on a
     * separate thread to avoid a NetworkOnMainThreadException
     */
    @Override
    public void run() {
        try {
            connection = (HttpURLConnection) url.openConnection();
            if (headers != null) {
                for (String header : headers) {
                    String[] keyAndValue = header.split(":");
                    connection.addRequestProperty(keyAndValue[0].trim(), keyAndValue[1].trim());
                }
            }
            connection.addRequestProperty("Content-Type", "application/json");

            if ( requestMethod == RequestMethod.POST ) {
                postToServer();
            } else {
                getFromServer();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                this.responseCode = connection.getResponseCode();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            isFinished = true;
            connection.disconnect();
        }
    }

    /**
     * Issues an HTTP GET request and stores the response element, if one is
     * expected
     *
     * @throws IOException
     * @throws JSONException
     */
    private void postToServer() throws IOException, JSONException {
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        InputStream inputStream = null;
        OutputStream outputStream = connection.getOutputStream();

        OutputStreamWriter osw = new OutputStreamWriter(outputStream);
        osw.write(bodyContent.toString());
        osw.flush();
        osw.close();

        switch (requestType) {
            case JSON_ARRAY:
                inputStream = connection.getInputStream();
                this.retrievedArray = new JSONArray(getStringFromStream(inputStream));
                break;
            case JSON_OBJECT:
                inputStream = connection.getInputStream();
                this.retrievedObject = new JSONObject(getStringFromStream(inputStream));
                break;
            case JSON_READER:
                inputStream = connection.getInputStream();
                this.retrievedReader = new JsonReader(new InputStreamReader(inputStream));
                break;
            case PLAIN_TEXT:
                inputStream = connection.getInputStream();
                this.plainText = getStringFromStream(inputStream);
                break;
            case SAVE_TO_DISK:
                break;
            case POST_ONLY:
                break;
        }
        this.responseCode = connection.getResponseCode();
        if ( inputStream != null ) {
            inputStream.close();
        }
    }

    /**
     * Issues an HTTP GET request and stores the response element
     *
     * @throws IOException
     * @throws JSONException
     */
    private void getFromServer() throws IOException, JSONException {
        InputStream inputStream = null;
        switch (requestType) {
            case JSON_ARRAY:
                inputStream = connection.getInputStream();
                this.retrievedArray = new JSONArray(getStringFromStream(inputStream));
                break;
            case JSON_OBJECT:
                inputStream = connection.getInputStream();
                this.retrievedObject = new JSONObject(getStringFromStream(inputStream).replaceAll("/n", ""));
                break;
            case JSON_READER:
                inputStream = connection.getInputStream();
                this.retrievedReader = new JsonReader(new InputStreamReader(inputStream));
                return;
            case SAVE_TO_DISK:
                inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(this.file);

                int contentLength = connection.getContentLength();

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                outputStream.close();

                if ( file.length() != contentLength ) {
                    file.delete();
                }
                break;
            case POST_ONLY:
                break;
        }
        if ( inputStream != null ) {
            inputStream.close();
        }
    }

    /**
     * Gets a String from an InputStream. This should only be used when the
     * response from an HTTP request can fit into memory.
     *
     * @param inputStream Stream to coerce into a String
     * @return String gathered from the InputStream
     *
     * @throws IOException
     */
    private String getStringFromStream(InputStream inputStream) throws IOException {
        BufferedReader reader;
        StringBuilder sb;
        reader = new BufferedReader(new InputStreamReader(inputStream));
        sb = new StringBuilder();
        for (String s; (s = reader.readLine()) != null;) {
            sb.append(s).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public boolean isFinished() {
        return isFinished;
    }

    public JSONArray getRetrievedArray() {
        return retrievedArray;
    }

    public JSONObject getRetrievedObject() {
        return retrievedObject;
    }

    public JsonReader getRetrievedReader() {
        return retrievedReader;
    }

    public String getRetrievedText() {
        return plainText;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

}