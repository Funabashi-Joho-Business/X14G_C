package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.text.NumberFormat;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;

public class ConfigFragment extends Fragment implements View.OnClickListener {


    private String mUserId;

    public ConfigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // タイトルを設定
        getActivity().setTitle("設定");

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_config, container, false);


        NovelDB settingDB = new NovelDB(getContext());
        String id = settingDB.getSetting("loginId","");
        String pass = settingDB.getSetting("loginPass","");
        boolean updateCheck = settingDB.getSetting("updateCheck",false);
        int updateTime = settingDB.getSetting("updateTime",60);
        long fileSize = settingDB.getFileSize()/1024;
        settingDB.close();

        mUserId = id;

        NumberFormat nf = NumberFormat.getNumberInstance();

        ((TextView) view.findViewById(R.id.LoginID)).setText(id);
        ((TextView) view.findViewById(R.id.loginPass)).setText(pass);
        ((Switch)view.findViewById(R.id.switchUpdateCheck)).setChecked(updateCheck);
        ((EditText)view.findViewById(R.id.editUpdateTime)).setText(""+updateTime);
        ((TextView) view.findViewById(R.id.textFileSize)).setText(nf.format(fileSize)+"KB");
        view.findViewById(R.id.buttonFix).setOnClickListener(this);
        return view;
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
            boolean updateCheck = ((Switch)getView().findViewById(R.id.switchUpdateCheck)).isChecked();
            int updateTime = Integer.parseInt(((EditText)getView().findViewById(R.id.editUpdateTime)).getText().toString());
            NovelDB db = new NovelDB(getContext());
            db.setSetting("loginId",id.getText().toString());
            db.setSetting("loginPass",pass.getText().toString());
            db.setSetting("updateCheck",updateCheck);
            db.setSetting("updateTime",updateTime);

            if(!mUserId.equals(id.getText().toString())){
                //違うIDが設定されたら、ブックマークをクリア
                db = new NovelDB(getContext());
                db.clearBookmark();
                db.close();
                mUserId = id.getText().toString();
                //ブックマークをネットから取得
                getContext().sendBroadcast(new Intent(getContext(),NaroReceiver.class).setAction(NaroReceiver.ACTION_BOOKMARK));
            }
            db.close();
            Snackbar.make(getView(), "保存完了", Snackbar.LENGTH_SHORT).show();
            //設定更新を通知
            getContext().sendBroadcast(new Intent(getContext(),NaroReceiver.class).setAction(NaroReceiver.ACTION_UPDATE_SETTING));
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonFix:
                NovelDB db = new NovelDB(getContext());
                db.optimisation();
                NumberFormat nf = NumberFormat.getNumberInstance();
                ((TextView) getView().findViewById(R.id.textFileSize)).setText(nf.format(db.getFileSize()/1024)+"KB");
                db.close();
                break;
        }
    }
}
