package com.example.dawan.near02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by dawan on 2016/2/23.
 */
public class MyHelpRecord extends AppCompatActivity {

    //我的求助信息
    private List<HelpContext> helpList = new ArrayList<HelpContext>();

    private ListView listView;

    private Button btn_show_request;
    private Button btn_show_give;
    private TextView tv_title;

    private User pUser;
    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myhelprecord);

        tv_title = (TextView) findViewById(R.id.tv_showRecordList_title);
        listView = (ListView) findViewById(R.id.myHelpRecord);
        btn_show_request = (Button) findViewById(R.id.btn_showRequestList);
        btn_show_give = (Button) findViewById(R.id.btn_showGiveHelpList);
        final String curUserId = BmobUser.getCurrentUser(MyHelpRecord.this, User.class).getObjectId();
        showList(MyHelpRecord.this, "requestid", curUserId, 1);

        btn_show_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_title.setText("求助列表");
                showList(MyHelpRecord.this, "requestid", curUserId, 1);
            }
        });
        btn_show_give.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_title.setText("帮助列表");
                showList(MyHelpRecord.this, "helperId", curUserId, 0);
            }
        });

    }

    public void showList(final Context context, String row, String id, final int boo) {

        BmobQuery<HelpContext> query = new BmobQuery<HelpContext>();//交易列表
        query.addWhereEqualTo(row, id);
        query.findObjects(context, new FindListener<HelpContext>() {
            @Override
            public void onSuccess(List<HelpContext> list) {
                helpList.clear();
                Log.e("Record", list.size() + "");
                for (HelpContext helpContext : list) {
                    helpList.add(helpContext);
                }
                HelpAdapter helpAdapter = new HelpAdapter(context, R.layout.help_item, helpList);
                listView.setAdapter(helpAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        HelpContext getHelpContext = (HelpContext) helpList.get(position);

                        if (boo == 0) {
                            userID = getHelpContext.getRequestid();
                        } else {
                            userID = getHelpContext.getHelperId();
                        }

                        BmobQuery<User> queryUser = new BmobQuery<User>();
                        queryUser.getObject(context, userID, new GetListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                                pUser = user;
                                Log.e("User", "Got it");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Log.e("User", "Can't Got it.");

                            }
                        });

                        Intent intent = new Intent(MyHelpRecord.this, ShowRecord.class);
                        intent.addCategory("SHOWHELP");
                        //传递数据给showRecord

                        intent.putExtra("HelpContext", getHelpContext);
                        if (pUser != null) {
                            intent.putExtra("UserContext", pUser);
                        }
                        intent.putExtra("Boo", boo);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }
}
