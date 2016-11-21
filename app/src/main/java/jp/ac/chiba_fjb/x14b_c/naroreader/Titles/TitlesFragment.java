package jp.ac.chiba_fjb.x14b_c.naroreader.Titles;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TitlesFragment extends Fragment implements TitlesAdapter.OnItemClickListener {


	private TitlesAdapter mAdapter;

	public TitlesFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.fragment_titles, container, false);

		//ブックマーク表示用アダプターの作成
		mAdapter = new TitlesAdapter();
		mAdapter.setOnItemClickListener(this);

		//データ表示用のビューを作成
		RecyclerView rv = (RecyclerView) view.findViewById(R.id.RecyclerView);
		rv.setLayoutManager(new LinearLayoutManager(getContext()));     //アイテムを縦に並べる
		rv.setAdapter(mAdapter);                                       //アダプターを設定

		//update();
		return view;
	}


	void update(){
		//アダプターにデータを設定
		NovelDB db = new NovelDB(getContext());
		mAdapter.setBookmarks(db.getTitles());
		db.close();
		mAdapter.notifyDataSetChanged();   //データ再表示要求
	}
	@Override
	public void onItemClick(Map<String, String> value) {

	}
}
