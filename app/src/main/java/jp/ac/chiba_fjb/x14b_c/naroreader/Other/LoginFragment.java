package jp.ac.chiba_fjb.x14b_c.naroreader.Other;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends DialogFragment implements View.OnClickListener {
    public interface OnEditUserListener{
        void onEditUser(String id,String pass);
    }
    private OnEditUserListener mListener;
    public LoginFragment() {
        // Required empty public constructor
    }

    public void setOnEditUserListener(OnEditUserListener listener){
        mListener = listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button b1 = (Button) view.findViewById(R.id.loginBt);
        b1.setOnClickListener(this);

        NovelDB settingDB = new NovelDB(getContext());
        String id = settingDB.getSetting("loginId","");
        String pass = settingDB.getSetting("loginPass","");
        settingDB.close();


        TextView textId = (TextView) view.findViewById(R.id.LoginID);
        TextView textPass = (TextView) view.findViewById(R.id.loginPass);
        textId.setText(id);
        textPass.setText(pass);

        return view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("ログイン");
        return dialog;
    }

    @Override
    public void onClick(View v)
    {
        getDialog().cancel();
        //インターフェースでコールバックさせているけれど、ここでDBに直接保存した方が早い
        //ということでコールバックのサンプル用
        if(mListener!=null) {
            TextView id = (TextView) getView().findViewById(R.id.LoginID);
            TextView pass = (TextView) getView().findViewById(R.id.loginPass);
            mListener.onEditUser(id.getText().toString(),pass.getText().toString());
        }
    }
}
