package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RankPointFragment extends DialogFragment implements View.OnClickListener {


    public RankPointFragment() {
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

        View view = inflater.inflate(R.layout.fragment_rank_point, container, false);

        Button b1 = (Button) view.findViewById(R.id.button1);
        Button b2 = (Button) view.findViewById(R.id.button2);



        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

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
                if(rg1.getCheckedRadioButtonId() != -1){
                    RadioButton rb = (RadioButton) getView().findViewById(rg1.getCheckedRadioButtonId());
                    String rb1 = (String) rb.getText();
                    rb1.substring(0,1);
                    int rbi = Integer.parseInt(rb1);
                }

                getDialog().cancel();
                break;
            case R.id.button1:
                getDialog().cancel();
                break;
        }
    }
}
