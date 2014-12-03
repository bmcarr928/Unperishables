package com.bmcarr.unperishable.util;

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

/**
 * Created by jeff on 12/1/14.
 */
public class SyncDbTask extends Observable implements Runnable {

    long time = 0;
    String loggedInUser;
    DataAccess dataAccess;

    private URL url;

    public SyncDbTask(String loggedInUser, DataAccess dataAccess) {
        this.loggedInUser = loggedInUser;
    }

    @Override
    public void run() {

        ApiRequestTask requestTask;
        String relativeUrl = "api";
        List<String> headers = new ArrayList<String>();

        JSONObject body = new JSONObject();
        try {
            body.put("name", loggedInUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            requestTask = ApiRequestTask.createPostRequest(relativeUrl,
                    ApiRequestTask.RequestType.JSON_ARRAY, headers, body);

            while ( ! requestTask.isFinished() ) {
            }
            JSONArray items = requestTask.getRetrievedArray();

            boolean dataSetChanged = false;
            if ( items == null ) {
                System.out.println("Could not get JSON object from HTTP request");
            } else {
                for ( int i = 0; i < items.length(); i++ ) {

                    JSONObject obj = (JSONObject)items.get(i);
                    if ( dataAccess.queryForItemOfName(obj.getString("name")) != null ) {
                        dataSetChanged = true;

                        String name = obj.getString("name");
                        Config.Category category = Config.Category.getCategory(obj.getInt("category"));
                        Config.Quantity quantity = Config.Quantity.getQuantity(obj.getInt("quantity"));
                        int input = obj.optInt("input_date", -1);
                        int expiration = obj.optInt("expiration_date", -1);
                        String owner = obj.optString("owner", loggedInUser);

                        Item item = new Item(name, category, quantity);
                        if ( input != -1 ) item = item.withInputDate(new Date(input));
                        if ( expiration != -1 ) item = item.withExpirationDate(new Date(input));
                        item = item.withOwner(owner);

                        this.dataAccess.saveItem(item);
                    }
                }
                if ( dataSetChanged ) notifyObservers();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
