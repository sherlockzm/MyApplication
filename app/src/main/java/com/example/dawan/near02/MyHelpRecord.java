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

    private String helperId = "";
    private String userName = "";
    private String userTel = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myhelprecord);

        tv_title = (TextView)findViewById(R.id.tv_showRecordList_title);
        listView = (ListView) findViewById(R.id.myHelpRecord);
        btn_show_request = (Button)findViewById(R.id.btn_showRequestList);
        btn_show_give = (Button)findViewById(R.id.btn_showGiveHelpList);
        final String curUserId = BmobUser.getCurrentUser(MyHelpRecord.this, User.class).getObjectId();
        showList(MyHelpRecord.this,"requestid",curUserId,1);

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

    public  void showList(Context context,String row,String id, final int boo){

        BmobQuery<HelpContext> query = new BmobQuery<HelpContext>();//交易列表
        query.addWhereEqualTo(row, id);
        query.findObjects(MyHelpRecord.this, new FindListener<HelpContext>() {
            @Override
            public void onSuccess(List<HelpContext> list) {
                helpList.clear();
                Log.e("Record", list.size() + "");
                for (HelpContext helpContext : list) {
                    helpList.add(helpContext);
                }
                HelpAdapter helpAdapter = new HelpAdapter(MyHelpRecord.this, R.layout.help_item, helpList);
                listView.setAdapter(helpAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        HelpContext gethelpContext = (HelpContext) helpList.get(position);

                        //添加帮助者信息传递给详细显示
                        String helpContextId = gethelpContext.getObjectId();  //取得求助信息ID
                        Log.e("contextId", helpContextId);
                        BmobQuery<TransactionRecord> queryTranRecord = new BmobQuery<TransactionRecord>();
                        queryTranRecord.addWhereEqualTo("buniessId", helpContextId);
                        queryTranRecord.findObjects(MyHelpRecord.this, new FindListener<TransactionRecord>() {
                            @Override
                            public void onSuccess(List<TransactionRecord> list) {
                                for (TransactionRecord record : list) {
                                    if (boo == 1) {                                   //判断帮助列表还是求助列表
                                        helperId = record.getHelperID().toString();
                                    }else{
                                        helperId = record.getRequestID().toString();
                                    }

                                    Log.e("Record", "helperID = " + helperId);
                                }
                            }

                            @Override
                            public void onError(int i, String s) {

                                Log.e("Record", "No This Record" + s);

                            }
                        });


                        BmobQuery<User> queryUser = new BmobQuery<User>();
                        queryUser.addWhereEqualTo("objectId", helperId);
                        queryUser.findObjects(MyHelpRecord.this, new FindListener<User>() {
                            @Override
                            public void onSuccess(List<User> list) {
                                for (BmobUser user : list) {
                                    userName = user.getUsername().toString();
                                    userTel = user.getMobilePhoneNumber().toString();

                                    Log.e("Record", "找到帮助者资料 " + user.getUsername().toString());
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                Log.e("Record", "找不到用户资料" + s);

                            }
                        });


                        ////////////////

                        Intent intent = new Intent(MyHelpRecord.this, ShowRecord.class);
                        intent.addCategory("SHOWHELP");
                        //传递数据给showRecord

//                            HelpContext gethelpContext = (HelpContext) helpList.get(position);

                        intent.putExtra("HelpContext", gethelpContext);
                        intent.putExtra("HelperName", userName);
                        intent.putExtra("HelperTel", userTel);
                        intent.putExtra("Boo",boo);

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
