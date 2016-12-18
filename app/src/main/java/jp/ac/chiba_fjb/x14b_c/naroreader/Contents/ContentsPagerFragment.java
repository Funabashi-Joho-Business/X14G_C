package jp.ac.chiba_fjb.x14b_c.naroreader.Contents;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdView;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.BottomDialog;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentsPagerFragment extends Fragment implements ViewPager.OnPageChangeListener {

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		setReaded(position+1);
	}
	void setReaded(int index){
		Bundle bundle = getArguments();
		NovelDB db = new NovelDB(getContext());
		db.setNovelReaded(bundle.getString("ncode"),index);
		db.close();
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	static class PagerAdapter extends FragmentStatePagerAdapter {

		int mCount;
		String mNcode;
		public PagerAdapter(FragmentManager fm,String ncode,int count) {
			super(fm);
			mCount = count;
			mNcode = ncode;
		}

		@Override
		public Fragment getItem(int position) {
			Bundle bundle = new Bundle();
			bundle.putString("ncode",mNcode);
			bundle.putInt("index",position+1);
			Fragment f = new ContentsFragment();
			f.setArguments(bundle);
			return f;
		}

		@Override
		public int getCount() {
			return mCount;
		}
	}


	public ContentsPagerFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_contents_pager, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Bundle bundle = getArguments();

		ViewPager viewPager = (ViewPager) getView().findViewById(R.id.pager);
		viewPager.addOnPageChangeListener(this);
		viewPager.setAdapter(new PagerAdapter(getFragmentManager(),bundle.getString("ncode"),bundle.getInt("count")));
		viewPager.setCurrentItem(bundle.getInt("index")-1);
		setReaded(bundle.getInt("index"));
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.option, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_more) {
			ViewPager viewPager = (ViewPager) getView().findViewById(R.id.pager);
			int index = viewPager.getCurrentItem();
			Fragment parent = (Fragment)viewPager.getAdapter().instantiateItem(viewPager,index);

			BottomDialog f = new BottomDialog();
			f.setMenu(R.menu.panel_contents,parent);
			f.show(getFragmentManager(), null);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();
		AdView adView = (AdView)getActivity().findViewById(R.id.ad_view);
		adView.setVisibility(View.GONE);
	}

	@Override
	public void onStop() {
		AdView adView = (AdView)getActivity().findViewById(R.id.ad_view);
		adView.setVisibility(View.VISIBLE);
		super.onStop();
	}
}
