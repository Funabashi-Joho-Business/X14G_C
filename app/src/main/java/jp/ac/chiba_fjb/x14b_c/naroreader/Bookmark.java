package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Bookmark extends Fragment implements View.OnClickListener {


    public Bookmark() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_bookmark, container, false);

        Button Bookmarkbutton = (Button)view.findViewById(R.id.BookmarkButton1);
        //インスタンスの取得
        Bookmarkbutton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        ////////////////////////////////////////////////////////////////////////////////////////////
        //////////////ブックマーク件数を抽出・件数分TextViewを生成し関連付けしたい//////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////

        //インスタンスの取得
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.BookmarkLayout);
        //動的なコントロールの生成
        //TextViewを100個
        for (int i = 0; i < 100; i++) {
            TextView textView = new TextView(getContext());             //インスタンスの生成(引数はActivityのインスタンス)
            textView.setId(i);                     //テキストの設定
            layout.addView(textView);
        }
    }
}
