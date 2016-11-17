package jp.ac.chiba_fjb.x14b_c.naroreader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import to.pns.lib.SQLite;

public class NovelDB extends SQLite {
    public NovelDB(Context context) {
        super(context, "novel.db", 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql;
        //ブックマーク用テーブルの作成
        sql = "create table t_bookmark(n_code text primary key,b_update date,b_category int)";
        db.execSQL(sql);
        //ノベル名用テーブルの作成
        sql = "create table t_novel(n_code text primary key,n_name text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addBookmark(String ncode, String name, Date update, int category){
        String d = new java.sql.Timestamp(update.getTime()).toString();
        String sql;
        //ブックマークデータの追加
        sql = String.format("replace into t_bookmark values('%s','%s','%d')",STR(ncode),d,category);
        exec(sql);
        System.out.println(sql);
        //ノベルデータの追加
        sql = String.format("replace into t_novel values('%s','%s')",STR(ncode),STR(name));
        exec(sql);
        System.out.println(sql);

    }

    public List<NovelBookmark> getBookmark(){
        String sql;
        sql = "select * from t_bookmark natural join t_novel";
        Cursor r = query(sql);

        List<NovelBookmark> list = new ArrayList<NovelBookmark>();
        while(r.moveToNext()){
            String d = r.getString(1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(java.sql.Timestamp.valueOf(r.getString(1)));
            NovelBookmark b = new NovelBookmark(r.getString(0),r.getString(3),r.getInt(2),cal);
            list.add(b);
        }
        r.close();
        return list;
    }


}
