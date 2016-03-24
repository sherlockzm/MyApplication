package com.example.dawan.near02;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by dawan on 2016/2/23.
 */
public class ShowRecord extends AppCompatActivity {

    private TextView tv_title;
    private TextView tv_pay;
    private TextView tv_detail;
    private TextView tv_helperName;
    private TextView tv_helperTel;
    private TextView tv_giveHelpName;
    private TextView tv_giveHelpTel;
    private TextView tv_scoreText;

    private TextView tv_score_station;

    private TextView tv_showScore;

    private Button btn_complete;
    private ImageButton btn_delete;

    private RatingBar ratingBar;
    private ImageButton btn_getScore;

    private ImageButton ibtn_tel;

    private RatingBar ratingBarOther;
    private TextView tv_scoreOther;
    private TextView tv_scoreOtherShow;

    private float score = 0;
    private float requesterScore;
    private float helperScore;

    private int progressNow;
    private String scoreID;

    private TextView tv_recordName;

    String pUserName = "";
    String pUserTel = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showrecord);

        init();

        final HelpContext helpContext = (HelpContext) getIntent().getSerializableExtra("HelpContext");        //传递了一个helpContext对象过来
        final User pUser = (User) getIntent().getSerializableExtra("UserContext");
        if (pUser != null) {
            pUserName = pUser.getUsername();
            pUserTel = pUser.getMobilePhoneNumber();
        } else {
            pUserName = "";
            pUserTel = "";
        }
        final int boo = getIntent().getIntExtra("Boo", 1);


        ///////////////////////////按钮事件
        final String helpContextId = helpContext.getObjectId();
        final String helperId = helpContext.getHelperId();
        progressNow = helpContext.getIscomplete();

        ///////////////////////////判断是否已经完成，添加评论。
        //获取该对象的状态

        if (boo == 0) {//为0则显示自己帮别人的列表,自己是帮助者
            tv_giveHelpName.setText("求助者昵称：");
            tv_giveHelpTel.setText("求助者电话：");
            btn_complete.setText("确认删除");

        }
        tv_title.setText(helpContext.getSimple_title());
        tv_pay.setText(helpContext.getPay().toString());
        tv_helperName.setText(pUserName);
        tv_helperTel.setText(pUserTel);
        tv_detail.setText(helpContext.getDetail());

        switch (progressNow) {
            case 0:
                tv_score_station.setText("新请求");
                break;
            case 1:
                tv_score_station.setText("进行中");
                break;
            case 2:
                tv_score_station.setText("已完成");
                break;
            case 3:
                tv_score_station.setText("取消中");
            case 4:
                tv_score_station.setText("已过期");
                break;
            default:
                break;
        }


        switch (progressNow) {//根据该求助的状态控制界面显示
            case 2: {//如果该求助已完成，则显示评分按钮,同时隐藏完成按钮，删除按钮，
                btn_getScore.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                btn_complete.setVisibility(View.GONE);
                btn_delete.setVisibility(View.GONE);
                tv_scoreText.setVisibility(View.VISIBLE);

                /////////////////////////////提前获取该求助是否已评分
                BmobQuery<Score> queryScore = new BmobQuery<Score>();
                queryScore.addWhereEqualTo("helpContextId", helpContextId);
                queryScore.findObjects(ShowRecord.this, new FindListener<Score>() {
                            @Override
                            public void onSuccess(List<Score> list) {
                                if (list.size() > 0) {
                                    Score lScore = list.get(0);
                                    requesterScore = lScore.getRequesterScore();
                                    helperScore = lScore.getHelperScore();
                                    scoreID = lScore.getObjectId();
                                } else {
                                    Log.e("Score Record", "Can't Find it");
                                }

                                /////////////////
                                if (boo == 0 && helperScore != 0) {
                                    ratingBarOther.setRating(helperScore);
                                    ratingBarOther.setIsIndicator(true);
                                    ratingBarOther.setVisibility(View.VISIBLE);
                                    tv_scoreOther.setVisibility(View.VISIBLE);
                                    tv_scoreOtherShow.setText(helperScore + "");
                                } else if (boo == 1 && requesterScore != 0) {
                                    ratingBarOther.setRating(requesterScore);
                                    ratingBarOther.setIsIndicator(true);
                                    ratingBarOther.setVisibility(View.VISIBLE);
                                    tv_scoreOther.setVisibility(View.VISIBLE);
                                    tv_scoreOtherShow.setText(requesterScore + "");
                                }

                                //////////
                                if (boo == 0 && requesterScore != 0) {//我是帮助者，并且求助者的评分不等于0（即系已经评分的情况下）显示评分，隐藏按钮//当前状态为显示
                                    ratingBar.setRating(requesterScore);
                                    ratingBar.setIsIndicator(true);
                                    btn_getScore.setVisibility(View.GONE);
//                            btn_complete.setVisibility(View.INVISIBLE);
//                            btn_delete.setVisibility(View.INVISIBLE);
                                    tv_scoreText.setVisibility(View.VISIBLE);
                                    tv_showScore.setText(requesterScore + "");

                                    ///////////////对方对我的评分


                                } else if (boo == 1 && helperScore != 0) {//我是求助者，并且帮助者评分不为0（我已经对其评分）的情况下//当前状态为显示
                                    ratingBar.setRating(helperScore);
                                    ratingBar.setIsIndicator(true);
                                    new Function().setButton(new ImageButton[]{btn_delete, btn_getScore}, btn_complete, false);
                                    tv_scoreText.setVisibility(View.VISIBLE);
                                    tv_showScore.setText("" + helperScore);
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                Log.e("get score record fail", "not this record!");

                            }
                        }

                );
                break;
            }
            case 0: {
                btn_complete.setVisibility(View.GONE);
                break;
            }
            case 1: {
                if (boo == 0) {
                    tv_scoreText.setText("如不能按要求完成，请电话联系求助者发起删除请求。");
                } else {
                    tv_scoreText.setText("顺便帮众正全力为你解决问题，请稍后。");
                    new Function().setButton(new ImageButton[]{btn_delete}, btn_complete, true);
                }
                break;
            }
            case 3: {
                if (boo == 0) {//求助者请求撤销求助
                    tv_scoreText.setText("求助者请求撤销求助。大侠，你愿意接受该请求吗？");
                    new Function().setButton(btn_complete, true);
                } else {
                    tv_scoreText.setText("正在等待大侠确认你的取消帮助请求。");
                    new Function().setButton(btn_complete, false);

                }
                break;
            }
            default:
                break;
        }
        ////////////////////////////////

        score = ratingBar.getRating();//获取默认评分
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                score = rating;//取得用户评分
            }
        });

        ibtn_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tel = tv_helperTel.getText().toString();
                if (tel.trim().equals("")) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                    startActivity(intent);
                }
            }
        });

        btn_getScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Score", "" + score);
                BmobQuery<Score> queryScore = new BmobQuery<Score>();
                queryScore.addWhereEqualTo("helpContextId", helpContextId);
                new Function().checkCacaheScore(ShowRecord.this, queryScore);
                queryScore.findObjects(ShowRecord.this, new FindListener<Score>() {
                    @Override
                    public void onSuccess(List<Score> list) {
                        if (list.size() > 0) {
                            Score lScore = list.get(0);
                            requesterScore = lScore.getRequesterScore();
                            helperScore = lScore.getHelperScore();
                            scoreID = lScore.getObjectId();
                            if (boo == 0) {
                                if (requesterScore != 0) {
                                    Toast.makeText(ShowRecord.this, "你已经进行过评分。", Toast.LENGTH_SHORT).show();

                                } else {
                                    addScore(lScore, scoreID, 0);
                                }
                            } else {
                                if (helperScore != 0) {
                                    Log.e("ScoreSave", "Save fail");
                                    Toast.makeText(ShowRecord.this, "你已经进行过评分。", Toast.LENGTH_SHORT).show();

                                } else {
                                    addScore(lScore, scoreID, 1);

                                }
                            }
                        } else {
                            Log.e("list", "More than one lise.");
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e("get score record fail", "not this record!");

                    }
                });
            }
        });


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boo == 1) {//我是求助者
                    BmobQuery<HelpContext> query = new BmobQuery<HelpContext>();
                    query.getObject(ShowRecord.this, helpContextId, new GetListener<HelpContext>() {
                        @Override
                        public void onSuccess(HelpContext helpContext) {
                            int progress = helpContext.getIscomplete();
                            switch (progress) {
                                case 0: {
                                    helpContext.setObjectId(helpContextId);
                                    helpContext.delete(ShowRecord.this, new DeleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e("Change", "Delete Success!");
                                            Toast.makeText(ShowRecord.this, "记录已删除.", Toast.LENGTH_LONG).show();
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            Log.e("Change", "Delete Fail!");

                                        }
                                    });
                                    break;
                                }
                                case 1: {
                                    //改变iscomplete 为3
                                    helpContext.setIscomplete(3);
                                    btn_complete.setVisibility(View.GONE);
                                    helpContext.update(ShowRecord.this, helpContextId, new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e("Update", "Change isComplete Success!");
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {

                                        }
                                    });
                                    String message = "求助者请求取消求助。你是否同意。";
                                    Toast.makeText(ShowRecord.this, "提交援助者确认.", Toast.LENGTH_LONG).show();
                                    new Function().pushMessage(ShowRecord.this, helperId, message);
                                    Toast.makeText(ShowRecord.this, "你正在申请删除，请耐心等待确认。", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                case 2: {
                                    Toast.makeText(ShowRecord.this, "已完成，不可删除。", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                case 3: {
                                    Toast.makeText(ShowRecord.this, "你已经申请删除，请耐心等待确认。", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                default:
                                    break;

                            }
//
//                                String message = "求助者请求取消求助。你是否同意。";
//                                Toast.makeText(ShowRecord.this, "提交援助者确认.", Toast.LENGTH_LONG).show();
//                                new Function().pushMessage(ShowRecord.this, helperId, message);
                        }

                        /////////////////////////////////////////////////////////////////////
                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(ShowRecord.this, "该求助状态不正确，可能是姿势不对。", Toast.LENGTH_SHORT).show();

                        }
                    });
                } else {
                    Toast.makeText(ShowRecord.this, "请电话联系求助者申请删除该求助。.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boo == 0) {
                    helpContext.setObjectId(helpContextId);
                    BmobQuery<HelpContext> delQuery = new BmobQuery<HelpContext>();
                    delQuery.getObject(ShowRecord.this, helpContextId, new GetListener<HelpContext>() {
                        @Override
                        public void onSuccess(HelpContext helpContext) {
                            if (helpContext.getIscomplete() == 3) {
                                helpContext.delete(ShowRecord.this, new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.e("Delete", "Delete Success");
                                        Toast.makeText(ShowRecord.this, "该求助已成功删除.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Log.e("Delete", "Delete Fail.");
                                        Toast.makeText(ShowRecord.this, "删除失败。", Toast.LENGTH_SHORT).show();


                                    }
                                });
                            } else {
                                Toast.makeText(ShowRecord.this, "如当初一时手快，请点击左侧的申请删除按钮，不能单方面删除喔。", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });

                } else {
                    //确认完成的情况

                    //检查状态是否为进行中
                    BmobQuery<HelpContext> queryProgress = new BmobQuery<HelpContext>();
                    queryProgress.getObject(ShowRecord.this, helpContextId, new GetListener<HelpContext>() {
                        @Override
                        public void onSuccess(HelpContext qqhelpContext) {
                            if (qqhelpContext.getIscomplete() == 1) {
                                helpContext.setIscomplete(2);
                                helpContext.update(ShowRecord.this, helpContextId, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.e("Complete", "Completed!");
                                        Toast.makeText(ShowRecord.this, "该求助已顺利完成，祝天天好心情喔！", Toast.LENGTH_SHORT).show();
                                        Log.e("id", helpContext.getRequestid() + "//" + helperId + "//" + helpContextId);

                                        Score newScore = new Score();
                                        newScore.setRequesterId(helpContext.getRequestid());
                                        newScore.setRequesterScore(0);
                                        newScore.setHelperId(helperId);
                                        newScore.setHelperScore(0);
                                        newScore.setHelpContextId(helpContextId);
                                        newScore.save(ShowRecord.this, new SaveListener() {
                                            @Override
                                            public void onSuccess() {
                                                Log.e("Score", "Save OK");
                                                ///////////推送给援助者
                                                String thank = "在你的帮助下，我的问题已解决，谢谢你。";
                                                new Function().pushMessage(ShowRecord.this, helperId, thank);
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                Log.e("Score", "Save fail");
                                            }
                                        });
                                        btn_getScore.setVisibility(View.VISIBLE);
                                        ratingBar.setVisibility(View.VISIBLE);
                                        new Function().setButton(new ImageButton[]{btn_delete}, btn_complete, false);
                                        //执行余额变动
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Log.e("Complete", "Complete Error!");
                                        Toast.makeText(ShowRecord.this, "该请求已完成或已删除。", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } else {
                                Toast.makeText(ShowRecord.this, "该求助状态已改变，请刷新。", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(ShowRecord.this, "记录为空。", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });

        tv_recordName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_recordName.getText().toString().trim() != "") {
                    BmobQuery<User> userQuery = new BmobQuery<User>();
                    userQuery.getObject(ShowRecord.this, pUser.getObjectId(), new GetListener<User>() {
                        @Override
                        public void onSuccess(User user) {
                            Intent intent = new Intent(ShowRecord.this, Info.class);
                            intent.putExtra("INFO", user);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            }
        });
    }

    /////////登记金额变动。
    public void pay() {
    }

    public void addScore(Score nScore, String scoreId, int role) {
        if (role == 0) {

            nScore.setRequesterScore(score);

            nScore.update(ShowRecord.this, scoreId, new UpdateListener() {
                @Override
                public void onSuccess() {
                    Log.e("ScoreSave", "Save requester");
                    Toast.makeText(ShowRecord.this, "评分已成功，你给对方的评分为" + score + "分。", Toast.LENGTH_SHORT).show();
                    ratingBar.setIsIndicator(true);
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.e("ScoreSave", "Fail");

                }
            });

        } else {

            nScore.setHelperScore(score);
//            nScore.setValue("helperScore", score);

            nScore.update(ShowRecord.this, scoreId, new UpdateListener() {
                @Override
                public void onSuccess() {
                    Log.e("ScoreSave", "Save requester");
                    Toast.makeText(ShowRecord.this, "评分已成功，你给对方的评分为" + score + "分。", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.e("ScoreSave", "Fail");

                }
            });


        }
        new Function().setButton(new ImageButton[]{btn_delete, btn_getScore}, btn_complete, false);
        tv_showScore.setText("" + score);
        tv_showScore.setVisibility(View.VISIBLE);
    }

    private void init() {
        /////////////////////////////

        tv_title = (TextView) findViewById(R.id.tv_record_title);
        tv_pay = (TextView) findViewById(R.id.tv_record_pay);
        tv_detail = (TextView) findViewById(R.id.tv_record_detail);
        tv_helperName = (TextView) findViewById(R.id.tv_record_helperName);
        tv_helperTel = (TextView) findViewById(R.id.tv_record_helperTel);
        tv_giveHelpName = (TextView) findViewById(R.id.tv_giveHelpName);
        tv_giveHelpTel = (TextView) findViewById(R.id.tv_giveHelpTel);
        tv_showScore = (TextView) findViewById(R.id.score_show);
        tv_scoreText = (TextView) findViewById(R.id.tv_scoreText);
        tv_score_station = (TextView) findViewById(R.id.tv_record_station);

        tv_scoreOther = (TextView) findViewById(R.id.tv_scoreTextOther);
        tv_scoreOtherShow = (TextView) findViewById(R.id.score_showOther);
        ratingBarOther = (RatingBar) findViewById(R.id.rb_scoreOther);
        ibtn_tel = (ImageButton) findViewById(R.id.ibtn_tel);

        btn_complete = (Button) findViewById(R.id.btn_complete);
        btn_delete = (ImageButton) findViewById(R.id.btn_delete);
        /////////////////////////初始化界面
        //////////////////////////////////////////评分相关
//初始化评分控件
        ratingBar = (RatingBar) findViewById(R.id.rb_score);
        btn_getScore = (ImageButton) findViewById(R.id.ibtn_getScore);

        tv_recordName = (TextView)findViewById(R.id.tv_record_helperName);

    }

    protected void onDestroy(){
        super.onDestroy();
        pUserName = "";
        pUserTel = "";

    }


}
