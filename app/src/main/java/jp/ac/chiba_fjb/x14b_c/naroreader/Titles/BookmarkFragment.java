package jp.ac.chiba_fjb.x14b_c.naroreader.Titles;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.chiba_fjb.x14b_c.naroreader.AddBookmarkFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.BottomDialog;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.SubTitle.SubtitleFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSeries;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment implements TitleAdapter.OnItemClickListener {

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
    private TitleAdapter mAdapter;
    private Map<String, NovelInfo> mNovelMap;
    private Handler mHandler = new Handler();
    private Map<String, NovelSeries> mSeriesMap;

    public BookmarkFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // タイトルを設定
        getActivity().setTitle("ブックマーク");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        //ブックマーク表示用アダプターの作成
        mAdapter = new TitleAdapter();
        mAdapter.setOnItemClickListener(this);

        //データ表示用のビューを作成
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
        rv.setAdapter(mAdapter);                              //アダプターを設定

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
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_more:
                BottomDialog bottomDialog = new BottomDialog();
                bottomDialog.setMenu(R.menu.panel_bookmark,this);
                bottomDialog.show(getFragmentManager(), null);
                break;
            case R.id.menu_bookmark_del:
                delBookmark();
                break;
            case R.id.menu_download:
                download();
                break;

        }

        return false;
    }
    void update(){
        //アダプターにデータを設定
        NovelDB db = new NovelDB(getContext());
        List<NovelInfo> list = db.getNovelInfoFromBookmark();
        mAdapter.setValues(list);


        List<String > listNcode = new ArrayList<String>();
        for(NovelInfo n : list){
            listNcode.add(n.ncode);
        }

        mSeriesMap = db.getNovelSeriesMap(listNcode);
        mAdapter.setNovelSeries(mSeriesMap);
        db.close();


        db.close();

        mAdapter.notifyDataSetChanged();   //データ再表示要求
    }

    @Override
    public void onItemClick(NovelInfo info) {
        Bundle bundle = new Bundle();
        bundle.putString("ncode",info.ncode);
        ((MainActivity)getActivity()).changeFragment(SubtitleFragment.class,bundle);
    }

    @Override
    public void onItemLongClick(final NovelInfo info) {
        NovelInfo novelInfo = mNovelMap.get(info.ncode.toUpperCase());
        String title = "";
        if(novelInfo != null)
           title = novelInfo.title;
        AddBookmarkFragment.show(this,info.ncode,title,false);
    }

    @Override
    public void onItemCheck() {
     //   setDelayMenu();
    }
    void setDelayMenu(){
//        mHandler.removeCallbacks(null);
//        if(mAdapter.getChecks().size() == 0)
//            return;
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ((MainActivity)getActivity()).showAppBar();
//            }
//        },1000);
        ((MainActivity)getActivity()).setAppBarScroll(mAdapter.getChecks().size() == 0);
    }

    void download(){
        Set<String> checks = mAdapter.getChecks();
        ArrayList<String> list = new ArrayList<String>(checks);
        NaroReceiver.download(getContext(),list);
    }


    void delBookmark(){
        Snackbar.make(getView(),"ブックマーク削除中", Snackbar.LENGTH_SHORT).show();
        new Thread(){
            @Override
            public void run() {
                NovelDB settingDB = new NovelDB(getContext());
                String id = settingDB.getSetting("loginId","");
                String pass = settingDB.getSetting("loginPass","");
                settingDB.close();

                //ログイン処理
                String hash = TbnReader.getLoginHash(id,pass);
                if(hash != null) {
                    Set<String> checks = mAdapter.getChecks();
                    for (String ncode : checks) {
                        if(!TbnReader.clearBookmark(hash,ncode)){
                            output(String.format("%s:ブックマーク解除失敗",ncode));
                        }
                    }
                }else{
                    output("認証エラー");
                }
                output("ブックマーク解除完了");
                NaroReceiver.updateBookmark(getContext());
            }
        }.start();
    }
    void output(final String msg){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getView(),msg, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}
