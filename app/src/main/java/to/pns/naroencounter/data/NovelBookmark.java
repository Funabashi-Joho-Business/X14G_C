package to.pns.naroencounter.data;

import java.util.Calendar;

/**
 * ブックマークデータ保存用
 */

public class NovelBookmark {
    public NovelBookmark(String ncode,int category,Calendar update,int mark){
        //書籍情報を取り込む
        mNCode = ncode;         //作品コード
        mUpdate = update;       //更新日
        mCategory = category;   //カテゴリ
        mMark = mark;           //しおり
    }
    String mNCode;
    Calendar mUpdate;
    int mCategory;
    int mMark;

    public String getCode() {
        return mNCode;
    }
    public Calendar getUpdate() {
        return mUpdate;
    }
    public int getCategory(){
        return mCategory;
    }
    public int getMark(){
        return mMark;
    }
}
