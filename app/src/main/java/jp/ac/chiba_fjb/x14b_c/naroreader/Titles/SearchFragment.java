package jp.ac.chiba_fjb.x14b_c.naroreader.Titles;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.AddBookmarkFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.SubTitle.SubtitleFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;



/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements TitleAdapter.OnItemClickListener {
    //通知処理
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case NaroReceiver.NOTIFI_SEARCH:
                    if(getView() != null) {
                       // ((SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);
                        if (!intent.getBooleanExtra("result", false))
                            Snackbar.make(getView(), "検索の受信失敗", Snackbar.LENGTH_SHORT).show();
                        else
                            update();
                    }
                    break;
            }
        }
    };

    public SearchFragment() {
        // Required empty public constructor
    }


    private TitleAdapter mSearch;
    private EditText mwordsearch;
    public List<NovelInfo> mNovelSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the layout for this fragment;
        View view =  inflater.inflate(R.layout.fragment_search, container, false);

        //ブックマーク表示用アダプターの作成
        mSearch = new TitleAdapter();
        mSearch.setOnItemClickListener(this);


        //データ表示用のビューを作成
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
        rv.setAdapter(mSearch);                                        //アダプターを設定

        update();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //イベント通知受け取りの宣言
        getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_SEARCH));
        update();
    }

    @Override
    public void onStop() {
        getContext().unregisterReceiver(mReceiver);
        super.onStop();
    }



    public void update(){
        NovelDB db = new NovelDB(getContext());
        mNovelSearch = db.getNovelSearch();
        db.close();


        mSearch.setValues(mNovelSearch);
        mSearch.notifyDataSetChanged();   //データ再表示要求
    }

    @Override
    public void onItemClick(NovelInfo value) { //ncodeがnull
        Bundle bundle = new Bundle();
        bundle.putString("ncode",value.ncode);
        ((MainActivity)getActivity()).changeFragment(SubtitleFragment.class,bundle);
    }

    @Override
    public void onItemLongClick(final NovelInfo item) {
        AddBookmarkFragment.show(this,item.ncode,item.title,true);
    }

    @Override
    public void onItemCheck() {

    }

}

