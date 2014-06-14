package com.maxclique.tuesday.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Austin on 6/13/2014.
 */

public class JSONParser<JSONType> {
    private JSONType jsonObject;
    public JSONParser(InputStream is, int len, JSONFactory<JSONType> factory)
            throws IOException, JSONException {
        // Convert the InputStream into a string
        Reader reader = new InputStreamReader(is, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        jsonObject = factory.create(new String(buffer));
    }

    public JSONParser(String string, JSONFactory<JSONType> factory) throws JSONException {
        jsonObject = factory.create(string);
    }

    public JSONType getJSON() {
        return jsonObject;
    }

    public static interface JSONFactory<T> {
        T create(String s) throws JSONException;
    }

    public static class JSONObjectFactory implements JSONFactory<JSONObject> {
        public JSONObject create(String s) throws JSONException {
            return new JSONObject(s);
        }
    }

    public static class JSONArrayFactory implements JSONFactory<JSONArray> {
        public JSONArray create(String s) throws JSONException {
            return new JSONArray(s);
        }
    }
}
