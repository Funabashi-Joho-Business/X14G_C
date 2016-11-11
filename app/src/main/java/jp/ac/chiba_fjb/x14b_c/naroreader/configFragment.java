package jp.ac.chiba_fjb.x14b_c.naroreader;



import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;

public class ConfigFragment extends Fragment implements View.OnClickListener {


    public ConfigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        // タイトルを設定
        toolbar.setTitle("設定");



        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_config, container, false);

        Button b1 = (Button) view.findViewById(R.id.config4);
        Button b2 = (Button) view.findViewById(R.id.config5);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.config4: //ログインダイアログ
                // ダイアログを表示する
                DialogFragment newFragment = new LoginFragment();
                newFragment.show(getFragmentManager(),null);
            break;

        }
    }


}
