package com.bmcarr.unperishable.util;

import android.util.Log;

import com.bmcarr.unperishable.data.DataAccess;
import com.bmcarr.unperishable.data.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class SyncDbTask extends Observable implements Runnable {

    private static final String TAG = "SyncDbTask";
    private long time = 0;
    private String loggedInUser;
    private DataAccess dataAccess;

    public SyncDbTask(String loggedInUser, DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.loggedInUser = loggedInUser;
    }

    @Override
    public void run() {

        Log.d(TAG, "SyncDbTask Running...");
        ApiRequestTask requestTask;
        String relativeUrl = "/api/sync/";
        List<String> headers = new ArrayList<String>();

        JSONObject body = new JSONObject();
        try {
            body.put("name", loggedInUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        time = System.currentTimeMillis();
        while ( ! Thread.currentThread().isInterrupted() ) {
            if ( System.currentTimeMillis() > time + 5000 ) {
                Log.d(TAG, "Loopage");
                try {
                    requestTask = ApiRequestTask.createPostRequest(relativeUrl,
                            ApiRequestTask.RequestType.JSON_ARRAY, headers, body);

                    Thread t = new Thread(requestTask);
                    t.start();

                    while (!requestTask.isFinished()) {
                    }
                    JSONArray items = requestTask.getRetrievedArray();

                    Log.d(TAG, items.toString());

//                    Log.d(TAG, items.getString("word"));
                    boolean dataSetChanged = false;
                    if (items.length() == 0) {
                        System.out.println("Could not get JSON object from HTTP request");
                    } else {
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject obj = ((JSONObject) items.get(i)).getJSONObject("fields");
                            if (dataAccess.queryForItemOfName(obj.getString("name")) == null) {
                                dataSetChanged = true;

                                String name = obj.getString("name");
                                Config.Category category = Config.Category.getCategory(obj.getInt("category"));
                                Config.Quantity quantity = Config.Quantity.getQuantity(obj.getInt("quantity"));
                                int input = obj.optInt("input_date", -1);
                                int expiration = obj.optInt("expiration_date", -1);
                                String owner = obj.optString("owner", loggedInUser);

                                Item item = new Item(name, category, quantity);
                                if (input != -1) item = item.withInputDate(new Date(input));
                                if (expiration != -1)
                                    item = item.withExpirationDate(new Date(input));
                                item = item.withOwner(owner);

                                this.dataAccess.saveItem(item);
                            }
                        }
                        Log.d(TAG, "dataSetChanged = " + dataSetChanged);
                        if (dataSetChanged) {
                            setChanged();
                            notifyObservers();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                time = System.currentTimeMillis();
            }
        }
    }
}
