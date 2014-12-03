package com.bmcarr.unperishable.util;

import android.util.Log;

import com.bmcarr.unperishable.data.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class CreateAccountTask extends Observable implements Runnable {

    private static final String TAG = "AddItemTask";
    private String loggedInUser;
    private Item item;

    public CreateAccountTask(String loggedInUser, Item item) {
        this.item = item;
        this.loggedInUser = loggedInUser;
    }

    @Override
    public void run() {

        Log.d(TAG, "SyncDbTask Running...");
        ApiRequestTask requestTask;
        String relativeUrl = "/api/additem/";
        List<String> headers = new ArrayList<String>();

        JSONObject body = new JSONObject();
        try {
            body.put("name", loggedInUser);
            JSONObject json_item = new JSONObject();
            json_item.put("name", item.getName());
            json_item.put("category", item.getCategory().getId());
            json_item.put("quantity", item.getQuantity().getId());
            json_item.put("owner", item.getOwner());
            json_item.put("input_date", item.getInputDate().getTime());
            json_item.put("expiration_date", item.getExpirationDate().getTime());
            body.put("item", json_item);
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

            Log.d(TAG, "Response = " + requestTask.getRetrievedText());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
