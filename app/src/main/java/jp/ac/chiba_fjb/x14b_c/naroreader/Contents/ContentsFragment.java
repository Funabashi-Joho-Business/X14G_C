package jp.ac.chiba_fjb.x14b_c.naroreader.Contents;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelContent;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;


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
                    ((SwipeRefreshLayout)getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);
                    if(intent.getBooleanExtra("result",false))
                        Snackbar.make(getView(), "本文の受信完了", Snackbar.LENGTH_SHORT).show();
                    else
                        Snackbar.make(getView(), "本文の受信失敗", Snackbar.LENGTH_SHORT).show();
                    update(false);
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
    private WebViewClient mWebClient = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            update(true);
        }


    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contents, container, false);

        /*
        値をサブタイトルから受け取ってURLに叩き込む
        nコードとページ数だけ貰えればOK
         */
        Bundle bundle = getArguments();
        mNCode = bundle.getString("ncode");
        mIndex = bundle.getInt("index");

        //スワイプされた場合の処理
        final SwipeRefreshLayout swipe = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }


        });


        //mWebView.setWebViewClient(new WebViewClient());
        mWebView = (WebView)view.findViewById(R.id.mWebView);
        mWebView.setWebViewClient(mWebClient);
        mWebView.addJavascriptInterface(this,"Java");
        mWebView.getSettings().setJavaScriptEnabled(true);  //JavaScript許可
        mWebView.loadUrl("file:///android_asset/Template.html");


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

    private String mContent;
    public void update(boolean flag){
        NovelDB db = new NovelDB(getContext());
        NovelContent nc = db.getNovelContent(mNCode,mIndex);
        db.close();
        if(nc == null){
            if(flag) {
                mContent = "読み込み中";
                load();
            }
            else
                mContent = "データ無し";
        }
        else {
            mContent = nc.body;
        }
        mWebView.loadUrl("javascript:update();");
    }
    @JavascriptInterface
    public String getContent(){
        return mContent;
    }

    void load(){
        NovelDB db = new NovelDB(getContext());
        NovelInfo novelInfo = db.getNovelInfo(mNCode);
        db.close();

        //短編確認
        int index = 0;
        if(novelInfo == null || novelInfo.novel_type != 2)
            index = mIndex;

        Snackbar.make(getView(), "本文の要求", Snackbar.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(),NaroReceiver.class);
        intent.putExtra("ncode",mNCode);
        intent.putExtra("index",index);
        //受信要求
        getContext().sendBroadcast(intent.setAction(NaroReceiver.ACTION_NOVELCONTENT));
    }
}
