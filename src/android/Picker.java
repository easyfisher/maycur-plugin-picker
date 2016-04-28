package com.maycur.plugin;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Picker extends CordovaPlugin {

    CallbackContext mCallbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("show")) {
            JSONArray nodesArray = args.getJSONArray(0);
            JSONArray indexArray = null;
            if (args.length() >= 2) {
                indexArray = args.getJSONArray(1);
            }
            if (nodesArray.length() <= 0)
                return false;

            int[] indexes = null;
            if (indexArray != null && indexArray.length() > 0) {
                indexes = new int[indexArray.length()];
                for (int i = 0; i < indexArray.length(); i++) {
                    indexes[i] = indexArray.getInt(i);
                }
            }

            PickerNode[] nodes = new PickerNode[nodesArray.length()];
            for (int i = 0; i < nodesArray.length(); i++) {
                nodes[i] = new PickerNode(nodesArray.getJSONObject(i));
            }
            mCallbackContext = callbackContext;
            showDialog(nodes, indexes);
            return true;
        }
        return false;
    }

    private void showDialog(PickerNode[] nodes, int[] defaultIndexes) {
        PickerDialog dialog = new PickerDialog(cordova.getActivity(), nodes, defaultIndexes);
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
