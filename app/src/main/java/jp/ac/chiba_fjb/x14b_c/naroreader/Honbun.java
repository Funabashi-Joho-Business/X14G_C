package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class Honbun extends Fragment implements View.OnClickListener {


    public Honbun() {
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

        //mWebView.setWebViewClient(new WebViewClient());
        mWebView = (WebView)view.findViewById(R.id.mWebView);
        mWebView.loadUrl("file:///android_asset/naiyou.html");
        view.findViewById(R.id.Hnext).setOnClickListener(this);
        view.findViewById(R.id.Hback).setOnClickListener(this);
        view.findViewById(R.id.shiori).setOnClickListener(this);
        view.findViewById(R.id.backhome).setOnClickListener(this);
        view.findViewById(R.id.insertBookmark).setOnClickListener(this);



        Hupdate();
        return view;
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

    public void Hupdate(){
        //Fullpage = SubtitleAdapter.pagelangth;
    }
}
