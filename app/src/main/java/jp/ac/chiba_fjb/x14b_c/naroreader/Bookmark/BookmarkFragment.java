package jp.ac.chiba_fjb.x14b_c.naroreader.Bookmark;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.ac.chiba_fjb.x14b_c.naroreader.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment {


    public BookmarkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //テストでデータ受信要求
        getContext().sendBroadcast(new Intent(getContext(),NaroReceiver.class));
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

}
