package jp.ac.chiba_fjb.x14b_c.naroreader.Titles;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPanelFragment extends Fragment implements View.OnClickListener {


    public SearchPanelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_panel, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.imageSearch).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String[] order = {"hyoka","weekly"};
        final String[] target = {"title","ex","keyword","wname"};
        final int[] genre = {1,2,3,4,98,99};
        //検索
        String word = ((EditText)getView().findViewById(R.id.editWord)).getText().toString();
        String exclusion = ((EditText)getView().findViewById(R.id.editExclusion)).getText().toString();
        int sort = ((RadioGroup)getView().findViewById(R.id.groupSort)).getCheckedRadioButtonId()-R.id.radioSort0;
        int type = ((RadioGroup)getView().findViewById(R.id.groupType)).getCheckedRadioButtonId();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("out=json&gzip=5&lim=500");
            if(word.length() > 0)
                sb.append("&word="+ URLEncoder.encode(word,"UTF-8"));
            if(exclusion.length() > 0)
                sb.append("&notword="+ URLEncoder.encode(exclusion,"UTF-8"));
            if(sort > 0)
                sb.append("&order="+ URLEncoder.encode(order[sort-1],"UTF-8"));

            for(int i=R.id.checkTarget0;i<=R.id.checkTarget3;i++){
                if(((CheckBox)getView().findViewById(i)).isChecked())
                   sb.append("&"+target[i-R.id.checkTarget0]+"=1");
            }
            String genreCode = "";
            for(int i=R.id.checkGenre0;i<=R.id.checkGenre5;i++){
                if(((CheckBox)getView().findViewById(i)).isChecked()){
                     if(genreCode.length() > 0)
                          genreCode += "-" + genre[i - R.id.checkGenre0];
                     else
                          genreCode += genre[i - R.id.checkGenre0];
                 }
            }
            if(genreCode.length() > 0)
                sb.append("&biggenre="+genreCode);

            if(type != R.id.radioButton1){
                String typeCode = (String)getView().findViewById(type).getTag();
                sb.append("&type="+typeCode);
            }

            NaroReceiver.search(getContext(),sb.toString());
            Snackbar.make(getView(), "検索開始", Snackbar.LENGTH_SHORT).show();

            ((MainActivity)getActivity()).changeFragment(SearchFragment.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
