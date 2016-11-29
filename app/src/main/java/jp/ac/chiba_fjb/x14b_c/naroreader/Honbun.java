package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.Subtitle.SubtitleAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class Honbun extends Fragment {


    public Honbun() {
        // Required empty public constructor
    }

    private WebView mWebView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_honbun, container, false);

        /*
        値をサブタイトルから受け取ってURLに叩き込む
        nコードとページ数だけ貰えればOK
         */

        mWebView.setWebViewClient(new WebViewClient());
        mWebView = (WebView)view.findViewById(R.id.mWebView);
        mWebView.loadUrl("file:///android_asset/naiyou.html");



        return view;
    }

}
