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

        //优化
        ViewHolder viewHolder;

        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.simple_title = (TextView)view.findViewById(R.id.tv_item_title);
            viewHolder.pay = (TextView)view.findViewById(R.id.tv_item_pay);
            viewHolder.when_request = (TextView)view.findViewById(R.id.tv_when);
            viewHolder.detail = (TextView)view.findViewById(R.id.tv_item_detail);
            viewHolder.station = (TextView)view.findViewById(R.id.tv_station);
            view.setTag(viewHolder);

        }else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.simple_title.setText(helpContext.getSimple_title());
        viewHolder.pay.setText(helpContext.getPay().toString());
        viewHolder.when_request.setText(helpContext.getUpdatedAt());
        viewHolder.detail.setText(helpContext.getDetail());

        switch (helpContext.getIscomplete()){

            case 0:
                viewHolder.station.setText("新请求");
                break;
            case 1:
                viewHolder.station.setText("进行中");
                break;
            case 2:
                viewHolder.station.setText("已完成");
                break;
            default:
                break;
        }

//        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
//        TextView simple_title = (TextView)view.findViewById(R.id.tv_item_title);
//        TextView pay = (TextView)view.findViewById(R.id.tv_item_pay);
//        TextView when_request = (TextView)view.findViewById(R.id.tv_when);
//        TextView detail = (TextView)view.findViewById(R.id.tv_item_detail);
//        simple_title.setText(helpContext.getSimple_title());
//        pay.setText(helpContext.getPay().toString());
//
//        when_request.setText(helpContext.getUpdatedAt().substring(5,16));
//        detail.setText(helpContext.getDetail());
        return view;
    }

    class ViewHolder{
        TextView simple_title;
        TextView pay;
        TextView when_request;
        TextView detail;
        TextView station;
    }
}
