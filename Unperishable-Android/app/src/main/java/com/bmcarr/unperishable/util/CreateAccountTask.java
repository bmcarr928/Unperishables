package com.bmcarr.unperishable.util;

import android.app.ProgressDialog;
import android.util.Log;

import com.bmcarr.unperishable.data.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class CreateAccountTask extends Observable implements Runnable {

    private static final String TAG = "AddItemTask";
    private String username;
    private String password;
    private ProgressDialog progressDialog;
    private int responseCode;

    public CreateAccountTask(String username, String password, ProgressDialog progressDialog) {
        this.username = username;
        this.password = password;
        this.progressDialog = progressDialog;
    }

    @Override
    public void run() {

        Log.d(TAG, "SyncDbTask Running...");
        ApiRequestTask requestTask;
        String relativeUrl = "/api/adduser/";
        List<String> headers = new ArrayList<String>();

        JSONObject body = new JSONObject();
        try {
            body.put("name", this.username);
            body.put("password", this.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            requestTask = ApiRequestTask.createPostRequest(relativeUrl,
                    ApiRequestTask.RequestType.PLAIN_TEXT, headers, body);

            Thread t = new Thread(requestTask);
            t.start();

            while (!requestTask.isFinished()) {
            }
            progressDialog.dismiss();


            this.responseCode = requestTask.getResponseCode();
            Log.d(TAG, "Response (" + this.responseCode + ") = " + requestTask.getRetrievedText());
            this.progressDialog.dismiss();

            this.setChanged();
            notifyObservers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getUsername() {
        return this.username;
    }
}
