package jp.ac.chiba_fjb.x14b_c.naroreader;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;

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

        Spinner s1 = (Spinner) view.findViewById(R.id.spinner);
        Spinner s2 = (Spinner) view.findViewById(R.id.spinner3);
        Spinner s3 = (Spinner) view.findViewById(R.id.spinner4);

        Button b1 = (Button) view.findViewById(R.id.config4);
        Button b2 = (Button) view.findViewById(R.id.config5);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

        NovelDB settingDB = new NovelDB(getContext());
        String ss1 = settingDB.getSetting("fontSize");
        String ss2 = settingDB.getSetting("fontColor");
        String ss3 = settingDB.getSetting("BackColor");
        settingDB.close();

        setSelection(s1,ss1);
        setSelection(s2,ss2);
        setSelection(s3,ss3);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.config4: //ログインダイアログ
                // ダイアログを表示する


                LoginFragment newFragment = new LoginFragment();
                newFragment.setOnEditUserListener(new LoginFragment.OnEditUserListener() {
                    @Override
                    public void onEditUser(String id, String pass) {
                        NovelDB settingDB = new NovelDB(getContext());
                        settingDB.setSetting("loginId",id);
                        settingDB.setSetting("loginPass",pass);
                        settingDB.close();
                    }
                });
                newFragment.show(getFragmentManager(),null);

                break;
            case R.id.config5: //設定の保存
                Spinner s1 = (Spinner) getView().findViewById(R.id.spinner);
                Spinner s2 = (Spinner) getView().findViewById(R.id.spinner3);
                Spinner s3 = (Spinner) getView().findViewById(R.id.spinner4);

                String t1 = (String)s1.getSelectedItem();
                String t2 = (String)s2.getSelectedItem();
                String t3 = (String)s3.getSelectedItem();

                settingSave(t1,t2,t3);
                break;
        }
    }

    public void settingSave(String fontSize,String fontColor,String backColor){
        NovelDB settingDB = new NovelDB(getContext());

        settingDB.setSetting("fontSize",fontSize);
        settingDB.setSetting("fontColor",fontColor);
        settingDB.setSetting("backColor",backColor);

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
}
