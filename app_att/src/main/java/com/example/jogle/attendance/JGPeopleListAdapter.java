package com.example.jogle.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by jogle on 15/8/8.
 */
public class JGPeopleListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater listContainer;
    private List<Map<String, Object>> listItems;

    public JGPeopleListAdapter(Context context, List<Map<String, Object>> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.jg_list_item,null);

        TextView name = (TextView) view.findViewById(R.id.title);
        name.setText((String) listItems.get(i).get("name"));

        TextView info = (TextView) view.findViewById(R.id.info);
        info.setText((String) listItems.get(i).get("department"));

        TextView signedBar = (TextView) view.findViewById(R.id.signed);
        TextView unsignedBar = (TextView) view.findViewById(R.id.unsigned);
        int passedDays = ((Integer) listItems.get(i).get("passedDays")).intValue();
        int signedDays = ((Integer) listItems.get(i).get("signedDays")).intValue();

        TextView signedDaysText = (TextView) view.findViewById(R.id.sdays);
        signedDaysText.setText(String.valueOf(signedDays));

        TextView unsignedDaysText = (TextView) view.findViewById(R.id.usdays);
        unsignedDaysText.setText(String.valueOf(passedDays - signedDays));

        if (passedDays > 0) {
            signedBar.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT, passedDays - signedDays
            ));
            signedBar.setBackgroundColor(0xAB79FF56); // green
            unsignedBar.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT, signedDays
            ));
            unsignedBar.setBackgroundColor(0xB6FF5E3A); // red
        }
        else {
            signedBar.setBackgroundColor(0xffcfcfcf); // gray
            unsignedBar.setBackgroundColor(0xffcfcfcf); //gray
        }
        return view;
    }
}
