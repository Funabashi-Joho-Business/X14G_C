package jp.ac.chiba_fjb.x14b_c.naroreader.data;

import java.util.Calendar;

/**
 * ブックマークデータ保存用
 */

public class NovelBookmark {
    public NovelBookmark(String ncode,String name,int category,Calendar update){
<<<<<<< HEAD
        mNCode = ncode;
        mName = name;
        mUpdate = update;
        mCategory = category;
=======
        //書籍情報を取り込む
        mNCode = ncode;         //作品コード
        mName = name;           //作品名
        mUpdate = update;       //更新日
        mCategory = category;   //カテゴリ
>>>>>>> 8d14d110d6d299b94a001e9bf6efdb6cba897543
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
