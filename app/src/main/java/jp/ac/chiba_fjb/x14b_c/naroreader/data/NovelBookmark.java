package jp.ac.chiba_fjb.x14b_c.naroreader.data;

import java.util.Calendar;

/**
 * ブックマークデータ保存用
 */

public class NovelBookmark {
    public NovelBookmark(String ncode,int category,Calendar update){
        //書籍情報を取り込む
        mNCode = ncode;         //作品コード
        mUpdate = update;       //更新日
        mCategory = category;   //カテゴリ
    }
    String mNCode;
    Calendar mUpdate;
    int mCategory;

    public String getCode() {
        return mNCode;
    }
    public Calendar getUpdate() {
        return mUpdate;
    }
    public int getCategory(){
        return mCategory;
    }
}
