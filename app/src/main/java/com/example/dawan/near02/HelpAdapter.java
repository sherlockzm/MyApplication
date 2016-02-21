package com.example.dawan.near02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dawan on 2016/2/18.
 */
public class HelpAdapter extends ArrayAdapter<HelpContext>{

    private LayoutInflater inflater;//画布
    private Context context;//当前上下文
    private int resourceId;

    public HelpAdapter(Context context,int textViewResourceId,List<HelpContext> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HelpContext helpContext = getItem(position);

        View view;

        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);

        }else {
            view = convertView;
        }

//        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView simple_title = (TextView)view.findViewById(R.id.tv_item_title);
        TextView pay = (TextView)view.findViewById(R.id.tv_item_pay);
        TextView detail = (TextView)view.findViewById(R.id.tv_item_detail);
        simple_title.setText(helpContext.getSimple_title());
        pay.setText(helpContext.getPay().toString());
        detail.setText(helpContext.getDetail());
        return view;
    }
}
