package com.maycur.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class Picker extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("show")) {
            return true;
        }
        return false;
    }
}
