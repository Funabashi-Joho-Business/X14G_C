package jp.ac.chiba_fjb.x14b_c.naroreader;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

public class BottomFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    int mMenuId;
    Fragment mFragment;
    public void setMenu(int id,Fragment f){
        mMenuId = id;
        mFragment = f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), R.style.TransparentDialogTheme);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, R.style.TransparentDialogTheme);

        final View view = View.inflate(getContext(), R.layout.sub_contents, null);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Menu menu = new CustomMenu(getContext());
        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(mMenuId,menu);

        final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.gridButton);

        Point p = new Point();
        ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(p);
        float d = getContext().getResources().getDisplayMetrics().density;
        int margin = (int)(3*d);

        int size = menu.size();

        int width = 0;
        int beforeItem = 1;
        for(int i=0;i<size;i++){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

            MenuItem item = menu.getItem(i);
            Button button = new Button(getContext());
            button.setCompoundDrawablesWithIntrinsicBounds(null,item.getIcon(),null,null);
            button.setText(item.getTitle());
            button.setLayoutParams(lp);
            button.setId(i+1);
            button.setTextSize(10.0f);
            button.setTag(item);
            button.setOnClickListener(this);
            button.measure(-1,-1);
            if(i >0){
                if(width+button.getMeasuredWidth() < p.x){
                    lp.addRule(RelativeLayout.ALIGN_TOP, i);
                    lp.addRule(RelativeLayout.RIGHT_OF, i);
                }
                else{
                    lp.addRule(RelativeLayout.BELOW, beforeItem);
                    beforeItem = i+1;
                    width = 0;

                }

            }
            width += button.getMeasuredWidth();
            System.out.println(width);
            layout.addView(button);
        }

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetBehavior behavior = BottomSheetBehavior.from((View)view.getParent());
                behavior.setPeekHeight(layout.getMeasuredHeight());
            }
        });






    }


    @Override
    public void onClick(View view) {
        mFragment.onOptionsItemSelected((MenuItem)view.getTag());
    }
}