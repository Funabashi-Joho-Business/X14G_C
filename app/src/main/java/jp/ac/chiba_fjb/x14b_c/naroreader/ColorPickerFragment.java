package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ColorPickerFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {


    public ColorPickerFragment() {
        // Required empty public constructor
    }



    //インタフェイスの定義
    public interface OnDialogButtonListener{
        void onDialogButton(int red,int blue,int green);
    }
    //インタフェイスのインスタンス保存用
    ColorPickerFragment.OnDialogButtonListener mListener;

    //ボタン動作のインスタンスを受け取る
    public void setOnDialogButtonListener(OnDialogButtonListener listener){mListener =  listener;}

    SeekBar seekRed;
    SeekBar seekBlue;
    SeekBar seekGreen;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color_picker, container, false);

        seekRed = (SeekBar) view.findViewById(R.id.seekBar);
        seekBlue = (SeekBar) view.findViewById(R.id.seekBar2);
        seekGreen = (SeekBar) view.findViewById(R.id.seekBar3);
        TextView tx = (TextView) view.findViewById(R.id.textView);
        Button b1 = (Button) view.findViewById(R.id.Button);

        seekRed.setOnSeekBarChangeListener(this);
        seekBlue.setOnSeekBarChangeListener(this);
        seekGreen.setOnSeekBarChangeListener(this);
        b1.setOnClickListener(this);
        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        TextView tx = (TextView) getView().findViewById(R.id.textView);

        tx.setBackgroundColor(Color.rgb(seekRed.getProgress(),seekBlue.getProgress(),seekGreen.getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        mListener.onDialogButton(seekRed.getProgress(),seekBlue.getProgress(),seekGreen.getProgress());
        getDialog().cancel();
    }
}
