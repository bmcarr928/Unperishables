package com.bmcarr.unperishable.util;

import android.util.Log;

import com.bmcarr.unperishable.data.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class DeleteItemTask extends Observable implements Runnable {

    private static final String TAG = "AddItemTask";
    private String loggedInUser;
    private Item item;

    public DeleteItemTask(String loggedInUser, Item item) {
        this.item = item;
        this.loggedInUser = loggedInUser;
    }

    @Override
    public void run() {

        Log.d(TAG, "SyncDbTask Running...");
        ApiRequestTask requestTask;
        String relativeUrl = "/api/deleteitem/";
        List<String> headers = new ArrayList<String>();

        try {
            JSONObject body = new JSONObject();
            body.put("name", loggedInUser);
            body.put("item_name", item.getName());

            requestTask = ApiRequestTask.createPostRequest(relativeUrl,
                    ApiRequestTask.RequestType.PLAIN_TEXT, headers, body);

            Thread t = new Thread(requestTask);
            t.start();

            while (!requestTask.isFinished()) {
            }

            Log.d(TAG, "Response = " + requestTask.getRetrievedText());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
