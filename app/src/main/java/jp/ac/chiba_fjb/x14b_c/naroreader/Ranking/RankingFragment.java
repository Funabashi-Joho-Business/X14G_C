package jp.ac.chiba_fjb.x14b_c.naroreader.Ranking;


import android.os.Bundle;
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

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelRanking;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class RankingFragment extends Fragment implements AdapterView.OnItemSelectedListener {

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
    private RankingAdapter mRankingAdapter;

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

        mSpiner1.setAdapter(arrayAdapter);
        mSpiner1.setOnItemSelectedListener(this);


        //ブックマーク表示用アダプターの作成
        mRankingAdapter = new RankingAdapter();


        //データ表示用のビューを作成
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
        rv.setAdapter(mRankingAdapter);                              //アダプターを設定


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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == mSpiner1){
            ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<String>(getContext(), R.layout.adapter_item, RANKING_FILTER2_NAME[i]);
                mSpiner2.setAdapter(arrayAdapter);

            ArrayAdapter<String> arrayAdapter2
                = new ArrayAdapter<String>(getContext(), R.layout.adapter_item, RANKING_FILTER3_NAME[i]);
            mSpiner3.setAdapter(arrayAdapter2);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    void load(){
        new Thread(){
            @Override
            public void run() {
                final List<NovelRanking> rankList = TbnReader.getRanking(mSpiner1.getSelectedItemPosition(), mSpiner2.getSelectedItemPosition(), mSpiner3.getSelectedItemPosition());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRankingAdapter.setRanking(rankList);
                        mRankingAdapter.notifyDataSetChanged();
                        ((SwipeRefreshLayout)getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);
                    }
                });
            }
        }.start();
    }


}
