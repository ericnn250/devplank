package com.ngenderic.datacollector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class CatAdapter extends ArrayAdapter<CatItem> {

    public CatAdapter(Context context, ArrayList<CatItem> categoryList) {
        super(context, 0, categoryList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.cat_spinner_row, parent, false
            );
        }

        TextView idViewFlag = convertView.findViewById(R.id.image_view_flag);
        TextView textViewName = convertView.findViewById(R.id.text_view_name);

        CatItem currentItem = getItem(position);

        if (currentItem != null) {
            idViewFlag.setText(" ");
            textViewName.setText(currentItem.getCountryName());
            Log.i("info",currentItem.getCountryName());
        }

        return convertView;
    }
}