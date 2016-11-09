package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;

public class ConfigFragment extends Fragment {


    public ConfigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        // タイトルを設定
        toolbar.setTitle("設定");

        //////////////////抽出条件を書き、ソートさせる文を作る（要SQL）;////////////////////////////

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_config, container, false);

        return view;
    }

}
