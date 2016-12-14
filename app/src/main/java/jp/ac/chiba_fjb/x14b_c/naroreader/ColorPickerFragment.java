package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ColorPickerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {


    public ColorPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color_picker, container, false);

        SeekBar seekRed = (SeekBar) getView().findViewById(R.id.seekBar);
        SeekBar seekBlue = (SeekBar) getView().findViewById(R.id.seekBar2);
        SeekBar seekGreen = (SeekBar) getView().findViewById(R.id.seekBar3);
        TextView tx = (TextView) getView().findViewById(R.id.textView);

        seekRed.setOnSeekBarChangeListener(this);
        seekBlue.setOnSeekBarChangeListener(this);
        seekGreen.setOnSeekBarChangeListener(this);
        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        TextView tx = (TextView) getView().findViewById(R.id.textView);
        SeekBar seekRed = (SeekBar) getView().findViewById(R.id.seekBar);
        SeekBar seekBlue = (SeekBar) getView().findViewById(R.id.seekBar2);
        SeekBar seekGreen = (SeekBar) getView().findViewById(R.id.seekBar3);

        tx.setBackgroundColor(Color.rgb(seekRed.getProgress(),seekBlue.getProgress(),seekGreen.getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
