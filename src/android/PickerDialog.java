package com.maycur.plugin;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Easter on 4/11/16.
 */
public class PickerDialog extends AlertDialog implements NumberPicker.OnValueChangeListener {

    private NumberPicker[] mPickers;
    private PickerNode[] mNodes;
    private SparseArray<PickerNode[]> mNodesMap;

    private OnPickListener mListener;

    public interface OnPickListener {
        void onPick(List<Integer> result);
    }

    public void setOnPickListener(OnPickListener listener) {
        mListener = listener;
    }

    public PickerDialog(Context context, PickerNode[] nodes) {
        super(context);

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(getIdentifier("dialog_picker", "layout"), null);

        if (nodes != null && nodes.length > 0) {
            mNodes = nodes;

            int maxDepth = 1;
            for (PickerNode node : nodes) {
                if (maxDepth < node.depth)
                    maxDepth = node.depth;
            }

            mPickers = new NumberPicker[maxDepth];
            LinearLayout lyt = (LinearLayout) view.findViewById(getIdentifier("lyt_picker_container", "id"));
            for (int i = 0; i < maxDepth; i++) {
                NumberPicker picker = new com.maycur.plugin.NumberPicker(context);
                picker.setLayoutParams(new TableLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1));
                picker.setMinValue(0);
                picker.setOnValueChangedListener(this);
                picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                picker.setBackgroundColor(Color.parseColor("#ffffff"));
                lyt.addView(picker);

                mPickers[i] = picker;
            }

            setDefaultData();
        }


        Button btnOK = (Button) view.findViewById(getIdentifier("btn_ok", "id"));
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    List<Integer> results = new ArrayList<Integer>();
                    for (int i = 0; i < mPickers.length; i++) {
                        if (mNodesMap.get(i) == null) {
                            break;
                        }
                        results.add(mPickers[i].getValue());
                    }
                    mListener.onPick(results);
                }
                dismiss();
            }
        });
        String okText = getContext().getResources().getString(getIdentifier("button_ok", "string"));
        if (okText != null && !okText.trim().isEmpty()) {
            btnOK.setText(okText);
        }

        Button btnCancel = (Button) view.findViewById(getIdentifier("btn_cancel", "id"));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        String cancelText = getContext().getResources().getString(getIdentifier("button_cancel", "string"));
        if (cancelText != null && !cancelText.trim().isEmpty()) {
            btnCancel.setText(cancelText);
        }

        setView(view);
    }

    private void setDefaultData() {
        mNodesMap = new SparseArray<PickerNode[]>();
        PickerNode[] columnNodes = mNodes;
        setData(0, columnNodes);

        for (int i = 1; i < mPickers.length; i++) {
            if (columnNodes != null && columnNodes.length > 0) {
                columnNodes = columnNodes[0].childs;
            }
            setData(i, columnNodes);
        }
    }

    private void setData(int column, PickerNode[] nodes) {
        NumberPicker picker = mPickers[column];
        picker.setValue(0);
        if (nodes == null || nodes.length <= 0) {
            mNodesMap.delete(column);

            picker.setMaxValue(0);
            picker.setDisplayedValues(new String[]{" "});
        } else {
            mNodesMap.put(column, nodes);

            String[] names = new String[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                names[i] = nodes[i].name;
            }
            if (picker.getMaxValue() > nodes.length - 1) {
                picker.setMaxValue(nodes.length - 1);
                picker.setDisplayedValues(names);
            } else {
                picker.setDisplayedValues(names);
                picker.setMaxValue(nodes.length - 1);
            }
        }
    }

    private int getIdentifier(String name, String type) {
        return getContext().getResources().getIdentifier(name, type, getContext().getPackageName());
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.d("PickerDialog", "selected " + newVal);
        int column = 0;
        for (int i = 0; i < mPickers.length; i++) {
            if (mPickers[i] == picker) {
                column = i;
                break;
            }
        }

        for (int i = column + 1; i < mPickers.length; i++) {
            PickerNode[] parents = mNodesMap.get(i - 1);
            if (parents == null || parents.length <= 0) {
                setData(i, null);
                continue;
            }

            PickerNode[] childs = parents[mPickers[i - 1].getValue()].childs;
            setData(i, childs);
        }
    }
}
