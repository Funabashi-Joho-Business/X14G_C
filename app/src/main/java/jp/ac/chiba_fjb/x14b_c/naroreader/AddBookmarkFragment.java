package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBookmarkFragment extends DialogFragment implements View.OnClickListener {
    public interface OnEditUserListener{
        void onEditUser(String id,String pass);
    }
    private OnEditUserListener mListener;
    public AddBookmarkFragment() {
        // Required empty public constructor
    }

    public void setOnAddBookmarkListener(OnEditUserListener listener){
        mListener = listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_bookmark, container, false);

        Button b1 = (Button) view.findViewById(R.id.addYes);
        Button b2 = (Button) view.findViewById(R.id.addNo);
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
                getDialog().cancel();
                break;
            case R.id.addNo:
                getDialog().cancel();
                break;
        }


    }
}

