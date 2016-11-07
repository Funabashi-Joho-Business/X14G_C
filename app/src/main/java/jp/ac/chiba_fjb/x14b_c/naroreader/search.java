package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class search extends Fragment implements View.OnClickListener{

    public search() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        １.検索ワードを受け取り　APIに送る
        ２.APIから貰ったデータを当てはめて出力

         */

        //Inflate the layout for this fragment;
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        view.findViewById(R.id.wordsearch);
        view.findViewById(R.id.searchbutton).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.answer);    //検索結果を表示するところ

        //スレッドスタート
        new Thread(){
            @Override
            public void run() {
                EditText Setext = (EditText)getView().findViewById(R.id.wordsearch);    //検索したい文字を格納
                String sort;
                sort = "hyoka";
                String s;
                s = Setext.getText().toString();
                TbnReader.getKeyword(s);
                TbnReader.getOrder(sort);
            }
        }.start();

        TextView sikiri = new TextView(getContext());
        sikiri.setText("------------------------");
        layout.addView(sikiri);
    }
}

