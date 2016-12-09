package jp.ac.chiba_fjb.x14b_c.naroreader.SearchPack;



import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import jp.ac.chiba_fjb.x14b_c.naroreader.AddBookmarkFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.Subtitle.SubtitleFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.HistoryFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;



/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements SearchAdapter.OnItemClickListener, View.OnClickListener{


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
        mSearch.setOnItemClickListener(this);


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

    @Override
    public void onItemClick(NovelInfo value) {
        NovelDB db = new NovelDB(getContext());
        //db.addNovel(value.ncode);
        db.close();
        getContext().sendBroadcast(new Intent(getContext(),NaroReceiver.class).setAction(NaroReceiver.ACTION_NOVELINFO));
        ((MainActivity)getActivity()).changeFragment(HistoryFragment.class);
    }

    @Override
    public void onItemLongClick(final NovelInfo item) {
        Bundle bn = new Bundle();
        bn.putString("ncode",item.ncode);
        bn.putString("title",item.title);
        bn.putInt("mode",0);
        //フラグメントのインスタンスを作成
        AddBookmarkFragment f = new AddBookmarkFragment();
        f.setArguments(bn);

        //ダイアログボタンの処理
        f.setOnDialogButtonListener(new AddBookmarkFragment.OnDialogButtonListener() {
            @Override
            public void onDialogButton() {
                new Thread(){
                    @Override
                    public void run() {

                        NovelDB settingDB = new NovelDB(getContext());
                        String id = settingDB.getSetting("loginId","");
                        String pass = settingDB.getSetting("loginPass","");
                        settingDB.close();

                        //ログイン処理
                        String hash = TbnReader.getLoginHash(id,pass);
                        if(item.ncode != null){
                            String mNcode = item.ncode;
                            if (TbnReader.setBookmark(hash, mNcode)) //ブックマーク処理
                                snack("ブックマークしました");
                            else
                                snack("ブックマークできませんでした");
                        }
                    }
                }.start();
            }
        });

        //フラグメントをダイアログとして表示
        f.show(getFragmentManager(),"");
    }

    void snack (final String data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getView(), data, Snackbar.LENGTH_SHORT).show();
            }
        });
    }


}

