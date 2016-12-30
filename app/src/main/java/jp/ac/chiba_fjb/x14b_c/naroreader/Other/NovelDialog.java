package jp.ac.chiba_fjb.x14b_c.naroreader.Other;

import android.view.MenuItem;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;

/**
 * Created by oikawa on 2016/12/30.
 */

public class NovelDialog extends BottomDialog {
    private String mNCode;
    public void setCode(String ncode){
        mNCode = ncode;
        setMenu(R.menu.panel_novel,null);
    }

    @Override
    public void onMenu(MenuItem menu) {
        switch(menu.getItemId()){

        }
    }
}
