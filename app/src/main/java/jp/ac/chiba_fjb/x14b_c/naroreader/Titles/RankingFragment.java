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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

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
public class RankingFragment extends Fragment implements AdapterView.OnItemSelectedListener, TitleAdapter.OnItemClickListener {

    final String[] RANKING_FILTER1_NAME=
    {
        "総合",
        "ジャンル別",
        "異世界転生/転移"
    };
    final String[][] RANKING_FILTER2_NAME=
        {
            {
                "日間",
                "週間",
                "月間",
                "四半期",
                "年間",
                "累計"
            },
            {
                "日間",
                "週間",
                "月間",
                "四半期",
                "年間"
            },
            {
                "日間",
                "週間",
                "月間",
                "四半期",
                "年間"
            }
        };
    final String[][] RANKING_FILTER3_NAME =
        {
            {
                "すべて",
                "短編",
                "連載中",
                "完結済"

            },
            {
                "恋愛(異世界)",
                "恋愛(現実世界)",
                "ハイファンタジー",
                "ローファンタジー",
                "純文学",
                "ヒューマンドラマ",
                "歴史",
                "推理",
                "ホラー",
                "アクション",
                "コメディー",
                "VRゲーム",
                "宇宙",
                "空想科学",
                "パニック",
                "その他",
                "童話",
                "詩",
                "エッセイ",
                "その他"

            },
            {
                "恋愛",
                "ファンタジー",
                "文芸・SF・その他"

            }

        };


    private Spinner mSpiner1;
    private Spinner mSpiner2;
    private Spinner mSpiner3;
    private TitleAdapter mAdapter;

    //通知処理
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case NaroReceiver.NOTIFI_RANKING:
                    if(getView() != null) {
                        ((SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);
                        if (!intent.getBooleanExtra("result", false))
                            Snackbar.make(getView(), "ランキングの受信失敗", Snackbar.LENGTH_SHORT).show();
                        else
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
    private String mNCode;
    private View mView;

    public RankingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("ランキング");

        if(mView != null)
            return mView;

        ArrayAdapter<String> arrayAdapter
            = new ArrayAdapter<String>(getContext(), R.layout.item_adapter, RANKING_FILTER1_NAME);

        FrameLayout toolFrame = (FrameLayout) getActivity().findViewById(R.id.toolFrame);
        View toolView = inflater.inflate(R.layout.tool_ranking,toolFrame,false);
        toolFrame.addView(toolView);

        View view =  inflater.inflate(R.layout.fragment_ranking, container, false);

        mSpiner1 = (Spinner) toolView.findViewById(R.id.spinnerRFilter1);
        mSpiner2 = (Spinner) toolView.findViewById(R.id.spinnerRFilter2);
        mSpiner3 = (Spinner) toolView.findViewById(R.id.spinnerRFilter3);


        //スピナー初期設定
        NovelDB db = new NovelDB(getContext());
        mSpiner1.setAdapter(arrayAdapter);
        mSpiner1.setSelection(db.getSetting("rankingFilter1",0),false);
        updateSpiner();
        mSpiner2.setSelection(db.getSetting("rankingFilter2",0),false);
        mSpiner3.setSelection(db.getSetting("rankingFilter3",0),false);
        db.close();


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
                Snackbar.make(getView(), "ランキングデータの要求", Snackbar.LENGTH_SHORT).show();
                load();
            }


        });

        //スピナー変更を受け取る
        mSpiner1.setOnItemSelectedListener(this);
        mSpiner2.setOnItemSelectedListener(this);
        mSpiner3.setOnItemSelectedListener(this);

        //イベント通知受け取りの宣言
        getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_RANKING));


        return view;
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(mView == null) {
            update();
            load(false);
            mView = view;
        }
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView ==  mSpiner1)
            updateSpiner();
        else
            load(false);

        int f1 = mSpiner1.getSelectedItemPosition();
        int f2 = mSpiner2.getSelectedItemPosition();
        int f3 = mSpiner3.getSelectedItemPosition();

        //変更の保存
        NovelDB db = new NovelDB(getContext());
        db.setSetting("rankingFilter1",f1);
        db.setSetting("rankingFilter2",f2);
        db.setSetting("rankingFilter3",f3);
        db.close();

    }

    //スピナーの内容切り替え
    void updateSpiner(){
        int f1 = mSpiner1.getSelectedItemPosition();

        ArrayAdapter<String> arrayAdapter
            = new ArrayAdapter<String>(getContext(), R.layout.item_adapter, RANKING_FILTER2_NAME[f1]);
        mSpiner2.setAdapter(arrayAdapter);
        mSpiner2.setSelection(0,false);

        ArrayAdapter<String> arrayAdapter2
            = new ArrayAdapter<String>(getContext(), R.layout.item_adapter, RANKING_FILTER3_NAME[f1]);
        mSpiner3.setAdapter(arrayAdapter2);
        mSpiner3.setSelection(0,false);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    void load(boolean flag){
        if(flag)
            load();
        else{
            //一時間経過していたら更新
            NovelDB db = new NovelDB(getContext());
            boolean re = db.isRankingReload(
                    mSpiner1.getSelectedItemPosition(),
                    mSpiner2.getSelectedItemPosition(),
                    mSpiner3.getSelectedItemPosition(),
                    60*60);
            db.close();
            if(re)
                load();
            else
                update();
        }
    }
    void load(){
        ((SwipeRefreshLayout)getView().findViewById(R.id.swipe_refresh)).setRefreshing(true);

        Intent intent = new Intent(getContext(),NaroReceiver.class).setAction(NaroReceiver.ACTION_RANKING);
        intent.putExtra("kind1",mSpiner1.getSelectedItemPosition());
        intent.putExtra("kind2",mSpiner2.getSelectedItemPosition());
        intent.putExtra("kind3",mSpiner3.getSelectedItemPosition());
        getContext().sendBroadcast(intent);

    }
    void update() {
        final int sp1 = mSpiner1.getSelectedItemPosition();
        final int sp2 = mSpiner2.getSelectedItemPosition();
        final int sp3 = mSpiner3.getSelectedItemPosition();

        ((SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh)).setRefreshing(true);
        new Thread(){
            @Override
            public void run() {
                NovelDB db = new NovelDB(getContext());
                final List<NovelDB.NovelInfoRanking> novelRanking = db.getNovelInfoFromRanking(
                        sp1,sp2,sp3);
                db.close();

                if(getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setValues(novelRanking);
                            mAdapter.notifyDataSetChanged();
                            if(getView()!=null)
                                ((SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);

                        }
                    });
                }

            }
        }.start();
    }


    @Override
    public void onItemClick(NovelInfo bookmark) {
        Bundle bundle = new Bundle();
        bundle.putString("ncode",bookmark.ncode);
        bundle.putString("title",bookmark.title);
        ((MainActivity)getActivity()).changeFragment(SubtitleFragment.class,bundle);
    }

    @Override
    public void onItemLongClick(NovelInfo info) {
        mNCode = info.ncode;
        BottomDialog bottomDialog = new BottomDialog();
        bottomDialog.setMenu(R.menu.panel_novel,this);
        bottomDialog.show(getFragmentManager(), null);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            default:
                ((MainActivity)getActivity()).enterMenu(item.getItemId(),mNCode);
                break;
        }

        return false;
    }

}
