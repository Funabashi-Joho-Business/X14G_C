package jp.ac.chiba_fjb.x14b_c.naroreader.Contents;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

import jp.ac.chiba_fjb.x14b_c.naroreader.ColorPickerFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelContent;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentsFragment extends Fragment {
    //通知処理
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case NaroReceiver.NOTIFI_NOVELCONTENT:
                    ((SwipeRefreshLayout)getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);
                    if(intent.getIntExtra("index",0) == mIndex) {
                        if (!intent.getBooleanExtra("result", false))
                            Snackbar.make(getView(), "本文の受信失敗", Snackbar.LENGTH_SHORT).show();
                        update(false);
                    }
                    break;
            }
        }
    };

    private String mNCode;
    private int mIndex;
    private String mMsg;
    private int mWebMsgIndex = 0;
    private Map<Integer,String> mMapMsg = new HashMap<Integer,String>();
    private int mTextColor;
    private int mBackColor;
    private int mTextColor2;
    private int mBackColor2;

    public ContentsFragment() {
        // Required empty public constructor
    }

    private WebView mWebView;
    private int mFontSize;
    private WebViewClient mWebClient = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            update(true);
            updateStyle();

        }


    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        NovelDB db = new NovelDB(getContext());
        mFontSize = db.getSetting("fontSize",10);
        mTextColor = db.getSetting("fontColor",0x444444);
        mBackColor = db.getSetting("backColor",0xedf7ff);
        db.close();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contents, container, false);

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
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.loadUrl("file:///android_asset/Template.html");


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //イベント通知受け取りの宣言
        getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_NOVELCONTENT));

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Uri uri;
        Intent i;
        switch(item.getItemId()){
            case R.id.menu_zoom_down:
                setFontSize(mFontSize-1);
                return true;
            case R.id.menu_zoom_up:
                setFontSize(mFontSize+1);
                return true;
            case R.id.menu_set_bookmark2:
                NaroReceiver.setBookmark2(getContext(),mNCode,mIndex);
                break;
            case R.id.menu_set_textcolor: {
                ColorPickerFragment colorPickerFragment = new ColorPickerFragment();
                mTextColor2 = mTextColor;
                colorPickerFragment.setColor(mTextColor);
                colorPickerFragment.setOnDialogButtonListener(new ColorPickerFragment.OnDialogButtonListener() {
                    @Override
                    public void onDialogButton() {
                        setTextColor(mTextColor2);
                    }

                    @Override
                    public void onColorChange(int color) {
                        setTextColor(color);
                    }
                });
                colorPickerFragment.show(getFragmentManager(), null);
                break;
            }
            case R.id.menu_set_backcolor: {
                ColorPickerFragment colorPickerFragment = new ColorPickerFragment();
                mBackColor2 = mBackColor;
                colorPickerFragment.setColor(mBackColor);
                colorPickerFragment.setOnDialogButtonListener(new ColorPickerFragment.OnDialogButtonListener() {
                    @Override
                    public void onDialogButton() {
                        setBackColor(mBackColor2);
                    }

                    @Override
                    public void onColorChange(int color) {
                        setBackColor(color);
                    }


                });
                colorPickerFragment.show(getFragmentManager(), null);
                break;
            }
            default:
                ((MainActivity)getActivity()).enterMenu(item.getItemId(),mNCode);
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy() {
        //イベント通知受け取りを解除
        getContext().unregisterReceiver(mReceiver);
        super.onDestroy();
    }



    private String mContent;
    public void update(boolean flag){
        NovelDB db = new NovelDB(getContext());
        NovelContent nc = db.getNovelContent(mNCode,mIndex);
        db.close();
        String msg = "";
        if(nc == null){
            if(flag) {
                msg = "読み込み中";
                load();
            }
            else
                msg = "データ無し";

            setText(".body",msg);
        }
        else {
            setText(".title",nc.title);
            setText(".tag",nc.tag);
            setText(".body",nc.body);
            if(nc.preface != null)
                setText(".preface",nc.preface);
            if(nc.trailer != null)
                setText(".trailer",nc.trailer);
        }

    }
    public void setStyle(String tag,String name,String value){
        String script = String.format("javascript:setStyle('%s','%s','%s');",tag,name,value);
        mWebView.loadUrl(script);
    }
    public void setText(String name,String msg){
        int index = addMsg(msg);
        //データの書き換え
        String script = String.format("javascript:setText('%s',Java.getMsg(%d));",name,index);
        mWebView.loadUrl(script);
    }

    public int addMsg(String msg){
        mMapMsg.put(mWebMsgIndex++,msg);
        return mWebMsgIndex-1;
    }
    @JavascriptInterface
    public String getMsg(int i){
        String msg = mMapMsg.get(i);
        mMapMsg.remove(i);
        return msg;
    }


    void load(){
        NovelDB db = new NovelDB(getContext());
        NovelInfo novelInfo = db.getNovelInfo(mNCode);
        db.close();

        //短編確認
        int index = 0;
        if(novelInfo == null || novelInfo.novel_type != 2)
            index = mIndex;

        Intent intent = new Intent(getContext(),NaroReceiver.class);
        intent.putExtra("ncode",mNCode);
        intent.putExtra("index",index);
        //受信要求
        getContext().sendBroadcast(intent.setAction(NaroReceiver.ACTION_NOVELCONTENT));
    }
    void setBackColor(int color){
        if(mBackColor != color){
            mBackColor = color;
            NovelDB db = new NovelDB(getContext());
            db.setSetting("backColor", color);
            db.close();
            updateStyle();
        }
    }
    void setTextColor(int color){
        if(mTextColor != color){
            mTextColor = color;
            NovelDB db = new NovelDB(getContext());
            db.setSetting("fontColor", color);
            db.close();
            updateStyle();
        }
    }
    void setFontSize(int size){
        if(mFontSize != size){
            mFontSize = size;
            NovelDB db = new NovelDB(getContext());
            db.setSetting("fontSize",size);
            db.close();
            updateStyle();
        }
    }
    void updateStyle(){
        setStyle(".all","font-size",mFontSize+"pt");
        setStyle("body","color",String.format("#%06x",mTextColor&0xffffff));
        setStyle("body","background-color",String.format("#%06x",mBackColor&0xffffff));
    }
}
