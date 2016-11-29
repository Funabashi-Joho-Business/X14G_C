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
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.Subtitle.SubtitleFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class TitlesFragment extends Fragment implements TitlesAdapter.OnItemClickListener {

	//通知処理
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		String a = "";
		@Override
		public void onReceive(Context context, Intent intent) {
			switch(intent.getAction()){
				case NaroReceiver.NOTIFI_NOVELINFO:
					if(intent.getBooleanExtra("result",false))
						Snackbar.make(getView(), "ノベルデータの受信完了", Snackbar.LENGTH_SHORT).show();
					else
						Snackbar.make(getView(), "ノベルデータの受信失敗", Snackbar.LENGTH_SHORT).show();
					update();
					break;
			}
		}
	};


	private TitlesAdapter mAdapter;

	public TitlesFragment() {
		// Required empty public constructor
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getActivity().setTitle("登録タイトル一覧");

		//ブックマーク表示用アダプターの作成
		mAdapter = new TitlesAdapter();
		mAdapter.setOnItemClickListener(this);

		//データ表示用のビューを作成
		RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
		rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
		rv.setAdapter(mAdapter);                                       //アダプターを設定

		//ボタンが押され場合の処理
		((SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				Snackbar.make(getView(), "ノベル情報の要求", Snackbar.LENGTH_SHORT).show();
				//受信要求
				getContext().sendBroadcast(new Intent(getContext(),NaroReceiver.class).setAction(NaroReceiver.ACTION_NOVELINFO));
			}

		});
		update();
		//イベント通知受け取りの宣言
		getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_NOVELINFO));


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_titles, container, false);
	}

	@Override
	public void onDestroyView() {
		getContext().unregisterReceiver(mReceiver);
		super.onDestroyView();
	}

	void update(){
		//アダプターにデータを設定
		NovelDB db = new NovelDB(getContext());
		mAdapter.setValues(db.getTitles());
		db.close();
		mAdapter.notifyDataSetChanged();   //データ再表示要求

		((SwipeRefreshLayout)getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);
	}
	@Override
	public void onItemClick(Map<String, String> value) {
		Bundle bundle = new Bundle();
		bundle.putString("ncode",value.get("ncode"));
		((MainActivity)getActivity()).changeFragment(SubtitleFragment.class,bundle);
	}
}
