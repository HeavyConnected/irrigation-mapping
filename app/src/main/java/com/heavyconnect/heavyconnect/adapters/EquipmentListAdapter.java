package com.heavyconnect.heavyconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.heavyconnect.heavyconnect.R;
import com.heavyconnect.heavyconnect.entities.Equipment;

import java.util.ArrayList;
import java.util.List;


public class EquipmentListAdapter extends ArrayAdapter<Equipment> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Equipment> mEquips = new ArrayList<Equipment>();

    public EquipmentListAdapter(Context context, List<Equipment> objects) {
        super(context, R.layout.equip_list_item, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mEquips = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.equip_list_item, null);

        Equipment equip = mEquips.get(position);

        TextView itemName = (TextView) convertView.findViewById(R.id.equip_list_item_tx);
        itemName.setText(equip.getName());

        ImageView itemImg = (ImageView) convertView.findViewById(R.id.equip_list_item_img);
        switch(equip.getStatus()){
            case Equipment.STATUS_OK:
                itemImg.setBackgroundColor(mContext.getResources().getColor(R.color.regular_green));
                break;
            case Equipment.STATUS_SERVICE:
                itemImg.setBackgroundColor(mContext.getResources().getColor(R.color.regular_yellow));
                break;
            case Equipment.STATUS_BROKEN:
                itemImg.setBackgroundColor(mContext.getResources().getColor(R.color.regular_red));
                break;
        }

        return convertView;
    }

    @Override
    public int getCount(){
        return mEquips == null? 0 : mEquips.size();
    }

    @Override
    public void add(Equipment equip){
        mEquips.add(equip);
        notifyDataSetChanged();
    }
}
