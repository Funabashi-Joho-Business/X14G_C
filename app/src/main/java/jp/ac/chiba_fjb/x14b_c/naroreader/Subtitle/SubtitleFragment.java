package jp.ac.chiba_fjb.x14b_c.naroreader.Subtitle;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.Contents.ContentsPagerFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.BottomDialog;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSubTitle;


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
                case NaroReceiver.NOTIFI_NOVELSUB:
                    if(getView() != null){
                        ((SwipeRefreshLayout)getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);
                        if(intent.getBooleanExtra("result",false))
                            Snackbar.make(getView(), "サブタイトルの受信完了", Snackbar.LENGTH_SHORT).show();
                        else
                            Snackbar.make(getView(), "サブタイトルの受信失敗", Snackbar.LENGTH_SHORT).show();
                        update();
                    }
                    break;
                case NaroReceiver.NOTIFI_NOVELINFO:
                    if(intent.getBooleanExtra("result",false)){
                        NovelDB db = new NovelDB(getContext());
                        NovelInfo ni = db.getNovelInfo(mNCode);
                        db.addNovelHistory(mNCode);
                        List<NovelSubTitle> subTitles = db.getSubTitles(mNCode);
                        db.close();
                        if(ni == null || subTitles.size() < ni.general_all_no)
                            load();
                    }

                    break;
            }
        }
    };
    private SubtitleAdapter mSubtitleAdapter;
    private String mNCode;
    private int mSort;
    private RecyclerView mRecycleView;
    private String mTitle;


    public SubtitleFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subtitle, container, false);

        //ブックマーク表示用アダプターの作成
        mSubtitleAdapter = new SubtitleAdapter();
        mSubtitleAdapter.setOnItemClickListener(this);

        //データ表示用のビューを作成
        mRecycleView = (RecyclerView) view.findViewById(R.id.RecyclerView);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
        mRecycleView.setAdapter(mSubtitleAdapter);                              //アダプターを設定


        //ボタンが押され場合の処理
        ((SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            load();
            }


        });
        mTitle = "";
        if(getArguments() != null) {
            mNCode = getArguments().getString("ncode");
            mTitle = getArguments().getString("title","");
        }


        NovelDB db = new NovelDB(getContext());
        NovelInfo ni = db.getNovelInfo(mNCode);
        db.addNovelHistory(mNCode);
        mSort = db.getSetting("SUB_SORT",0);
        db.close();

        if(ni != null)
            mTitle = ni.title;
        getActivity().setTitle(mTitle);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //イベント通知受け取りの宣言
        getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_NOVELSUB));
        getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_NOVELINFO));
        //ネットワークから情報の取得
        NaroReceiver.updateNovelInfo(getContext(),mNCode);
        //初回更新
        update();

    }

    @Override
    public void onDestroy() {
        //イベント通知受け取りを解除
        getContext().unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    @Override
    public void onItemClick(int value) {
        Bundle bundle = new Bundle();
        bundle.putString("ncode",mNCode);
        bundle.putInt("index",value);
        bundle.putInt("count",mSubtitleAdapter.getItemCount()-1);
        ((MainActivity)getActivity()).changeFragment(ContentsPagerFragment.class,bundle);
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
                bottomDialog.setMenu(R.menu.panel_subtitle,this);
                bottomDialog.show(getFragmentManager(), BottomDialog.class.getName());
                break;
            case R.id.action_sort:
                mSort = (mSort+1)%2;
                NovelDB db = new NovelDB(getContext());
                db.setSetting("SUB_SORT",mSort);
                db.close();
                update();
                break;
            default:
                return ((MainActivity)getActivity()).enterMenu(item.getItemId(),mNCode);
        }

        return false;
    }



    void load(){
        Snackbar.make(getView(), "サブタイトルデータの要求", Snackbar.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(),NaroReceiver.class);
        intent.putExtra("ncode",mNCode);
        //受信要求
        getContext().sendBroadcast(intent.setAction(NaroReceiver.ACTION_NOVELSUB));
    }
    void update(){
        mSubtitleAdapter.setSort(mSort);
        //アダプターにデータを設定
        NovelDB db = new NovelDB(getContext());
        NovelDB.NovelInfoBookmark novelInfo = db.getNovelInfo(mNCode);
        if(novelInfo != null) {
            mSubtitleAdapter.setValues(mNCode, novelInfo, db.getSubTitles(mNCode),db.getSeriesInfo(mNCode));
            db.close();
            if(novelInfo.b_mark > 0) {
                if(mSort == 0)
                    mRecycleView.scrollToPosition(novelInfo.b_mark);
                else
                    mRecycleView.scrollToPosition(novelInfo.general_all_no - novelInfo.b_mark+1);
            }
            mSubtitleAdapter.notifyDataSetChanged();   //データ再表示要求
        }else{
            db.close();
            Snackbar.make(getView(), "ノベル情報の取得中", Snackbar.LENGTH_SHORT).show();
        }

    }
}
