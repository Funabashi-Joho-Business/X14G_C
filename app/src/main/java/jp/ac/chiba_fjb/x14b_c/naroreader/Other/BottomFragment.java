package jp.ac.chiba_fjb.x14b_c.naroreader.Other;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;

public class BottomFragment extends Fragment implements View.OnClickListener {
    int mMenuId;
    Fragment mFragment;
    public void setMenu(int id,Fragment f){
        mMenuId = id;
        mFragment = f;

        Menu menu = new CustomMenu(getContext());
        MenuInflater inflaterMenu = new MenuInflater(getContext());
        inflaterMenu.inflate(mMenuId,menu);

        final RelativeLayout layout = (RelativeLayout) getView().findViewById(R.id.gridButton);
        layout.removeAllViews();

        Point p = new Point();
        ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(p);
        int size = menu.size();

        int width = 0;
        int beforeItem = 1;
        for(int i=0;i<size;i++) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            MenuItem item = menu.getItem(i);
            Button button = new Button(getContext());
            button.setCompoundDrawablesWithIntrinsicBounds(null, item.getIcon(), null, null);
            button.setText(item.getTitle());
            button.setLayoutParams(lp);
            button.setId(i + 1);
            button.setTextSize(10.0f);
            button.setTag(item);
            button.setOnClickListener(this);
            button.measure(-1, -1);
            if (i > 0) {
                if (width + button.getMeasuredWidth() < p.x) {
                    lp.addRule(RelativeLayout.ALIGN_TOP, i);
                    lp.addRule(RelativeLayout.RIGHT_OF, i);
                } else {
                    lp.addRule(RelativeLayout.BELOW, beforeItem);
                    beforeItem = i + 1;
                    width = 0;

                }

            }
            width += button.getMeasuredWidth();
            System.out.println(width);
            layout.addView(button);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return View.inflate(getContext(), R.layout.sub_contents, null);

  /*
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetBehavior behavior = BottomSheetBehavior.from((View)view.getParent());
                behavior.setPeekHeight(layout.getMeasuredHeight());
            }
        });*/
    }




    @Override
    public void onClick(View view) {

    }
}
