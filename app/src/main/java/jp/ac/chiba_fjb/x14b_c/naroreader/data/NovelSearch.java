package jp.ac.chiba_fjb.x14b_c.naroreader.data;

import java.util.Calendar;

/**
 * 検索データ保存用
 */

public class NovelSearch {
    public NovelSearch(String ncode,String name,int category,Calendar update,String keyword){
        //書籍情報を取り込む
        mNCode = ncode;         //作品コード
        mName = name;           //作品名
        mUpdate = update;       //更新日
        mCategory = category;   //カテゴリ
        mkeyword = keyword;     //検索ワード
    }
    String mNCode;
    String mName;
    Calendar mUpdate;
    int mCategory;
    String mkeyword;

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

    public String getKeyword(String word){
        return mkeyword;
    }


}
