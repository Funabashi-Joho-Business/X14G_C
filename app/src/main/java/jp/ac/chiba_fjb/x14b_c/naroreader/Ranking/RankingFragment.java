package jp.ac.chiba_fjb.x14b_c.naroreader.Ranking;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.AddBookmarkFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.TitlesFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelRanking;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class RankingFragment extends Fragment implements AdapterView.OnItemSelectedListener, RankingAdapter.OnItemClickListener {

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
    private RankingAdapter mAdapter;

    public RankingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("ランキング");

        ArrayAdapter<String> arrayAdapter
            = new ArrayAdapter<String>(getContext(), R.layout.adapter_item, RANKING_FILTER1_NAME);
        View view =  inflater.inflate(R.layout.fragment_ranking, container, false);
        mSpiner1 = (Spinner) view.findViewById(R.id.spinnerRFilter1);
        mSpiner2 = (Spinner) view.findViewById(R.id.spinnerRFilter2);
        mSpiner3 = (Spinner) view.findViewById(R.id.spinnerRFilter3);


        //スピナー初期設定
        NovelDB db = new NovelDB(getContext());
        mSpiner1.setAdapter(arrayAdapter);
        mSpiner1.setSelection(db.getSetting("rankingFilter1",0),false);
        updateSpiner();
        mSpiner2.setSelection(db.getSetting("rankingFilter2",0),false);
        mSpiner3.setSelection(db.getSetting("rankingFilter3",0),false);
        db.close();


        //ブックマーク表示用アダプターの作成
        mAdapter = new RankingAdapter();
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


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        load();
    }



    @Override
    public void onResume() {
        super.onResume();
        //スピナー変更を受け取る
        mSpiner1.setOnItemSelectedListener(this);
        mSpiner2.setOnItemSelectedListener(this);
        mSpiner3.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView ==  mSpiner1)
            updateSpiner();
        else
            load();

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
            = new ArrayAdapter<String>(getContext(), R.layout.adapter_item, RANKING_FILTER2_NAME[f1]);
        mSpiner2.setAdapter(arrayAdapter);
        mSpiner2.setSelection(0,false);

        ArrayAdapter<String> arrayAdapter2
            = new ArrayAdapter<String>(getContext(), R.layout.adapter_item, RANKING_FILTER3_NAME[f1]);
        mSpiner3.setAdapter(arrayAdapter2);
        mSpiner3.setSelection(0,false);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    void load(){
        ((SwipeRefreshLayout)getView().findViewById(R.id.swipe_refresh)).setRefreshing(true);
        new Thread(){
            @Override
            public void run() {
                final List<NovelRanking> rankList = TbnReader.getRanking(mSpiner1.getSelectedItemPosition(), mSpiner2.getSelectedItemPosition(), mSpiner3.getSelectedItemPosition());
                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setRanking(rankList);
                            mAdapter.notifyDataSetChanged();
                            ((SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);
                        }
                    });
                }
            }
        }.start();
    }


    @Override
    public void onItemClick(NovelRanking item) {
        NovelDB db = new NovelDB(getContext());
        db.addNovel(item.ncode);
        db.close();
        getContext().sendBroadcast(new Intent(getContext(),NaroReceiver.class).setAction(NaroReceiver.ACTION_NOVELINFO));
        ((MainActivity)getActivity()).changeFragment(TitlesFragment.class);
    }

    @Override
    public void onItemLongClick(final NovelRanking item) {
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

    void snack (final String data){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(getView(), data, Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
