package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;

public class ConfigFragment extends Fragment  {


    private String mUserId;

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

        Spinner s1 = (Spinner) view.findViewById(R.id.spinner);
        Spinner s2 = (Spinner) view.findViewById(R.id.spinner3);
        Spinner s3 = (Spinner) view.findViewById(R.id.spinner4);

        NovelDB settingDB = new NovelDB(getContext());
        String id = settingDB.getSetting("loginId","");
        String pass = settingDB.getSetting("loginPass","");
        String ss1 = settingDB.getSetting("fontSize");
        String ss2 = settingDB.getSetting("fontColor");
        String ss3 = settingDB.getSetting("BackColor");
        settingDB.close();

        mUserId = id;

        TextView textId = (TextView) view.findViewById(R.id.LoginID);
        TextView textPass = (TextView) view.findViewById(R.id.loginPass);
        textId.setText(id);
        textPass.setText(pass);

        setSelection(s1,ss1);
        setSelection(s2,ss2);
        setSelection(s3,ss3);

        return view;
    }


    public void settingSave(String id,String pass,String fontSize,String fontColor,String backColor){
        NovelDB settingDB = new NovelDB(getContext());

        settingDB.setSetting("fontSize",fontSize);
        settingDB.setSetting("fontColor",fontColor);
        settingDB.setSetting("backColor",backColor);
        settingDB.setSetting("loginId",id);
        settingDB.setSetting("loginPass",pass);
        settingDB.close();
    }

    public static void setSelection(Spinner spinner, String item) {
        SpinnerAdapter adapter = spinner.getAdapter();
        int index = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(item)) {
                index = i; break;
            }
        }
        spinner.setSelection(index);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.config, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            //ソフトキーボードを非表示
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            TextView id = (TextView) getView().findViewById(R.id.LoginID);
            TextView pass = (TextView) getView().findViewById(R.id.loginPass);
            Spinner s1 = (Spinner) getView().findViewById(R.id.spinner);
            Spinner s2 = (Spinner) getView().findViewById(R.id.spinner3);
            Spinner s3 = (Spinner) getView().findViewById(R.id.spinner4);

            String t1 = (String) s1.getSelectedItem();
            String t2 = (String) s2.getSelectedItem();
            String t3 = (String) s3.getSelectedItem();

            settingSave(id.getText().toString(), pass.getText().toString(), t1, t2, t3);
            Snackbar.make(getView(), "保存完了", Snackbar.LENGTH_SHORT).show();

            if(!mUserId.equals(id)){
                //違うIDが設定されたら、ブックマークをクリア
                NovelDB db = new NovelDB(getContext());
                db.clearBookmark();;
                db.close();
                //ブックマークをネットから取得
                getContext().sendBroadcast(new Intent(getContext(),NaroReceiver.class).setAction(NaroReceiver.ACTION_BOOKMARK));
            }
        }
        return true;
    }
}
