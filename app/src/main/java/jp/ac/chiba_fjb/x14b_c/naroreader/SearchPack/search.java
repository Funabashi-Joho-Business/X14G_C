package jp.ac.chiba_fjb.x14b_c.naroreader.SearchPack;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;


import jp.ac.chiba_fjb.x14b_c.naroreader.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class search extends Fragment implements View.OnClickListener{

    public search() {
        // Required empty public constructor
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        String b = "";
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case NaroReceiver.NOTIFI_SEARCH:
                  if(intent.getBooleanExtra("result",false))
                        Snackbar.make(getView(), "検索情報の受信完了", Snackbar.LENGTH_SHORT).show();
                    else
                        Snackbar.make(getView(), "検索情報の受信失敗", Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private SearchAdapter mSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
        １.検索ワードを受け取り　APIに送る
        ２.APIから貰ったデータを当てはめて出力
         */

        //Inflate the layout for this fragment;
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        view.findViewById(R.id.wordsearch);
        view.findViewById(R.id.searchbutton).setOnClickListener(this);

        //ブックマーク表示用アダプターの作成
        mSearch = new SearchAdapter();


        //データ表示用のビューを作成
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
        //rv.setAdapter(mSearch);                              //アダプターを設定
        return view;

    }

    @Override
    public void onClick(View view) {
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.answer);    //検索結果を表示するところ
        //受信要求
        getContext().sendBroadcast(new Intent(getContext(),NaroReceiver.class).setAction(NaroReceiver.ACTION_SEARCH).putExtra("data",R.id.wordsearch));

        //イベント通知受け取りの宣言
        getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_SEARCH));
    }


    public static class SearchAdapter {
    }
}

