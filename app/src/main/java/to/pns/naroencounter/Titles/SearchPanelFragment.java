package to.pns.naroencounter.Titles;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import to.pns.naroencounter.MainActivity;
import to.pns.naroencounter.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPanelFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    private View mView;

    public SearchPanelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mView != null)
            return mView;
        return inflater.inflate(R.layout.fragment_search_panel, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("検索");
        if(mView != null)
            return;
        getView().findViewById(R.id.imageSearch).setOnClickListener(this);
        ((SeekBar)view.findViewById(R.id.seekLimit)).setOnSeekBarChangeListener(this);
        mView = getView();
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
            sb.append("out=json&gzip=5");

            int limit = ((SeekBar)getView().findViewById(R.id.seekLimit)).getProgress()*10+10;
            sb.append("&lim="+limit);
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

            //ソフトキーボードを非表示
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            //検索開始
            Bundle bundle = new Bundle();
            bundle.putString("params",sb.toString());
            bundle.putInt("writer",0);
           ((MainActivity)getActivity()).changeFragment(SearchFragment.class,bundle);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        ((TextView)getView().findViewById(R.id.textLimit)).setText(""+((progress+1)*10));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
