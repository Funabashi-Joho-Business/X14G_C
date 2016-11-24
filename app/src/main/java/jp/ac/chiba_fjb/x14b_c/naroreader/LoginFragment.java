package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends DialogFragment implements View.OnClickListener {


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button b1 = (Button) view.findViewById(R.id.loginBt);
        b1.setOnClickListener(this);

        return view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("ログイン");
        return dialog;
    }

    @Override
    public void onClick(View v) {
        getDialog().cancel();
    }
}
