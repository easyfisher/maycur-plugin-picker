package com.maycur.plugin;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Easter on 4/12/16.
 */
public class NumberPicker extends android.widget.NumberPicker {

    public NumberPicker(Context context) {
        super(context);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    private void updateView(View view) {
        if (view instanceof EditText) {
            ((EditText) view).setTextSize(13);
            ((EditText) view).setTextColor(Color.parseColor("#222222"));
        }
    }

}