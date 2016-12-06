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
import android.widget.TextView;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBookmarkFragment extends DialogFragment implements View.OnClickListener {
    public AddBookmarkFragment() {
        // Required empty public constructor
    }

    //インタフェイスの定義
    public interface OnDialogButtonListener{
        void onDialogButton();
    }
    //インタフェイスのインスタンス保存用
    OnDialogButtonListener mListener;

    //ボタン動作のインスタンスを受け取る
    public void setOnDialogButtonListener(OnDialogButtonListener listener){mListener =  listener;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_bookmark, container, false);

        TextView tv = (TextView) view.findViewById(R.id.textTitle);
        Button b1 = (Button) view.findViewById(R.id.addYes);
        Button b2 = (Button) view.findViewById(R.id.addNo);

        //バンドルの取得
        if(getArguments() != null){
            Bundle bn = getArguments();
            String mTitle = bn.getString("title");

            tv.setText(mTitle);
        }

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

        return view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("ブックマーク");
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addYes:
                //ブックマークに追加する処理
                mListener.onDialogButton();
                getDialog().cancel();
                break;
            case R.id.addNo:
                getDialog().cancel();
                break;
        }
    }
}

