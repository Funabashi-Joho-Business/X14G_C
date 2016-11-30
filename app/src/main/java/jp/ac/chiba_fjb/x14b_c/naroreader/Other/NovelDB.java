package jp.ac.chiba_fjb.x14b_c.naroreader.Other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSubTitle;
import to.pns.lib.AppDB;

public class NovelDB extends AppDB {
    public NovelDB(Context context) {
        super(context, "novel4.db", 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);

        String sql;
        //ブックマーク用テーブルの作成
        sql = "create table t_bookmark(n_code text primary key,b_update date,b_category int)";
        db.execSQL(sql);
        //ノベル名用テーブルの作成
        sql = "create table t_novel(n_code text primary key,n_name text)";
        db.execSQL(sql);

        sql = "create table t_novel_reg(ncode text primary key)";
        db.execSQL(sql);
        sql = createSqlCreateClass(NovelInfo.class,"t_novel_info","ncode");
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,  int oldVersion, int newVersion) {
        if(oldVersion < 2){
            //ブックマーク用テーブルの作成
            String sql;
            sql = "create table t_novel_sub(n_code text,sub_no int,sub_title text,sub_regdate date,sub_update date,primary key(n_code,sub_no))";
            db.execSQL(sql);
        }
    }
    public void addSubTitle(String ncode,List<NovelSubTitle> list){
        begin();

        String sql;
        sql = String.format("delete from t_novel_sub where n_code='%s'",STR(ncode));
        exec(sql);

        int index = 1;
        for(NovelSubTitle sub : list){
            ContentValues values = new ContentValues();
            values.put("n_code", ncode);
            values.put("sub_no", index++);
            values.put("sub_title", sub.title);
            values.put("sub_regdate", new java.sql.Timestamp(sub.date.getTime()).toString());
            if(sub.update != null)
                values.put("sub_update", new java.sql.Timestamp(sub.update.getTime()).toString());
            insert("t_novel_sub",values);
        }
        commit();
    }

    public void addNovel(String ncode){
        String sql = String.format("replace into t_novel_reg values(UPPER('%s'))",STR(ncode));
        exec(sql);
    }
    public void delNovel(String ncode){
        String sql = String.format("delete from t_novel_reg where n_code = '%s'",STR(ncode));
        exec(sql);
    }
    public List<String> getNovel(){
        List<String> list = new ArrayList<String>();
        String sql = String.format("select * from t_novel_reg");
        Cursor c = query(sql);
        while(c.moveToNext()){
            list.add(c.getString(0));
        }
        return list;
    }
    public void addNovelInfo(List<NovelInfo> novelInfo){
        for(NovelInfo info : novelInfo){
            String sql = createSqlReplaceClass(info,"t_novel_info");
            exec(sql);
        }
    }
    public NovelInfo getNovelInfo(String ncode){
        String sql = String.format("select * from t_novel_info where ncode LIKE '%s'",STR(ncode));
        List<NovelInfo> list = queryClass(sql,NovelInfo.class);
        if(list.size() == 0)
            return null;
        return list.get(0);

    }
    public void addBookmark(String ncode, String name, Date update, int category){
        String d = new java.sql.Timestamp(update.getTime()).toString();
        String sql;
        //ブックマークデータの追加
        sql = String.format("replace into t_bookmark values('%s','%s','%d')",STR(ncode),d,category);
        exec(sql);
        //ノベルデータの追加
        sql = String.format("replace into t_novel values('%s','%s')",STR(ncode),STR(name));
        exec(sql);
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

    public void addSearch(String ncode, String name, Date update, int category){
        String d = new java.sql.Timestamp(update.getTime()).toString();
        String sql;
        //ブックマークデータの追加
        sql = String.format("replace into t_bookmark values('%s','%s','%d','%s')",STR(ncode),d,category);
        exec(sql);
        System.out.println(sql);
        //ノベルデータの追加
        sql = String.format("replace into t_novel values('%s','%s')",STR(ncode),STR(name));
        exec(sql);
        System.out.println(sql);

    }
    public List<Map<String,String>> getTitles(){
        //メインに登録されているデータを抽出
        String sql = "select * from t_novel_reg natural left join t_novel_info";
        return queryMap(sql);
    }

    public List<NovelSubTitle> getSubTitles(String ncode) {
        //メインに登録されているデータを抽出
        String sql = String.format("select * from t_novel_sub where n_code='%s' order by sub_no",STR(ncode));
        Cursor c = query(sql);
        List<NovelSubTitle> list = new ArrayList<NovelSubTitle>();
        while(c.moveToNext()){
            NovelSubTitle n = new NovelSubTitle();
            n.title = c.getString(2);
            n.date = java.sql.Timestamp.valueOf(c.getString(3));
            if(c.getString(4) != null)
                n.update = java.sql.Timestamp.valueOf(c.getString(4));
            list.add(n);
        }
        return list;
    }

/*    public List<NovelSearch> getSearch(){
        String sql;
        sql = "select * from t_bookmark natural join t_novel";
        Cursor r = query(sql);

        List<NovelSearch> list = new ArrayList<>();
        while(r.moveToNext()){
            String d = r.getString(1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(java.sql.Timestamp.valueOf(r.getString(1)));
            NovelSearch b = new NovelSearch(r.getString(0),r.getString(3),r.getInt(2),cal,r.getString(4));
            list.add(b);
        }
        r.close();
        return list;
    }
    */
}
