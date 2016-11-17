package jp.ac.chiba_fjb.x14b_c.naroreader.SearchPack;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import jp.ac.chiba_fjb.x14b_c.naroreader.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener{


    public SearchFragment() {
        // Required empty public constructor
    }


    private SearchAdapter mSearch;
    private EditText mwordsearch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the layout for this fragment;
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        mwordsearch = (EditText)view.findViewById(R.id.wordsearch);
        view.findViewById(R.id.searchbutton).setOnClickListener(this);

        //ブックマーク表示用アダプターの作成
        mSearch = new SearchAdapter();

        //データ表示用のビューを作成
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
        rv.setAdapter(mSearch);                              //アダプターを設定

        System.out.println("Section1");
        mSearch.notifyDataSetChanged();   //データ再表示要求
        System.out.println("Section8");
        return view;
    }

    @Override
    public void onClick(View view) {

        System.out.println("Section2");

        new Thread(){
            @Override
            public void run() {


                String word = mwordsearch.getText().toString();
                TbnReader.getKeyword(word); //word "異世界"が入ってる
                System.out.println("Section6");
                mSearch.getItemCount();
                System.out.println("Section7");
                return ;
            }
        }.start();


    }



}

