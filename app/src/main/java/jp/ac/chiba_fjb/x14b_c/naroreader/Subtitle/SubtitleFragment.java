package jp.ac.chiba_fjb.x14b_c.naroreader.Subtitle;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.TitlesFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubtitleFragment extends Fragment implements SubtitleAdapter.OnItemClickListener {

    //通知処理
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        String a = "";
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case NaroReceiver.NOTIFI_BOOKMARK:
                    ((SwipeRefreshLayout)getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);

                    if(intent.getBooleanExtra("result",false))
                        Snackbar.make(getView(), "ブックマークデータの受信完了", Snackbar.LENGTH_SHORT).show();
                    else
                        Snackbar.make(getView(), "ブックマークデータの受信失敗", Snackbar.LENGTH_SHORT).show();
                    update();
                    break;
            }
        }
    };
    private SubtitleAdapter mSubtitleAdapter;


    public SubtitleFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        // タイトルを設定
        toolbar.setTitle("ブックマーク");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subtitle, container, false);

        //ブックマーク表示用アダプターの作成
        mSubtitleAdapter = new SubtitleAdapter();
        mSubtitleAdapter.setOnItemClickListener(this);

        //データ表示用のビューを作成
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
        rv.setAdapter(mSubtitleAdapter);                              //アダプターを設定


        //ボタンが押され場合の処理
        ((SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Snackbar.make(getView(), "ブックマークデータの要求", Snackbar.LENGTH_SHORT).show();
                //受信要求
                getContext().sendBroadcast(new Intent(getContext(),NaroReceiver.class).setAction(NaroReceiver.ACTION_BOOKMARK));
            }


        });

        //イベント通知受け取りの宣言
        // getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_BOOKMARK));

        //初回更新
        update();
        return view;
    }

    @Override
    public void onDestroy() {
        //イベント通知受け取りを解除
        // getContext().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    void update(){
        //アダプターにデータを設定
        NovelDB db = new NovelDB(getContext());
        mSubtitleAdapter.setBookmarks(db.getBookmark());
        db.close();
        mSubtitleAdapter.notifyDataSetChanged();   //データ再表示要求
    }

    @Override
    public void onItemClick(NovelBookmark bookmark) {
        NovelDB db = new NovelDB(getContext());
        db.addNovel(bookmark.getCode());
        db.close();

        ((MainActivity)getActivity()).changeFragment(TitlesFragment.class);
    }
}
