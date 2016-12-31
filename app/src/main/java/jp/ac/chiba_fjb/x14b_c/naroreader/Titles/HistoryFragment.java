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

import java.util.Set;

import jp.ac.chiba_fjb.x14b_c.naroreader.AddBookmarkFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.BottomDialog;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.SubTitle.SubtitleFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements TitleAdapter.OnItemClickListener {

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


	private TitleAdapter mAdapter;
	private String mNCode;

	public HistoryFragment() {
		// Required empty public constructor
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getActivity().setTitle("閲覧履歴");

		//ブックマーク表示用アダプターの作成
		mAdapter = new TitleAdapter();
		mAdapter.setOnItemClickListener(this);

		//データ表示用のビューを作成
		RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
		rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
		rv.setAdapter(mAdapter);                                       //アダプターを設定

		//イベント通知受け取りの宣言
		getContext().registerReceiver(mReceiver,new IntentFilter(NaroReceiver.NOTIFI_NOVELINFO));

		//ボタンが押され場合の処理
		((SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh)).setProgressViewOffset(false,0,getResources().getDimensionPixelSize(R.dimen.bar_margin));
		((SwipeRefreshLayout)getView().findViewById(R.id.swipe_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				Snackbar.make(getView(), "ノベル情報の要求", Snackbar.LENGTH_SHORT).show();
				reload();
			}

		});

		update();
		//reload();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_history, container, false);
	}

	@Override
	public void onDestroyView() {
		getContext().unregisterReceiver(mReceiver);
		super.onDestroyView();
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
				bottomDialog.setMenu(R.menu.panel_history,this);
				bottomDialog.show(getFragmentManager(), null);
				break;
			case R.id.menu_history_del:
				delHistory();
				break;
			default:
				((MainActivity)getActivity()).enterMenu(item.getItemId(),mNCode);
				break;
		}

		return false;
	}
	void delHistory(){
		NovelDB db = new NovelDB(getContext());
		Set<String> checks = mAdapter.getChecks();
		for (String ncode : checks) {
			db.delNovelHistory(ncode);
		}
		db.close();
		update();
		output("履歴削除完了");
	}
	void addBookmark(){
		Snackbar.make(getView(),"ブックマーク追加中", Snackbar.LENGTH_SHORT).show();
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
						if(!TbnReader.setBookmark(hash,ncode)){
							output(String.format("%s:ブックマーク失敗",ncode));
						}
					}
				}else{
					output("認証エラー");
				}
				output("ブックマーク完了");
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
	void reload(){
		NaroReceiver.updateNovelInfoHistory(getContext());

	}
	void update(){
		//アダプターにデータを設定
		NovelDB db = new NovelDB(getContext());
		mAdapter.setValues(db.getHistorys());
		db.close();
		mAdapter.notifyDataSetChanged();   //データ再表示要求

		((SwipeRefreshLayout)getView().findViewById(R.id.swipe_refresh)).setRefreshing(false);
	}
	@Override
	public void onItemClick(NovelInfo value) {
		Bundle bundle = new Bundle();
		bundle.putString("ncode",value.ncode);
		((MainActivity)getActivity()).changeFragment(SubtitleFragment.class,bundle);
	}

	@Override
	public void onItemLongClick(final NovelInfo info) {
		mNCode = info.ncode;
		BottomDialog bottomDialog = new BottomDialog();
		bottomDialog.setMenu(R.menu.panel_novel,this);
		bottomDialog.show(getFragmentManager(), null);
	}



}

