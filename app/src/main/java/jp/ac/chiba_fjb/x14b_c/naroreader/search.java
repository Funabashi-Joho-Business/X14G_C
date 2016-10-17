package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class search extends Fragment implements View.OnClickListener {
    subSearch sub = new subSearch();


    public search() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ///////////////////検索条件を受け取り、検索・抽出を実行させる（要SQL）//////////////////////

        //Inflate the layout for this fragment;
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        view.findViewById(R.id.wordsearch);
        view.findViewById(R.id.searchbutton).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        EditText Setext = (EditText)getView().findViewById(R.id.wordsearch);
        String s;
        s = Setext.getText().toString();
        sub.start();
    }
}

class subSearch extends Thread{
        public void Subclass001(String s){
            TbnReader.getKeyword(s);            // サブスレッドで実行するもの
        }
}
