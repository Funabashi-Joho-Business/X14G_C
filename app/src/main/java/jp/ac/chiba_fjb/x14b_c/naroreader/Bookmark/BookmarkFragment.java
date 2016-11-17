package jp.ac.chiba_fjb.x14b_c.naroreader.Bookmark;


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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.ac.chiba_fjb.x14b_c.naroreader.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment {

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
    private BookmarkAdapter mBookmarkAdapter;


    public BookmarkFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        //ブックマーク表示用アダプターの作成
        mBookmarkAdapter = new BookmarkAdapter();


        //データ表示用のビューを作成
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
        rv.setAdapter(mBookmarkAdapter);                              //アダプターを設定


        //ボタンが押された場合の処理
        ((SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Snackbar.make(getView(), "ブックマークデータの要求", Snackbar.LENGTH_SHORT).show();
                //受信要求
                getContext().sendBroadcast(new Intent(getContext(),NaroReceiver.class).setAction(NaroReceiver.ACTION_BOOKMARK));
            }


        });

        //イベント通知受け取りの宣言
        getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_BOOKMARK));

        //初回更新
        update();
        return view;
    }

    @Override
    public void onDestroy() {
        //イベント通知受け取りを解除
        getContext().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    void update(){
        //アダプターにデータを設定
        NovelDB db = new NovelDB(getContext());
        mBookmarkAdapter.setBookmarks(db.getBookmark());
        db.close();
        mBookmarkAdapter.notifyDataSetChanged();   //データ再表示要求
    }
}
