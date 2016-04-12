package com.maycur.plugin;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Picker extends CordovaPlugin {

    private PickerNode[] mNodes;
    CallbackContext mCallbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("show")) {
            JSONArray array = args.getJSONArray(0);
            if (array.length() <= 0)
                return false;

            mNodes = new PickerNode[array.length()];
            for (int i = 0; i < array.length(); i++) {
                mNodes[i] = new PickerNode(array.getJSONObject(i));
            }
            mCallbackContext = callbackContext;
            showDialog();
            return true;
        }
        return false;
    }

    private void showDialog() {
        PickerDialog dialog = new PickerDialog(cordova.getActivity(), mNodes);
        dialog.setOnPickListener(new PickerDialog.OnPickListener() {
            @Override
            public void onPick(List<Integer> results) {
                mCallbackContext.success(new JSONArray(results));
            }
        });
        dialog.show();
    }
}

class PickerNode {
    public String name;
    public PickerNode[] childs;
    public int depth = 1;

    public PickerNode(JSONObject data) {
        try {
            name = data.getString("name");
            if (data.has("childs")) {
                JSONArray array = data.getJSONArray("childs");
                if (array.length() > 0) {
                    childs = new PickerNode[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        childs[i] = new PickerNode(array.getJSONObject(i));
                        if (depth < childs[i].depth + 1) {
                            depth = childs[i].depth + 1;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("Picker", e.getMessage());
        }

    }
}
