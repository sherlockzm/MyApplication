package com.example.dawan.near02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.karumi.expandableselector.ExpandableItem;
import com.karumi.expandableselector.ExpandableSelector;
import com.karumi.expandableselector.OnExpandableItemClickListener;
import com.yalantis.phoenix.PullToRefreshView;

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

    private ImageButton btn_show_request;
    private ImageButton btn_show_give;
//    private TextView tv_title;

    private String curUserId;

    private User pUser;

    private String userID = "";
    private PullToRefreshView mPullToRefreshView;
    final static int REFRESH_DELAY = 500;
    private int role;

    private ExpandableSelector sizesExpandableSelector;
    private ExpandableSelector helpExpandableSelector;

    private int i = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myhelprecord);

//        tv_title = (TextView) findViewById(R.id.tv_showRecordList_title);
        listView = (ListView) findViewById(R.id.myHelpRecord);
        btn_show_request = (ImageButton) findViewById(R.id.btn_showRequestList);
        btn_show_give = (ImageButton) findViewById(R.id.btn_showGiveHelpList);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh_record);
        role = 0;

        sizesExpandableSelector = (ExpandableSelector) findViewById(R.id.es_sizes);
        helpExpandableSelector = (ExpandableSelector) findViewById(R.id.es_help);

        if(BmobUser.getCurrentUser(MyHelpRecord.this,User.class) != null){
            curUserId = BmobUser.getCurrentUser(MyHelpRecord.this, User.class).getObjectId();
        }
        if (curUserId != null) {
            showList(MyHelpRecord.this, "requestid", curUserId, 1, 5);
        }
        /////////////////
        if (curUserId != null) {
            initializeRequestExpandableSelector(curUserId);//展开列表选择
        }
        if (curUserId != null) {
            initializeHelpExpandableSelector(curUserId);
        }
        ///////////
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        Log.e("Fresh", "Runing Fresh.role = " + role);
                        if (role == 1) {
                            showList(MyHelpRecord.this, "requestid", curUserId, 1, i);
                        } else {
                            showList(MyHelpRecord.this, "helperId", curUserId, 0, i);
                        }
                    }
                }, REFRESH_DELAY);
            }
        });

        //////////

        btn_show_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseAll();
//                tv_title.setText("求助列表");
                showList(MyHelpRecord.this, "requestid", curUserId, 1, 5);

            }
        });
        btn_show_give.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseAll();
//                tv_title.setText("帮助列表");
                showList(MyHelpRecord.this, "helperId", curUserId, 0, 5);
            }
        });


    }

    public void showList(final Context context, String row, String id, final int boo, final int iscomplete) {

        role = boo;
//        if (boo == 0) {
//            tv_title.setText("帮助列表");
//        } else {
//            tv_title.setText("求助列表");
//        }
        BmobQuery<HelpContext> query = new BmobQuery<HelpContext>();//交易列表
        query.addWhereEqualTo(row, id);
        ///添加排序及状态排序
//        BmobQuery<HelpContext> query1 = new BmobQuery<>();
        query.order("iscomplete");
        query.order("-time");
        if (iscomplete < 5) {
            query.addWhereEqualTo("iscomplete", iscomplete);
        }
        ////

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

                        final HelpContext getHelpContext = (HelpContext) helpList.get(position);

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
                                Log.e("Info", "HERE");
                                Log.e("User", "Got it");
                                Log.e("Info", pUser.getMobilePhoneNumber() + "/////");

                                Intent intent = new Intent(MyHelpRecord.this, ShowRecord.class);
                                intent.putExtra("HelpContext", getHelpContext);
                                intent.putExtra("UserContext", pUser);
                                intent.putExtra("Boo", boo);
                                intent.addCategory("SHOWHELP");
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                pUser = null;
                                Intent intent = new Intent(MyHelpRecord.this, ShowRecord.class);
                                intent.putExtra("HelpContext", getHelpContext);
                                intent.putExtra("UserContext", pUser);
                                intent.putExtra("Boo", boo);
                                intent.addCategory("SHOWHELP");
                                startActivity(intent);
                                Log.e("User", "Can't Got it.");
                            }
                        });

                    }
                });

            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }



    /////////////////////////////求助列表////////////////////////
    private void initializeRequestExpandableSelector(final String curUserId) {

        List<ExpandableItem> expandableItems = new ArrayList<ExpandableItem>();
        expandableItems.add(new ExpandableItem("求助列表"));
        expandableItems.add(new ExpandableItem("新请求"));
        expandableItems.add(new ExpandableItem("进行中"));
        expandableItems.add(new ExpandableItem("已完成"));
        expandableItems.add(new ExpandableItem("取消中"));
        expandableItems.add(new ExpandableItem("已过期"));
        sizesExpandableSelector.showExpandableItems(expandableItems);

        sizesExpandableSelector.setOnExpandableItemClickListener(new OnExpandableItemClickListener() {
            @Override
            public void onExpandableItemClickListener(int index, View view) {
                switch (index) {
                    case 0:
                        i = 5;
                        ExpandableItem zeroItem = sizesExpandableSelector.getExpandableItem(0);
                        swipeFirstItem(0, zeroItem);
                        showList(MyHelpRecord.this, "requestid", curUserId, 1, 5);//全部列出来
                        break;
                    case 1:
                        i = 0;
                        ExpandableItem firstItem = sizesExpandableSelector.getExpandableItem(1);
                        swipeFirstItem(1, firstItem);
                        showList(MyHelpRecord.this, "requestid", curUserId, 1, 0);//新请求
                        break;
                    case 2:
                        i = 1;
                        ExpandableItem secondItem = sizesExpandableSelector.getExpandableItem(2);
                        swipeFirstItem(2, secondItem);
                        showList(MyHelpRecord.this, "requestid", curUserId, 1, 1);//处理中

                        break;
                    case 3:
                        i = 2;
                        ExpandableItem fourthItem = sizesExpandableSelector.getExpandableItem(3);
                        swipeFirstItem(3, fourthItem);
                        showList(MyHelpRecord.this, "requestid", curUserId, 1, 2);//已完成
                        break;
                    case 4:
                        i = 3;
                        ExpandableItem fifth = sizesExpandableSelector.getExpandableItem(4);
                        swipeFirstItem(4, fifth);
                        showList(MyHelpRecord.this, "requestid", curUserId, 1, 3);//请求删除
                        break;
                    case 5:
                        i = 4;
                        ExpandableItem sixth = sizesExpandableSelector.getExpandableItem(5);
                        swipeFirstItem(5, sixth);
                        showList(MyHelpRecord.this, "requestid", curUserId, 1, 4);//已过期
                        break;
                    case 6:
                        i = 5;
                        ExpandableItem seventh = sizesExpandableSelector.getExpandableItem(6);
                        swipeFirstItem(6, seventh);
                        showList(MyHelpRecord.this, "requestid", curUserId, 1, 5);//已过期
                        break;
                    default:
                }
//                sizesExpandableSelector.collapse();
                collapseAll();
            }

            private void swipeFirstItem(int position, ExpandableItem clickedItem) {
                ExpandableItem firstItem = sizesExpandableSelector.getExpandableItem(0);
                sizesExpandableSelector.updateExpandableItem(0, clickedItem);
//                sizesExpandableSelector.updateExpandableItem(position, firstItem);
            }

        });
    }

    ///////////////////帮助列表////////////////
    private void initializeHelpExpandableSelector(final String curUserId) {

        final List<ExpandableItem> expandableItems = new ArrayList<ExpandableItem>();
        expandableItems.add(new ExpandableItem("帮助列表"));
        expandableItems.add(new ExpandableItem("新请求"));
        expandableItems.add(new ExpandableItem("进行中"));
        expandableItems.add(new ExpandableItem("已完成"));
        expandableItems.add(new ExpandableItem("取消中"));
        expandableItems.add(new ExpandableItem("已过期"));
        helpExpandableSelector.showExpandableItems(expandableItems);

        helpExpandableSelector.setOnExpandableItemClickListener(new OnExpandableItemClickListener() {
            @Override
            public void onExpandableItemClickListener(int index, View view) {

                switch (index) {
                    case 0:
                        i = 5;
                        Log.e("Item", "0");
                        ExpandableItem zeroItem = helpExpandableSelector.getExpandableItem(0);
                        swipeFirstItem(0, zeroItem);
                        showList(MyHelpRecord.this, "helperId", curUserId, 0, 5);//全部列出来
                        break;
                    case 1:
                        i = 0;
                        ExpandableItem firstItem = helpExpandableSelector.getExpandableItem(1);
                        swipeFirstItem(1, firstItem);
                        showList(MyHelpRecord.this, "helperId", curUserId, 0, 0);//新请求
                        break;
                    case 2:
                        i = 1;
                        ExpandableItem secondItem = helpExpandableSelector.getExpandableItem(2);
                        swipeFirstItem(2, secondItem);
                        showList(MyHelpRecord.this, "helperId", curUserId, 0, 1);//处理中

                        break;
                    case 3:
                        i = 2;
                        ExpandableItem fourthItem = helpExpandableSelector.getExpandableItem(3);
                        swipeFirstItem(3, fourthItem);
                        showList(MyHelpRecord.this, "helperId", curUserId, 0, 2);//已完成
                        break;
                    case 4:
                        i = 3;
                        ExpandableItem fifth = helpExpandableSelector.getExpandableItem(4);
                        swipeFirstItem(4, fifth);
                        showList(MyHelpRecord.this, "helperId", curUserId, 0, 3);//请求删除
                        break;
                    case 5:
                        i = 4;
                        ExpandableItem sixth = helpExpandableSelector.getExpandableItem(5);
                        swipeFirstItem(5, sixth);
                        showList(MyHelpRecord.this, "helperId", curUserId, 0, 4);//已过期
                        break;

                    default:
                }
//                helpExpandableSelector.collapse();
                collapseAll();
            }

            private void swipeFirstItem(int position, ExpandableItem clickedItem) {
                ExpandableItem firstItem = helpExpandableSelector.getExpandableItem(0);
                helpExpandableSelector.updateExpandableItem(0, clickedItem);
//                sizesExpandableSelector.updateExpandableItem(position, firstItem);
            }

        });
    }


    private void collapseAll() {
        helpExpandableSelector.collapse();

        sizesExpandableSelector.collapse();
    }


}
