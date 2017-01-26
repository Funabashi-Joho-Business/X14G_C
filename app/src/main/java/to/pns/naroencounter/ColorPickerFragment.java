package to.pns.naroencounter;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ColorPickerFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    class TextEvent implements TextWatcher{
        int mId;
        public TextEvent(int id){
            mId = id;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int value = Integer.parseInt(s.toString());
            if(mId == R.id.editR )
                ((SeekBar)getView().findViewById(R.id.seekBarR)).setProgress(value);
            if(mId == R.id.editG )
                ((SeekBar)getView().findViewById(R.id.seekBarG)).setProgress(value);
            if(mId == R.id.editB )
                ((SeekBar)getView().findViewById(R.id.seekBarB)).setProgress(value);
        }
    }


    public ColorPickerFragment() {
        // Required empty public constructor
    }



    //インタフェイスの定義
    public interface OnDialogButtonListener{
        void onDialogButton();
        void onColorChange(int color);
    }
    //インタフェイスのインスタンス保存用
    ColorPickerFragment.OnDialogButtonListener mListener;

    //ボタン動作のインスタンスを受け取る
    public void setOnDialogButtonListener(OnDialogButtonListener listener){mListener =  listener;}

    SeekBar seekRed;
    SeekBar seekBlue;
    SeekBar seekGreen;

    int mColor;
    public void setColor(int color){
        mColor = color;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_color_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seekRed = (SeekBar) view.findViewById(R.id.seekBarR);
        seekBlue = (SeekBar) view.findViewById(R.id.seekBarB);
        seekGreen = (SeekBar) view.findViewById(R.id.seekBarG);
        view.findViewById(R.id.imageClose).setOnClickListener(this);
        view.findViewById(R.id.buttonSet).setOnClickListener(this);

        seekRed.setOnSeekBarChangeListener(this);
        seekBlue.setOnSeekBarChangeListener(this);
        seekGreen.setOnSeekBarChangeListener(this);

        seekRed.setProgress(Color.red(mColor));
        seekGreen.setProgress(Color.green(mColor));
        seekBlue.setProgress(Color.blue(mColor));

        ((EditText)view.findViewById(R.id.editR)).addTextChangedListener(new TextEvent(R.id.editR));
        ((EditText)view.findViewById(R.id.editG)).addTextChangedListener(new TextEvent(R.id.editG));
        ((EditText)view.findViewById(R.id.editB)).addTextChangedListener(new TextEvent(R.id.editB));
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(),  R.style.TransparentDialogTheme);
        dialog.setTitle("ColorPicker");

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.alpha = 0.98f;
        dialog.getWindow().setAttributes(lp);

        return dialog;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        TextView tx = (TextView) getView().findViewById(R.id.textView);
        tx.setBackgroundColor(Color.rgb(seekRed.getProgress(),seekGreen.getProgress(),seekBlue.getProgress()));

        ((EditText)getView().findViewById(R.id.editR)).setText(""+seekRed.getProgress());
        ((EditText)getView().findViewById(R.id.editG)).setText(""+seekGreen.getProgress());
        ((EditText)getView().findViewById(R.id.editB)).setText(""+seekBlue.getProgress());

        int color = Color.rgb(seekRed.getProgress(),seekGreen.getProgress(),seekBlue.getProgress());
        mListener.onColorChange(color);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.imageClose)
            mListener.onDialogButton();
        getDialog().cancel();
    }
}
