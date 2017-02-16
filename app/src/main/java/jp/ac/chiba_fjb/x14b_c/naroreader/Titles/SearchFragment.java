package jp.ac.chiba_fjb.x14b_c.naroreader.Titles;



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
import android.widget.EditText;

import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.BottomDialog;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.Subtitle.SubtitleFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;



/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements TitleAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private View mView;
    private String mNCode;

    @Override
    public void onRefresh() {
        update();
    }


    //通知処理

    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case NaroReceiver.NOTIFI_SEARCH:
                    if(getView() != null) {
                        ((SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);
                        if (!intent.getBooleanExtra("result", false))
                            Snackbar.make(getView(), "検索の受信失敗", Snackbar.LENGTH_SHORT).show();
                        else
                            update();
                    }
                    break;
            }
        }
    };

    private BroadcastReceiver mReceiver;
    private TitleAdapter mSearch;
    private EditText mwordsearch;
    public List<NovelInfo> mNovelSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mView != null)
            return mView;

        //ブックマーク表示用アダプターの作成
        mSearch = new TitleAdapter();
        mSearch.setOnItemClickListener(this);
        mSearch.showInfo(true);

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //データ表示用のビューを作成
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
        rv.setAdapter(mSearch);                                        //アダプターを設定


        ((SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh)).setProgressViewOffset(false,0,getResources().getDimensionPixelSize(R.dimen.bar_margin));
        ((SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh)).setOnRefreshListener(this);
        //Inflate the layout for this fragment;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("検索結果");
        String params = getArguments().getString("params");
        int writer = getArguments().getInt("writer",0);
        if(params != null){
            Snackbar.make(getView(), "検索開始", Snackbar.LENGTH_SHORT).show();
            getArguments().putString("params",null);
            ((SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh)).setRefreshing(true);
            NaroReceiver.search(getContext(),params,writer);
        }else{
            update();
        }

        mView = getView();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //イベント通知受け取りの宣言
        mReceiver = new Receiver();
        getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_SEARCH));
    }

    @Override
    public void onDetach() {
        getContext().unregisterReceiver(mReceiver);
        super.onDetach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_search:
                ((MainActivity)getActivity()).changeFragment(SearchPanelFragment.class);
                break;
            default:
                return ((MainActivity)getActivity()).enterMenu(item.getItemId(),mNCode);
        }

        return true;
    }
    public void update(){
        ((SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh)).setRefreshing(true);
        new Thread(){
            @Override
            public void run() {
                NovelDB db = new NovelDB(getContext());
                mNovelSearch = db.getNovelSearch();
                db.close();
                if(getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().setTitle("検索結果 " + mNovelSearch.size()+"件");
                            mSearch.setValues(mNovelSearch);
                            mSearch.notifyDataSetChanged();   //データ再表示要求
                            if(getView()!=null)
                                ((SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);

                        }
                    });
                }

            }
        }.start();





    }

    @Override
    public void onItemClick(NovelInfo value) { //ncodeがnull
        Bundle bundle = new Bundle();
        bundle.putString("ncode",value.ncode);
        ((MainActivity)getActivity()).changeFragment(SubtitleFragment.class,bundle);
    }

    @Override
    public void onItemLongClick(NovelInfo info) {
        mNCode = info.ncode;
        BottomDialog bottomDialog = new BottomDialog();
        bottomDialog.setMenu(R.menu.panel_novel,this);
        bottomDialog.show(getFragmentManager(), BottomDialog.class.getName());
    }


}

