package com.example.misaya.ttword;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.bean.TieBean;
import com.dou361.dialogui.listener.DialogUIItemListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Util.HttpUtil;

import static com.dou361.dialogui.DialogUIUtils.showToast;

public class MainActivity extends AppCompatActivity {

    private static final String keyfrom = "TYNsWord";
    private static final String key = "92609876";
    private static final String type = "data";
    private static final String doctype = "json";
    private static final String version = "1.1";
    private static final String baseUrl = "http://katarinar.top/tt/server/";

    private int index = 0;
    private boolean isShow = true;
    private Activity mActivity;
    private Context mContext;
    private JSONArray arr;
    private TextView tvWord,tvPhonetic,tvExplains,tvTopBefore,tvTopAfter,tvTopChoose;
    private LinearLayout layoutChoose;
    private RelativeLayout layoutLeft,layoutRight;
    //private ImageView leftView,rightView;
    private ImageView bottomBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mActivity = this;
        mContext = getApplication();
        init();
        chooseUnit("unit1");

        layoutLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index--;
                layoutRight.setVisibility(View.VISIBLE);
                if(index<=0){
                    layoutLeft.setVisibility(View.INVISIBLE);
                }
                setWord(index);
                try {
                    audioService(arr.getString(index));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        layoutRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                layoutLeft.setVisibility(View.VISIBLE);
                Log.e("RightOnClick",index + "/" + arr.length());
                if(index>=arr.length()-1){
                    layoutRight.setVisibility(View.INVISIBLE);
                }
                setWord(index);
                try {
                    audioService(arr.getString(index));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShow) {
                    hideWord();
                }else {
                    showWord();
                }
            }
        });

        layoutChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<TieBean> data = new ArrayList<>();
                for(int i=1;i<=30;i++){
                    data.add(new TieBean("Unit" + String.valueOf(i)));
                }
                DialogUIUtils.showMdBottomSheet(mActivity, false, "Choose", data, 4, new DialogUIItemListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        chooseUnit("unit" + String.valueOf(position + 1));
                        //showToast(text + "-" + position);
                    }
                }).show();
            }
        });

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layoutCentent);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("CenterOnClick","OnClickListener");
                try {
                    audioService(arr.getString(index));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() {
        DialogUIUtils.init(mContext);
        tvWord = (TextView) findViewById(R.id.tvWord);
        tvPhonetic = (TextView) findViewById(R.id.tvPhonetic);
        tvExplains = (TextView) findViewById(R.id.tvMean);
        //leftView = (ImageView) findViewById(R.id.imgLeftBtn);
        //rightView = (ImageView) findViewById(R.id.imgRightBtn);
        bottomBtn = (ImageView) findViewById(R.id.imgBottomBtn);
        tvTopBefore = (TextView) findViewById(R.id.tvTopBefore);
        tvTopAfter = (TextView) findViewById(R.id.tvTopAfter);
        tvTopChoose = (TextView) findViewById(R.id.tvTopChoose);
        layoutChoose = (LinearLayout) findViewById(R.id.layoutChoose);
        layoutLeft = (RelativeLayout) findViewById(R.id.layoutLeft);
        layoutRight = (RelativeLayout) findViewById(R.id.layoutRight);
    }

    private void chooseUnit(String unit) {
        JSONArray tempArr = getWords(unit);
        if((tempArr != null ? tempArr.length() : 0) !=0) {
            arr = tempArr;
            index = 0;
            //Log.e("WordList",arr.toString());
            tvTopChoose.setText(unit);
            setWord(index);
            layoutLeft.setVisibility(View.INVISIBLE);
            if(arr.length()==1)
                layoutRight.setVisibility(View.INVISIBLE);
            else layoutRight.setVisibility(View.VISIBLE);
            Log.e("HasValue",String.valueOf(arr.length()));
        } else {
            String msg = "暂未录入...";
            Log.e("Don'tHasValue",String.valueOf(arr.length()));
            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    private JSONArray getWords(String unit) {
        String url = baseUrl + "getWords.php?unit=" + unit;
        HttpUtil httpUtil = new HttpUtil(url);
        httpUtil.start();
        try {
            httpUtil.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            return new JSONArray(httpUtil.getResponse());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setWord(int index) {
        String word;
        tvExplains.setText("");
        setTop();
        hideWord();
        try {
            word = arr.getString(index);
            //Log.e("NowWord",word);
            JSONObject basicJson = getBasic(word);
            //Log.e("NowWordBasic",basicJson.toString());
            tvWord.setText(word);
            tvPhonetic.setText("[" + (basicJson != null ? basicJson.getString("phonetic") : null) + "]");
            JSONArray explains = basicJson != null ? basicJson.getJSONArray("explains") : null;
            for(int i = 0; i< (explains != null ? explains.length() : 0); i++){
                tvExplains.append(explains.getString(i) + "\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getBasic(String word) {
        String url = "http://fanyi.youdao.com/openapi.do?keyfrom=" + keyfrom +
                "&key=" + key +
                "&type=" + type +
                "&doctype=" + doctype +
                "&version=" + version +
                "&q=" + word;
        HttpUtil httpUtil = new HttpUtil(url);
        httpUtil.start();
        try {
            httpUtil.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            return new JSONObject(httpUtil.getResponse()).getJSONObject("basic");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void audioService(String word) {
        Intent intent = new Intent(MainActivity.this, AudioService.class);
        intent.putExtra("query", word);
        startService(intent);
    }

    private void setTop() {
        tvTopBefore.setText(String.valueOf(index + 1));
        tvTopAfter.setText(String.valueOf(arr.length()));
    }

    private void showWord() {
        tvPhonetic.setVisibility(View.VISIBLE);
        tvExplains.setVisibility(View.VISIBLE);
        isShow = true;
    }

    private void hideWord() {
        tvPhonetic.setVisibility(View.INVISIBLE);
        tvExplains.setVisibility(View.INVISIBLE);
        isShow = false;
    }

    @Override
    protected void onResume() {
        //设置为横屏
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }
}
