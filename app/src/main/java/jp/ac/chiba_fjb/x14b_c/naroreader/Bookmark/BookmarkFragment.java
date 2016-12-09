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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.AddBookmarkFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.Subtitle.SubtitleFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment implements BookmarkAdapter.OnItemClickListener {

    //通知処理
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        String a = "";
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case NaroReceiver.NOTIFI_BOOKMARK:
                    if(getView()!=null) {
                        ((SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);

                        if (intent.getBooleanExtra("result", false))
                            Snackbar.make(getView(), "ブックマークデータの受信完了", Snackbar.LENGTH_SHORT).show();
                        else
                            Snackbar.make(getView(), "ブックマークデータの受信失敗", Snackbar.LENGTH_SHORT).show();
                        update();
                    }
                    break;
                case NaroReceiver.NOTIFI_NOVELINFO:
                    if(getView()!=null) {
                        update();
                    }
                    break;
            }
        }
    };
    private BookmarkAdapter mBookmarkAdapter;
    private HashMap<String, NovelInfo> mNovelMap;

    public BookmarkFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        // タイトルを設定
        toolbar.setTitle("ブックマーク");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        //ブックマーク表示用アダプターの作成
        mBookmarkAdapter = new BookmarkAdapter();
        mBookmarkAdapter.setOnItemClickListener(this);

        //データ表示用のビューを作成
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
        rv.setAdapter(mBookmarkAdapter);                              //アダプターを設定


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
        getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_BOOKMARK));
        getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_NOVELINFO));
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
        List<NovelBookmark> list = db.getBookmark();
        mBookmarkAdapter.setBookmarks(list);


        List<String > listNcode = new ArrayList<String>();
        for(NovelBookmark n : list){
            listNcode.add(n.getCode());
        }
        List<NovelInfo> novelInfo = db.getNovelInfo(listNcode);
        mNovelMap = new HashMap<String,NovelInfo>();
        for(NovelInfo n : novelInfo){
            mNovelMap.put(n.ncode,n);
        }
        mBookmarkAdapter.setNovelInfos(mNovelMap);
        db.close();




        mBookmarkAdapter.notifyDataSetChanged();   //データ再表示要求
    }

    @Override
    public void onItemClick(NovelBookmark bookmark) {
        Bundle bundle = new Bundle();
        bundle.putString("ncode",bookmark.getCode());
        ((MainActivity)getActivity()).changeFragment(SubtitleFragment.class,bundle);
    }

    @Override
    public void onItemLongClick(final NovelBookmark bookmark) {
        NovelInfo novelInfo = mNovelMap.get(bookmark.getCode().toUpperCase());
        Bundle bn = new Bundle();
        bn.putString("ncode",bookmark.getCode());
        if(novelInfo != null)
            bn.putString("title",novelInfo.title);
        else
            bn.putString("title","");
        bn.putInt("mode",1);
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
                        if(bookmark.getCode() != null){
                            String mNcode = bookmark.getCode();
                            if (TbnReader.clearBookmark(hash, mNcode)){ //ブックマーク処理
                                snack("ブックマーク解除しました");
                                NaroReceiver.updateBookmark(getContext());

                            } else {
                                snack("ブックマーク解除できませんでした");
                            }
                        }
                    }
                }.start();
            }
        });

        //フラグメントをダイアログとして表示
        f.show(getFragmentManager(),"");
    }

    void snack (final String data){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getView(), data, Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
