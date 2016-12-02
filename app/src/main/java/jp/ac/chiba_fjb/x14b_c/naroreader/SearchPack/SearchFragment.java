package jp.ac.chiba_fjb.x14b_c.naroreader.SearchPack;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
    public NovelInfo[] flashdata;

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

        update();
        return view;
    }

    @Override
    public void onClick(View view) {

        new Thread() {
            @Override
            public void run() {


                String word = mwordsearch.getText().toString();
                flashdata = TbnReader.getKeyword(word);
                mSearch.getItemCount();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                });
                return;
            }
        }.start();


    }
    public void update(){
        mSearch.setSearch(flashdata);
        mSearch.notifyDataSetChanged();   //データ再表示要求
    }

    

}

