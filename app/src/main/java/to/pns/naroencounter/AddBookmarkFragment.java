package to.pns.naroencounter;


import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import to.pns.naroencounter.Other.NaroReceiver;
import to.pns.naroencounter.Other.NovelDB;
import to.pns.naroencounter.data.NovelInfo;
import to.pns.naroencounter.data.TbnReader;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBookmarkFragment extends DialogFragment implements View.OnClickListener {
    private String mNCode;
    private boolean mFlag;

    public static void show(FragmentActivity activity, String ncode, boolean flag){
        Bundle bn = new Bundle();
        bn.putString("ncode",ncode);
        bn.putBoolean("mode",flag);
        //フラグメントのインスタンスを作成
        AddBookmarkFragment f = new AddBookmarkFragment();
        f.setArguments(bn);
        f.show(activity.getSupportFragmentManager(),"");
    }

    public AddBookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_bookmark, container, false);

        TextView tv = (TextView) view.findViewById(R.id.textTitle);
        TextView tv2 = (TextView) view.findViewById(R.id.textMode);
        view.findViewById(R.id.addYes).setOnClickListener(this);
        view.findViewById(R.id.addNo).setOnClickListener(this);

        //バンドルの取得
        if(getArguments() != null){
            Bundle bn = getArguments();
            mNCode = bn.getString("ncode");
            mFlag = bn.getBoolean("mode");

            NovelDB db = new NovelDB(getContext());
            NovelInfo novelInfo = db.getNovelInfo(mNCode);
            db.close();

            String title = "";
            if(novelInfo != null)
                title = novelInfo.title;

            tv.setText(title);
            if(mFlag)
                tv2.setText("ブックマークしますか？");
            else
                tv2.setText("ブックマーク解除しますか？");
        }
        return view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("ブックマーク");
        return dialog;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            case R.id.addYes:
                new Thread(){
                    @Override
                    public void run() {

                        NovelDB settingDB = new NovelDB(getContext());
                        String id = settingDB.getSetting("loginId","");
                        String pass = settingDB.getSetting("loginPass","");
                        settingDB.close();

                        Snackbar.make(getActivity().findViewById(R.id.coordinator),"ブックマーク処理中", Snackbar.LENGTH_SHORT).show();

                        //ログイン処理
                        String hash = TbnReader.getLoginHash(id,pass);
                        if(mNCode != null){
                            if(mFlag){
                                final boolean flag = TbnReader.setBookmark(hash, mNCode);
                                if(flag)
                                    NaroReceiver.updateBookmark(getContext());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(getActivity().findViewById(R.id.coordinator), flag?"ブックマーク完了":"ブックマークエラー", Snackbar.LENGTH_SHORT).show();
                                        getDialog().cancel();
                                    }
                                });
                            }else{
                                final boolean flag = TbnReader.clearBookmark(hash, mNCode);
                                if(flag)
                                    NaroReceiver.updateBookmark(getContext());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(getActivity().findViewById(R.id.coordinator), flag?"ブックマーク解除":"ブックマークエラー", Snackbar.LENGTH_SHORT).show();
                                        getDialog().cancel();
                                    }
                                });
                            }

                        }
                    }
                }.start();

                break;
            case R.id.addNo:
                getDialog().cancel();
                break;
        }
    }
}

