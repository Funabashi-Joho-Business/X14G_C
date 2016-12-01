package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelContent;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContentsFragment extends Fragment implements View.OnClickListener {
    //通知処理
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case NaroReceiver.NOTIFI_NOVELCONTENT:
                    if(intent.getBooleanExtra("result",false))
                        Snackbar.make(getView(), "本文の受信完了", Snackbar.LENGTH_SHORT).show();
                    else
                        Snackbar.make(getView(), "本文の受信失敗", Snackbar.LENGTH_SHORT).show();
                    update();
                    break;
            }
        }
    };

    private String mNCode;
    private int mIndex;

    public ContentsFragment() {
        // Required empty public constructor
    }

    private WebView mWebView;
    private int Fullpage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_honbun, container, false);

        /*
        値をサブタイトルから受け取ってURLに叩き込む
        nコードとページ数だけ貰えればOK
         */
        Bundle bundle = getArguments();
        mNCode = bundle.getString("ncode");
        mIndex = bundle.getInt("index");

        //mWebView.setWebViewClient(new WebViewClient());
        mWebView = (WebView)view.findViewById(R.id.mWebView);
        mWebView.loadUrl("file:///android_asset/naiyou.html");
        view.findViewById(R.id.Hnext).setOnClickListener(this);
        view.findViewById(R.id.Hback).setOnClickListener(this);
        view.findViewById(R.id.shiori).setOnClickListener(this);
        view.findViewById(R.id.backhome).setOnClickListener(this);
        view.findViewById(R.id.insertBookmark).setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //イベント通知受け取りの宣言
        getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_NOVELCONTENT));
        update();
    }

    @Override
    public void onDestroy() {
        //イベント通知受け取りを解除
        getContext().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Hnext:
                //次ページに飛ぶ
                break;
            case R.id.Hback:
                //1ページ前に飛ぶ
                break;
            case R.id.shiori:
                //しおりをはさむ
                break;
            case R.id.insertBookmark:
                //ブックマークの設定or解除
            case R.id.backhome:
                //帰る
                break;
        }
    }

    public void update(){
        NovelDB db = new NovelDB(getContext());
        NovelContent nc = db.getNovelContent(mNCode,mIndex);
        db.close();

        if(nc == null){
            Snackbar.make(getView(), "本文の要求", Snackbar.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(),NaroReceiver.class);
            intent.putExtra("ncode",mNCode);
            intent.putExtra("index",mIndex);
            //受信要求
            getContext().sendBroadcast(intent.setAction(NaroReceiver.ACTION_NOVELCONTENT));
        }
        else {
            mWebView.loadDataWithBaseURL(null, nc.body, "text/html", "UTF-8", null);
            //System.out.println(nc.body);
        }
        //Fullpage = SubtitleAdapter.pagelangth;
    }
}
