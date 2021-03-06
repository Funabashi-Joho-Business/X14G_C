package to.pns.naroencounter.Titles;


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

import to.pns.naroencounter.MainActivity;
import to.pns.naroencounter.Other.BottomDialog;
import to.pns.naroencounter.Other.NaroReceiver;
import to.pns.naroencounter.Other.NovelDB;
import to.pns.naroencounter.R;
import to.pns.naroencounter.Subtitle.SubtitleFragment;
import to.pns.naroencounter.data.NovelInfo;
import to.pns.naroencounter.data.NovelSeries;


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
    private View mView;
    private String mNCode;

    public BookmarkFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // タイトルを設定
        getActivity().setTitle("ブックマーク");

        if(mView != null) {
            update();
            return mView;
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        mView = view;
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
        ((SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh)).setProgressViewOffset(false,0,getResources().getDimensionPixelSize(R.dimen.bar_margin));

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
            case R.id.menu_list_bookmark_del:
                delBookmarks();
                break;
            case R.id.menu_list_download:
                download();
                break;
            default:
                return ((MainActivity)getActivity()).enterMenu(item.getItemId(),mNCode);
        }

        return false;
    }
    void update(){
        //アダプターにデータを設定
        NovelDB db = new NovelDB(getContext());
        List<NovelDB.NovelInfoBookmark> list = db.getNovelInfoFromBookmark();
        mAdapter.setValues(list);


        List<String > listNcode = new ArrayList<String>();
        for(NovelInfo n : list){
            listNcode.add(n.ncode);
        }

        mSeriesMap = db.getNovelSeriesMap(listNcode);
        mAdapter.setNovelSeries(mSeriesMap);
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
        mNCode = info.ncode;
        BottomDialog bottomDialog = new BottomDialog();
        bottomDialog.setMenu(R.menu.panel_novel,this);
        bottomDialog.show(getFragmentManager(), BottomDialog.class.getName());
    }



    void download(){
        Set<String> checks = mAdapter.getChecks();
        ArrayList<String> list = new ArrayList<String>(checks);
        NaroReceiver.download(getContext(),list);
    }
    void delBookmarks(){
        Set<String> checks = mAdapter.getChecks();
        ArrayList<String> list = new ArrayList<String>(checks);
        NaroReceiver.delBookmark(getContext(),list);
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
