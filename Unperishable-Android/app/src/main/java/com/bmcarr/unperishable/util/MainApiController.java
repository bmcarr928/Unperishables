//package com.bmcarr.unperishable.util;
//
///**
// * Created by jeff on 12/1/14.
// */
//
//import java.net.URL;
//
//import android.util.JsonReader;
//import android.widget.Toast;
//
//import com.bmcarr.unperishable.util.ApiRequestTask;
//import com.bmcarr.unperishable.util.ApiRequestTask.RequestType;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * The MainApiController is the primary interface between the server API and
// * the application, and also manages all login credentials and authorization
// * tokens. It manages the individual calls to the server and returns their
// * responses to the auxiliary API classes in the form of files, JSON constructs,
// * etc. It also handles the caching and persisting of user credentials
// * in the form of instance variables and a local file, respectively.
// *
// * @author ElegantThought
// *
// */
//public class MainApiController {
//
//    private static final String SERVER_URL = "http://unperishables.bmcarr.com/";
//    private static final String TAG = "MainAPIController";
//    private static final MainApiController INSTANCE = new MainApiController();
//
//    public static enum RequestMethod {
//        POST, GET
//    }
//
//    private String username = null;
//    private String userId = null;
//    private String userToken= null;
//    private String deviceName = null;
//    private String deviceToken = null;
//    private URL url;
//
//    /**
//     * Private constructor that creates the singleton instance, held in the
//     * static final reference to INSTANCE
//     */
//    private MainApiController() {
//        try {
//            url = new URL(SERVER_URL);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Returns the singleton reference to the instance of this class.
//     * @return Singleton MainAPIController instance
//     */
//    public static MainApiController getInstance() {
//        return INSTANCE;
//    }
//
//    /**
//     * Initializes the instance of this class for testing, injecting its URL
//     * dependency with the URL parameter. This method exists solely for testing
//     * purposes, and shouldn't be used for any other reason.
//     *
//     * @param url URL that HTTP requests will be executed on
//     * @return MainAPIController singleton instance
//     */
//    public static MainApiController initForTesting(URL url) {
//        INSTANCE.url = url;
//        return INSTANCE;
//    }
//
//    /**
//     * Returns the response from an HTTP request as a JSONObject.
//     *
//     * @param relativeUrl URL relative to the server URL to which the HTTP
//     * request will be sent
//     * @param headers List of strings containing a JSON header to be added
//     * to the HTTP request
//     * @param bodyContent Body content of the request. If not null, the request
//     * will be sent as a POST request
//     * @return JSONObject representing the JSON response from the server
//     */
//    public JSONObject getJsonObject(String relativeUrl, RequestMethod requestMethod, List<String> headers, JSONObject bodyContent) {
//        if( headers == null ) {
//            headers = new ArrayList<String>();
//        }
//
//        try {
//            addValidation(headers);
//            ApiRequestTask requestTask;
//            if ( requestMethod == RequestMethod.POST ) {
//                requestTask = ApiRequestTask.createPostRequest(new URL(this.url, relativeUrl), RequestType.JSON_OBJECT, headers, bodyContent);
//            } else {
//                requestTask = ApiRequestTask.createGetRequest(new URL(this.url, relativeUrl), RequestType.JSON_OBJECT, headers);
//            }
//
//            Thread thread = new Thread( requestTask );
//            thread.start();
//
//            while ( ! requestTask.isFinished() ) {
//            }
//            JSONObject obj = requestTask.getRetrievedObject();
//
//
//            if ( obj == null ) {
//                System.err.println("Could not get JSON object from HTTP request");
//            } else {
//                return obj;
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Returns the response from an HTTP request as a JSONArray.
//     *
//     * @param relativeUrl URL relative to the server URL to which the HTTP
//     * request will be sent
//     * @param headers List of strings containing a JSON header to be added
//     * to the HTTP request
//     * @param bodyContent Body content of the request. If not null, the request
//     * will be sent as a POST request
//     * @return JSONArray representing the JSON response from the server
//     */
//    public JSONArray getJsonArray(String relativeUrl, RequestMethod requestMethod, List<String> headers, JSONObject bodyContent){
//        if( headers == null ) {
//            headers = new ArrayList<String>();
//        }
//
//        try {
//            addValidation(headers);
//            ApiRequestTask requestTask;
//            if ( requestMethod == RequestMethod.POST ) {
//                requestTask = ApiRequestTask.createPostRequest(new URL(this.url, relativeUrl), RequestType.JSON_ARRAY, headers, bodyContent);
//            } else {
//                requestTask = ApiRequestTask.createGetRequest(new URL(this.url, relativeUrl), RequestType.JSON_ARRAY, headers);
//            }
//
//            Thread thread = new Thread( requestTask );
//            thread.start();
//
//            while ( ! requestTask.isFinished() ) {
//            }
//            JSONArray array = requestTask.getRetrievedArray();
//
//            if ( array == null ) {
//                System.err.println("Could not get JSONArray object from HTTP request");
//            } else {
//                return array;
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * Returns the response from an HTTP request as a JSONObject. If the
//     * bodyContent parameter isn't null, this will be sent as a POST request.
//     *
//     * @param relativeUrl URL relative to the server URL to which the HTTP
//     * request will be sent
//     * @param headers List of strings containing a JSON header to be added
//     * to the HTTP request
//     * @param bodyContent Body content of the request. If not null, the request
//     * will be sent as a POST request
//     * @return JSONObject representing the JSON response from the server
//     */
//    public JsonReader getJsonReader(String relativeUrl, RequestMethod requestMethod, List<String> headers, JSONObject bodyContent) {
//        if( headers == null ) {
//            headers = new ArrayList<String>();
//        }
//
//        try {
//            addValidation(headers);
//            ApiRequestTask requestTask;
//            if ( requestMethod == RequestMethod.POST ) {
//                requestTask = ApiRequestTask.createPostRequest(new URL(this.url, relativeUrl), RequestType.JSON_READER, headers, bodyContent);
//            } else {
//                requestTask = ApiRequestTask.createGetRequest(new URL(this.url, relativeUrl), RequestType.JSON_READER, headers);
//            }
//
//            Thread thread = new Thread( requestTask );
//            thread.start();
//
//            while ( ! requestTask.isFinished() ) {
//            }
//            JsonReader reader = requestTask.getRetrievedReader();
//
//
//            if ( reader == null ) {
//                System.err.println("Could not get JSONReader object from HTTP request");
//            } else {
//                return reader;
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * Writes a response from an HTTP request to disk
//     *
//     * @param relativeUrl URL relative to the server URL to which the HTTP
//     * request will be sent
//     * @param fileToSave File to be written to disk (or overwritten)
//     * @param headers List of strings containing a JSON header to be added
//     * to the HTTP request
//     * @return The new file that was written to disk
//     */
////    public File saveToDiskFromServer(String relativeUrl, List<String> headers, File fileToSave) {
////        if( headers == null ) {
////            headers = new ArrayList<String>();
////        }
////
////        try {
////            addValidation(headers);
////            fileToSave.getParentFile().mkdirs();
////            ApiRequestTask requestTask = ApiRequestTask.createFileDownloadRequest(new URL(this.url, relativeUrl), headers, fileToSave);
////
////            Thread thread = new Thread( requestTask );
////            thread.start();
////
////            while ( ! requestTask.isFinished() ) {
////            }
////
////            if ( requestTask.threwException() ) {
////                throw requestTask.getException();
////            }
////            return fileToSave;
////        } catch (MalformedURLException e) {
////            throw new ServerApiException( e );
////        }
////
////    }
//
//    /**
//     * Sends an HTTP POST request to the server.
//     *
//     * @param relativeUrl URL relative to the server URL to which the HTTP
//     * request will be sent
//     * @param headers List of strings containing a JSON header to be added
//     * to the HTTP request
//     * @param bodyContent Body content of the request. If not null, the request
//     * will be sent as a POST request
//
//     * @throws ServerApiException
//     */
//    public void postToServer(String relativeUrl, List<String> headers, JSONObject bodyContent) throws ServerApiException {
//        if( headers == null ) {
//            headers = new ArrayList<String>();
//        }
//
//        try {
//            addValidation(headers);
//            ApiRequestTask requestTask = ApiRequestTask.createPostRequest(new URL(this.url, relativeUrl), RequestType.POST_ONLY, headers, bodyContent);
//
//            Thread thread = new Thread( requestTask );
//            thread.start();
//
//            while ( ! requestTask.isFinished() ) {
//            }
//
//            if ( requestTask.threwException() ) {
//                throw requestTask.getException();
//            }
//        } catch (MalformedURLException e) {
//            throw new ServerApiException( e );
//        }
//    }
//
//    /**
//     * Adds validation to an HttpURLConnection, if a user is logged in with a
//     * user authorization token and/or a device token
//     *
//     * @param headers List of headers to which to add validation tokens
//     */
//    public void addValidation(List<String> headers) {
//        if ( userToken != null ) {
//            headers.add("Authorization: Bearer " + userToken);
//        }
//        if ( deviceToken != null ) {
//            headers.add("DeviceAuthorization: " + deviceToken);
//        }
//    }
//
//    /**
//     * Saves the user's authorization credentials to disk
//     */
//    public void saveUserInfo() throws ServerApiException {
//        if ( isAuthorized() ) {
//            FileWriter writer;
//            try {
//                File saveFile = new File(Utilities.androidDir() + "/userinfo");
//                writer = new FileWriter(saveFile);
//
//                writer.write(this.username + "\n");
//                writer.write(this.userId + "\n");
//                writer.write(this.userToken + "\n");
//                writer.write(this.deviceName + "\n");
//                writer.write(this.deviceToken + "\n");
//
//                writer.close();
//            } catch (IOException e) {
//                File toDelete = new File(Utilities.androidDir() + "/userinfo");
//                toDelete.delete();
//                throw new ServerApiException( "Could not save user information, aborting", e);
//            }
//        }
//    }
//
//    /**
//     * Checks that all required information has been supplied for a user to
//     * submit HTTP requests that require authorization
//     *
//     * @return True, if authorized
//     */
//    public boolean isAuthorized() {
//        return ! (username == null || deviceName == null || userId == null ||
//                userToken == null || deviceToken == null);
//    }
//
//    /**
//     * Attempts to retrieve the user's login information from disk
//     *
//     * @return True, if login was successful
//     */
//    public boolean loginUserFromDisk() throws ServerApiException {
//        if ( isAuthorized() ) {
//            return true;
//        }
//        File file = new File(Utilities.androidDir() + "/userinfo");
//        BufferedReader reader = null;
//        if ( file.exists() && file.canRead() ) {
//            try {
//                reader = new BufferedReader(new FileReader(file));
//                this.username = reader.readLine();
//                this.userId = reader.readLine();
//                this.userToken = reader.readLine();
//                this.deviceName = reader.readLine();
//                this.deviceToken = reader.readLine();
//                reader.close();
//                return true;
//            } catch (FileNotFoundException e) {
//                throw new ServerApiException( "File not found, or could not be opened. Check permissions.", e );
//            } catch (IOException e) {
//                throw new ServerApiException( e );
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Removes all login credentials from memory, essentially logging
//     * the user out. The boolean parameter determines whether or not to
//     * save the user information to disk
//     *
//     * @param persistData If true, save user credentials to disk to be loaded
//     * in the future
//     */
//    public void invalidateUser(boolean persistData) throws ServerApiException {
//        if ( persistData ) {
//            saveUserInfo();
//        } else {
//            File file = new File(Utilities.androidDir() + "/userinfo");
//            if ( file.exists() ) {
//                file.delete();
//            }
//        }
//        this.username = null;
//        this.userId = null;
//        this.userToken = null;
//        this.deviceName = null;
//        this.deviceToken = null;
//    }
//
//    public URL getUrl() {
//        return this.url;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getUsername() {
//        return this.username;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getUserId() {
//        return this.userId;
//    }
//
//    public void setUserToken(String userToken) {
//        this.userToken = userToken;
//    }
//
//    public String getUserToken() {
//        return this.userToken;
//    }
//
//    public void setDeviceName(String deviceName) {
//        this.deviceName = deviceName;
//    }
//
//    public String getDeviceName() {
//        return this.deviceName;
//    }
//
//    public void setDeviceToken(String deviceToken) {
//        this.deviceToken = deviceToken;
//    }
//
//    public String getDeviceToken() {
//        return this.deviceToken;
//    }
//}