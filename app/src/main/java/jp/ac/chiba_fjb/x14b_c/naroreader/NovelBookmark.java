package jp.ac.chiba_fjb.x14b_c.naroreader;

import java.util.Calendar;

/**
 * Created by oikawa on 2016/09/20.
 */

public class NovelBookmark {
    public NovelBookmark(String ncode,String name,int category,Calendar update){
        mNCode = ncode;
        mName = name;
        mUpdate = update;
        mCategory = category;
    }
    String mNCode;
    String mName;
    Calendar mUpdate;
    int mCategory;

    public String getCode() {
        return mNCode;
    }

    public String getName() {
        return mName;
    }

    public Calendar getUpdate() {
        return mUpdate;
    }
    public int getCategory(){
        return mCategory;
    }
}
