package com.heavyconnect.heavyconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heavyconnect.heavyconnect.R;
import com.heavyconnect.heavyconnect.entities.FieldModel;

import java.util.List;

/**
 * Created by juice on 11/28/15.
 */
public class FieldListAdapter extends BaseAdapter {
    private Context mContext;
    private List<FieldModel> mFieldList;
    private LayoutInflater mLayoutInflater;

    public FieldListAdapter(Context context, List<FieldModel> fieldList) {
        mContext = context;
        mFieldList = fieldList;
        mLayoutInflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {
        return mFieldList == null ? 0 : mFieldList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFieldList == null ? null : mFieldList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FieldModel current = mFieldList.get(position);

        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.drawer_list_item, null);
        }

        TextView textView = (TextView) convertView;
        textView.setText(current.getFieldName());

        return textView;
    }
}