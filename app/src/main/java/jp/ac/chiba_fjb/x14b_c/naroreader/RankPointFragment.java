package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelEvaluation;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;

import static jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader.setEvaluation;


/**
 * A simple {@link Fragment} subclass.
 */
public class RankPointFragment extends DialogFragment implements View.OnClickListener {


    private String mNCode;

    public RankPointFragment() {
        // Required empty public constructor
    }
    private boolean end2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_rank_point, container, false);

        Button b1 = (Button) view.findViewById(R.id.button1);
        Button b2 = (Button) view.findViewById(R.id.button2);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

        mNCode = getArguments().getString("ncode");

        //既存評価の確認
        new Thread(){
            @Override
            public void run() {
                NovelDB settingDB = new NovelDB(getContext());
                String id = settingDB.getSetting("loginId","");
                String pass = settingDB.getSetting("loginPass","");
                settingDB.close();
                String hash = TbnReader.getLoginHash(id,pass);
                final NovelEvaluation eva = TbnReader.getEvaluation(hash,mNCode);
                if(eva != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RadioGroup rg1 = (RadioGroup) getView().findViewById(R.id.RgBunpou);
                            RadioGroup rg2 = (RadioGroup) getView().findViewById(R.id.RgStory);
                            rg1.check(R.id.Rb1+eva.bunsyopoint-1);
                            rg2.check(R.id.Rs1+eva.storypoint-1);
                        }
                    });
                }



            }
        }.start();

        return view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("ポイント評価");
        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button2:
                //評価ポイントをつける処理
                RadioGroup rg1 = (RadioGroup) getView().findViewById(R.id.RgBunpou);
                RadioGroup rg2 = (RadioGroup) getView().findViewById(R.id.RgStory);
                int rbi = 0;
                int rbs = 0;

                if(rg1.getCheckedRadioButtonId() != -1){
                    RadioButton rb = (RadioButton) getView().findViewById(rg1.getCheckedRadioButtonId());
                    String rb1 = (String) rb.getText();
                    rb1 = rb1.substring(0,1);
                    rbi = Integer.parseInt(rb1);
                }
                if(rg2.getCheckedRadioButtonId() != -1){
                    RadioButton rb = (RadioButton) getView().findViewById(rg2.getCheckedRadioButtonId());
                    String rb2 = (String) rb.getText();
                    rb2 = rb2.substring(0,1);
                    rbs = Integer.parseInt(rb2);
                }
                if(rbi != 0 && rbs != 0){
                    final int finalRbi = rbi;
                    final int finalRbs = rbs;
                    new Thread(){
                        @Override
                        public void run() {

                            NovelDB settingDB = new NovelDB(getContext());
                            String id = settingDB.getSetting("loginId","");
                            String pass = settingDB.getSetting("loginPass","");
                            settingDB.close();

                            //ログイン処理
                            Bundle bn = getArguments();
                            String hash = TbnReader.getLoginHash(id,pass);
                            final boolean flag = setEvaluation(hash,bn.getString("ncode"), finalRbi, finalRbs);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(getTargetFragment().getView(), flag?"評価成功":"評価失敗", Snackbar.LENGTH_SHORT).show();
                                    getDialog().cancel();
                                }
                            });



                        }
                    }.start();
                }

                break;
            case R.id.button1:
                getDialog().cancel();
                break;
        }
    }
}
