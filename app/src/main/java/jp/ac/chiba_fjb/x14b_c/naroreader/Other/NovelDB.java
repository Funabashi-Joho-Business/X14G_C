package jp.ac.chiba_fjb.x14b_c.naroreader.Other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBody;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelContent;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelRanking;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSeries;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSubTitle;
import to.pns.lib.AppDB;

class NovelIndex{
    public String ncode;
    public int index;
}

class NovelRankingValue extends NovelRanking{
    public int ranking_kind1;
    public int ranking_kind2;
    public int ranking_kind3;
    public int ranking_index;
    public NovelRankingValue(int kind1,int kind2,int kind3,int index,NovelRanking v){
        ranking_kind1 = kind1;
        ranking_kind2 = kind2;
        ranking_kind3 = kind3;
        ranking_index = index;

        //全プロパティーのコピー
        Class c = v.getClass();
        Field[] fields = c.getFields();
        for(Field f : fields){
            try {
                String name = f.getName();
                if(name.charAt(0) != '$' && !name.equals("serialVersionUID"))
                   f.set(this,f.get(v));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

public class NovelDB extends AppDB {
    public NovelDB(Context context) {
        super(context, "novel00.db", 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);

        String sql;
        //ブックマーク用テーブルの作成
        sql = "create table t_bookmark(n_code text primary key,b_update date,b_category int)";
        db.execSQL(sql);
        //履歴用
        sql = "create table t_novel_history(ncode text primary key,his_date date)";
        db.execSQL(sql);
        sql = createSqlCreateClass(NovelInfo.class,"t_novel_info","ncode");
        db.execSQL(sql);

        sql = "create table t_novel_sub(n_code text,sub_no int,sub_title text,sub_regdate date,sub_update date,primary key(n_code,sub_no))";
        db.execSQL(sql);

        sql = "create table t_novel_content(n_code text,sub_no int,content_update date,content_original_size int,content_body blob,content_preface blog,content_trailer blob,content_tag blob,primary key(n_code,sub_no))";
        db.execSQL(sql);

        sql = "create table t_novel_read(n_code text,sub_no int,read_date date,primary key(n_code,sub_no))";
        db.execSQL(sql);

        sql = "create table t_novel_ranking(ranking_kind1 int,ranking_kind2,ranking_kind3,ranking_date date,primary key(ranking_kind1,ranking_kind2,ranking_kind3))";
        db.execSQL(sql);
        sql = createSqlCreateClass(NovelRankingValue.class,"t_novel_ranking_info","ranking_kind1","ranking_kind2","ranking_kind3","ranking_index");
        db.execSQL(sql);

        sql = "create table t_novel_series(s_code text primary key,series_title text,series_info text,series_writer int)";
        db.execSQL(sql);
        sql = "create table t_novel_series_bind(s_code text,n_code text,primary key(s_code,n_code))";
        db.execSQL(sql);

        sql = "create table t_novel_search(ncode text primary key,search_order int)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        String sql;
        if(i < 3) {
            sql = "drop table t_novel_content";
            db.execSQL(sql);
            sql = "create table t_novel_content(n_code text,sub_no int,content_update date,content_original_size int,content_body blob,content_preface blog,content_trailer blob,content_tag blob,primary key(n_code,sub_no))";
            db.execSQL(sql);
        }
        if(i < 4) {
            sql = "create table t_novel_series(s_code text primary key,series_title text,series_info text,series_writer int)";
            db.execSQL(sql);
            sql = "create table t_novel_series_bind(s_code text,n_code text,primary key(s_code,n_code))";
            db.execSQL(sql);
        }
        if(i==5){
            sql = "drop table t_novel_search";
            db.execSQL(sql);
        }
        if(i < 6) {
            sql = "create table t_novel_search(ncode text primary key,search_order int)";
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
    public static byte[] encodeValue(String value){
        if(value == null)
            return null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            GZIPOutputStream stream = new GZIPOutputStream(bytes){
                public GZIPOutputStream setLevel( int level ) {
                    def.setLevel(level);
                    return this;
                }
            }.setLevel(Deflater.BEST_COMPRESSION);
            stream.write(value.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes.toByteArray();
    }
    public static String decodeValue(byte[] value){
        if(value == null)
            return null;
        try {
            ByteArrayInputStream bytes = new ByteArrayInputStream(value);
            GZIPInputStream gis = new GZIPInputStream(bytes);
            InputStreamReader reader = new InputStreamReader(gis);
            BufferedReader in = new BufferedReader(reader);

            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = in.readLine()) != null) {
                sb.append(s);
            }
            in.close();
            reader.close();
            gis.close();
            bytes.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void addNovelContents(String ncode, int index, NovelBody novelBody){
        ContentValues values = new ContentValues();

        values.put("n_code", ncode);
        values.put("sub_no", index);
        values.put("content_update", new java.sql.Timestamp(new Date().getTime()).toString());
        values.put("content_original_size",novelBody.body.getBytes().length);
        values.put("content_body",encodeValue(novelBody.body));
        values.put("content_tag",encodeValue(novelBody.tag));
        values.put("content_preface",encodeValue(novelBody.preface));
        values.put("content_trailer",encodeValue(novelBody.trailer));
        replace("t_novel_content",values);
    }
    public void addNovelHistory(String ncode){
        String sql = String.format("replace into t_novel_history values('%s','%s')",
            STR(ncode.toUpperCase()),new java.sql.Timestamp(new Date().getTime()).toString());
        exec(sql);
    }
    public void delNovelHistory(String ncode){
        String sql = String.format("delete from t_novel_history where ncode = '%s'",STR(ncode));
        exec(sql);
    }
    public List<String> getNovelHistory(){
        List<String> list = new ArrayList<String>();
        String sql = String.format("select * from t_novel_history where his_date desc");
        Cursor c = query(sql);
        while(c.moveToNext()){
            list.add(c.getString(0));
        }
        c.close();
        return list;
    }
    public void addNovelInfo(List<NovelInfo> novelInfo){
        begin();
        for(NovelInfo info : novelInfo){
            String sql = createSqlReplaceClass(info,"t_novel_info");
            exec(sql);
        }
        commit();
    }
    public void setNovelSearch(List<NovelInfo> novelInfo){
        addNovelInfo(novelInfo);
        int i = 1;
        exec("delete from t_novel_search");
        for(NovelInfo info : novelInfo){
            String sql = String.format("insert into t_novel_search values('%s',%d);",info.ncode,i++);
            exec(sql);
        }
    }
    public List<NovelInfo> getNovelSearch(){
        String sql = String.format("select * from t_novel_info natural join t_novel_search order by search_order");
        List<NovelInfo> list = queryClass(sql,NovelInfo.class);
        return list;
    }
    public NovelInfo getNovelInfo(String ncode){
        String sql = String.format("select * from t_novel_info where ncode LIKE '%s'",STR(ncode));
        List<NovelInfo> list = queryClass(sql,NovelInfo.class);
        if(list.size() == 0)
            return null;
        return list.get(0);

    }
    public List<NovelInfo> getNovelInfoFromBookmark(){
        String sql = String.format("select * from t_novel_info where ncode in (select n_code from t_bookmark) order by novelupdated_at desc");
        List<NovelInfo> list = queryClass(sql,NovelInfo.class);
        return list;

    }

    public Map<String,NovelSeries> getNovelSeriesMap(List<String> listNcode){
        Map<String,NovelSeries> map = new HashMap<String,NovelSeries>();

        StringBuilder sb = new StringBuilder();
        for(String s : listNcode){
            NovelSeries series = getSeriesInfo(s);
            if(series != null)
            map.put(s,series);
        }
        return map;
    }

    public NovelSeries getSeriesInfo(String ncode){
        String sql = String.format("select * from t_novel_series where s_code in (select s_code from t_novel_series_bind where n_code='%s')",STR(ncode));
        Cursor c = query(sql);
        if(!c.moveToNext()) {
            c.close();
            return null;
        }
        NovelSeries series = new NovelSeries();
        series.scode = c.getString(0);
        series.title = c.getString(1);
        series.info = c.getString(2);
        series.writer = c.getInt(3);
        c.close();
        return series;

    }
    public boolean isNovelInfo(String ndoce){
        String sql = String.format("select 1 from t_novel_info where ncode = %s",ndoce.toUpperCase());
        Cursor c = query(sql);
        boolean flag = c.moveToNext();
        c.close();
        return flag;
    }
    public List<NovelInfo> getNovelInfo(List<String> listNcode){
        StringBuilder sb = new StringBuilder();
        for(String s : listNcode){
            if(sb.length() > 0)
                sb.append(",");
            sb.append(String.format("'%s'",s.toUpperCase()));
        }
        String sql = String.format("select * from t_novel_info where ncode in (%s)",sb.toString());
        return queryClass(sql,NovelInfo.class);
    }
    public Map<String,NovelInfo> getNovelInfoMap(List<String> listNcode){
        List<NovelInfo> novelInfo = getNovelInfo(listNcode);

        HashMap<String, NovelInfo> map = new HashMap<String, NovelInfo>();
        for(NovelInfo n : novelInfo){
            map.put(n.ncode,n);
        }
        return map;
    }

    public void addBookmark(List<NovelBookmark> bookmarks){
        begin();
        exec("delete from t_bookmark");
        for(NovelBookmark b : bookmarks){
            String d = new java.sql.Timestamp(b.getUpdate().getTime().getTime()).toString();
            String sql;
            //ブックマークデータの追加
            sql = String.format("replace into t_bookmark values('%s','%s','%d')",STR(b.getCode()),d,b.getCategory());
            exec(sql);
        }
        commit();
    }
    public void clearBookmark(){
        exec("delete from t_bookmark");
    }
    public List<NovelBookmark> getBookmark(){
        String sql;
        sql = "select * from t_bookmark order by b_update desc";
        Cursor r = query(sql);

        List<NovelBookmark> list = new ArrayList<NovelBookmark>();
        while(r.moveToNext()){
            String d = r.getString(1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(java.sql.Timestamp.valueOf(r.getString(1)));
            NovelBookmark b = new NovelBookmark(r.getString(0),r.getInt(2),cal);
            list.add(b);
        }
        r.close();
        return list;
    }

    public Map<String,NovelBookmark> getBookmarkMap(){
        String sql;
        sql = "select * from t_bookmark";
        Cursor r = query(sql);

        Map<String,NovelBookmark> map = new HashMap<String,NovelBookmark>();
        while(r.moveToNext()){
            String d = r.getString(1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(java.sql.Timestamp.valueOf(r.getString(1)));
            NovelBookmark b = new NovelBookmark(r.getString(0),r.getInt(2),cal);
            map.put(b.getCode(),b);
        }
        r.close();
        return map;
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
    public List<NovelInfo> getHistorys(){
        return this.queryClass("select * from t_novel_info natural join t_novel_history order by his_date desc",NovelInfo.class);
    }

    public List<NovelSubTitle> getSubTitles(String ncode) {
        //メインに登録されているデータを抽出
        String sql = String.format("select n_code,sub_no,sub_title,sub_regdate,sub_update,read_date,content_update,content_original_size,length(content_body) from t_novel_sub natural left join t_novel_read natural left join t_novel_content where n_code='%s' order by sub_no",STR(ncode));
        Cursor c = query(sql);
        List<NovelSubTitle> list = new ArrayList<NovelSubTitle>();
        for(int i=1;c.moveToNext();i++){
            NovelSubTitle n = new NovelSubTitle();
            n.index = i;
            n.title = c.getString(2);
            n.date = java.sql.Timestamp.valueOf(c.getString(3));
            if(c.getString(4) != null)
                n.update = java.sql.Timestamp.valueOf(c.getString(4));
            if(c.getString(5) != null)
                n.readDate = java.sql.Timestamp.valueOf(c.getString(5));
            if(c.getString(6) != null)
                n.contentDate = java.sql.Timestamp.valueOf(c.getString(6));
            if(c.getString(7) != null)
                n.originalSize = c.getInt(7);
            if(c.getString(8) != null)
                n.compressionSize = c.getInt(8);
            list.add(n);
        }
        c.close();
        return list;
    }

    public void setNovelReaded(String ncode, int index){
        String sql = String.format("replace into t_novel_read values('%s',%d,'%s')",
                STR(ncode),index,new java.sql.Timestamp(new Date().getTime()));
        exec(sql);
    }
    public NovelContent getNovelContent(String ncode, int index) {
        String sql = String.format("select sub_title,sub_regdate,sub_update,content_body,content_preface,content_trailer,content_tag from t_novel_content natural join t_novel_sub where n_code='%s' and sub_no=%d",STR(ncode),index);
        Cursor c = query(sql);
        NovelContent content = null;
        if(c.moveToNext()){
            content = new NovelContent();
            content.ncode = ncode;
            content.index = index;
            content.title = c.getString(0);
            content.regdate = java.sql.Timestamp.valueOf(c.getString(1));
            if(c.getString(2) != null)
                content.update = java.sql.Timestamp.valueOf(c.getString(2));
            content.body = decodeValue(c.getBlob(3));
            content.preface = decodeValue(c.getBlob(4));
            content.trailer = decodeValue(c.getBlob(5));
            content.tag = decodeValue(c.getBlob(6));

        }
        c.close();

        return content;

    }

    public void addRanking(int kind1, int kind2, int kind3, List<NovelRanking> rankList) {
        int index = 1;
        begin();
        String sql = String.format("replace into t_novel_ranking values(%d,%d,%d,datetime('now'));",kind1,kind2,kind3);
        exec(sql);
        for(NovelRanking rank : rankList){
            NovelRankingValue v = new NovelRankingValue(kind1,kind2,kind3,index++,rank);
            sql = createSqlReplaceClass(v,"t_novel_ranking_info");
            exec(sql);
        }
        commit();
    }
    public List<NovelRanking> getRanking(int kind1, int kind2, int kind3){
        String sql = String.format("select * from t_novel_ranking_info where ranking_kind1=%d and ranking_kind2=%d and ranking_kind3=%d order by ranking_index",
                kind1,kind2,kind3);
        return queryClass(sql,NovelRanking.class);
    }
    public boolean isRankingReload(int kind1, int kind2, int kind3,int time){
        String sql = String.format(
                "select 1 from t_novel_ranking where ranking_kind1=%d and ranking_kind2=%d and "+
                "ranking_kind3=%d and strftime('%%s', 'now') - strftime('%%s', ranking_date) < %d",
                kind1,kind2,kind3,time);
        Cursor c = query(sql);
        boolean ret = !c.moveToNext();
        c.close();
        return ret;
    }

    public List<NovelIndex> getContentNull(List<String> listNcode) {
        StringBuilder sb = new StringBuilder();
        for(String s : listNcode){
            if(sb.length() > 0)
                sb.append(",");
            sb.append(String.format("'%s'",s.toUpperCase()));
        }
        String sql = String.format("select n_code,sub_no from t_novel_sub natural left join t_novel_content where n_code in (%s) and content_body isnull",sb.toString());

        List<NovelIndex> list = new ArrayList<>();
        Cursor c = query(sql);
        while(c.moveToNext()){
            NovelIndex novelIndex = new NovelIndex();
            novelIndex.ncode = c.getString(0);
            novelIndex.index = c.getInt(1);
            list.add(novelIndex);
        }
        c.close();
        return list;
    }

    public void addSeries(NovelSeries seriesInfo) {
        ContentValues values = new ContentValues();
        values.put("s_code", seriesInfo.scode);
        values.put("series_title", seriesInfo.title);
        values.put("series_info", seriesInfo.info);
        values.put("series_writer", seriesInfo.writer);
        replace("t_novel_series",values);

        //シリーズ対応関係の削除
        exec(String.format("delete from t_novel_series_bind where s_code='%s'",STR(seriesInfo.scode)));
        //シリーズ対応関係の再設定
        for(String ncode : seriesInfo.novelList){
            values = new ContentValues();
            values.put("s_code", seriesInfo.scode);
            values.put("n_code", ncode);
            insert("t_novel_series_bind",values);
        }

    }
}
