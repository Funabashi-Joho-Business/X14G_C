package to.pns.naroencounter.Other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import to.pns.naroencounter.data.NovelBody;
import to.pns.naroencounter.data.NovelBookmark;
import to.pns.naroencounter.data.NovelContent;
import to.pns.naroencounter.data.NovelInfo;
import to.pns.naroencounter.data.NovelRanking;
import to.pns.naroencounter.data.NovelSeries;
import to.pns.naroencounter.data.NovelSubTitle;
import to.pns.lib.AppDB;




public class NovelDB extends AppDB {
    public static class NovelInfoBookmark extends NovelInfo{
        public int b_category;
        public int b_mark;
    }

    public static class NovelInfoRanking extends NovelInfoBookmark{
        public int ranking_index;
        public int point;
    }
    public static class NovelIndex{
        public String ncode;
        public int index;
    }

    static class NovelRankingValue extends NovelRanking{
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

    public NovelDB(Context context) {
        super(context, "novel02.db", 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);

        String sql;
        //ブックマーク用テーブルの作成
        sql = "create table t_bookmark(ncode text primary key,b_update date,b_category int,b_mark int)";
        db.execSQL(sql);
        //履歴用
        sql = "create table t_novel_history(ncode text primary key,his_date date)";
        db.execSQL(sql);
        sql = createSqlCreateClass(NovelInfo.class,"t_novel_info","ncode");
        db.execSQL(sql);

        sql = "create table t_novel_sub(ncode text,sub_no int,sub_title text,sub_regdate date,sub_update date,primary key(ncode,sub_no))";
        db.execSQL(sql);

        sql = "create table t_novel_content(ncode text,sub_no int,content_update date,content_original_size int,content_body blob,content_preface blog,content_trailer blob,content_tag blob,primary key(ncode,sub_no))";
        db.execSQL(sql);

        sql = "create table t_novel_read(ncode text,sub_no int,read_date date,primary key(ncode,sub_no))";
        db.execSQL(sql);

        sql = "create table t_novel_ranking(ranking_kind1 int,ranking_kind2,ranking_kind3,ranking_date date,primary key(ranking_kind1,ranking_kind2,ranking_kind3))";
        db.execSQL(sql);
        sql = createSqlCreateClass(NovelRankingValue.class,"t_novel_ranking_info","ranking_kind1","ranking_kind2","ranking_kind3","ranking_index");
        db.execSQL(sql);

        sql = "create table t_novel_series(scode text primary key,series_title text,series_info text,series_writer int)";
        db.execSQL(sql);
        sql = "create table t_novel_series_bind(scode text,ncode text,primary key(scode,ncode))";
        db.execSQL(sql);

        sql = "create table t_novel_search(ncode text primary key,search_order int)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        String sql;
        if(i < 3) {
        }


    }


    public void addSubTitle(String ncode,List<NovelSubTitle> list){
        begin();

        String sql;
        sql = String.format("delete from t_novel_sub where ncode='%s'",STR(ncode));
        exec(sql);

        int index = 1;
        for(NovelSubTitle sub : list){
            ContentValues values = new ContentValues();
            values.put("ncode", ncode);
            values.put("sub_no", index++);
            values.put("sub_title", sub.title);
            values.put("sub_regdate", new java.sql.Timestamp(sub.date.getTime()).toString());
            if(sub.update != null)
                values.put("sub_update", new java.sql.Timestamp(sub.update.getTime()).toString());
            insert("t_novel_sub",values);
        }
        commit();
    }

    public void addNovelContents(String ncode, int index, NovelBody novelBody){
        ContentValues values = new ContentValues();

        values.put("ncode", ncode);
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

    public void alldelNovelHistory(){
        String sql = String.format("delete from t_novel_history");
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
            replaceClass(info,"t_novel_info");
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
        String sql = String.format("select * from t_novel_search natural join t_novel_info order by search_order");
        List<NovelInfo> list = queryClass(sql,NovelInfo.class);
        return list;
    }
    public NovelInfoBookmark getNovelInfo(String ncode){
        String sql = String.format("select * from t_novel_info natural left join t_bookmark where ncode LIKE '%s'",STR(ncode));
        List<NovelInfoBookmark> list = queryClass(sql,NovelInfoBookmark.class);
        if(list.size() == 0)
            return null;
        return list.get(0);

    }
    public List<NovelInfoBookmark> getNovelInfoFromBookmark(){
        String sql = String.format("select * from t_novel_info natural left join t_bookmark where ncode in (select ncode from t_bookmark) order by novelupdated_at desc");
        List<NovelInfoBookmark> list = queryClass(sql,NovelInfoBookmark.class);
        return list;
    }

    public List<NovelInfoRanking> getNovelInfoFromRanking(int kind1, int kind2, int kind3){
        String sql = String.format("select * from t_novel_info natural join (select ncode,ranking_index from t_novel_ranking_info where ranking_kind1=%d and ranking_kind2=%d and ranking_kind3=%d) a order by ranking_index",
                kind1,kind2,kind3);
        return queryClass(sql,NovelInfoRanking.class);
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
        String sql = String.format("select * from t_novel_info natural left join t_bookmark where ncode in (%s)",sb.toString());
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
            sql = String.format("replace into t_bookmark values('%s','%s','%d','%d')",STR(b.getCode()),d,b.getCategory(),b.getMark());
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
            NovelBookmark b = new NovelBookmark(r.getString(0),r.getInt(2),cal,r.getInt(3));
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
            NovelBookmark b = new NovelBookmark(r.getString(0),r.getInt(2),cal,r.getInt(3));
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
    public List<NovelInfoBookmark> getHistorys(){
        return this.queryClass("select * from t_novel_info natural join t_novel_history natural left join t_bookmark order by his_date desc",NovelInfoBookmark.class);
    }

    public List<NovelSubTitle> getSubTitles(String ncode) {
        //メインに登録されているデータを抽出
        String sql = String.format("select ncode,sub_no,sub_title,sub_regdate,sub_update,read_date,content_update,content_original_size,length(content_body) from t_novel_sub natural left join t_novel_read natural left join t_novel_content where ncode='%s' order by sub_no",STR(ncode));
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
        String sql = String.format("select sub_title,sub_regdate,sub_update,content_body,content_preface,content_trailer,content_tag from t_novel_content natural join t_novel_sub where ncode='%s' and sub_no=%d",STR(ncode),index);
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
            replaceClass(v,"t_novel_ranking_info");
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
        String sql = String.format("select ncode,sub_no from t_novel_sub natural left join t_novel_content where ncode in (%s) and content_body isnull",sb.toString());

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
        values.put("scode", seriesInfo.scode);
        values.put("series_title", seriesInfo.title);
        values.put("series_info", seriesInfo.info);
        values.put("series_writer", seriesInfo.writer);
        replace("t_novel_series",values);

        //シリーズ対応関係の削除
        exec(String.format("delete from t_novel_series_bind where scode='%s'",STR(seriesInfo.scode)));
        //シリーズ対応関係の再設定
        for(String ncode : seriesInfo.novelList){
            values = new ContentValues();
            values.put("scode", seriesInfo.scode);
            values.put("ncode", ncode);
            insert("t_novel_series_bind",values);
        }
    }
    public NovelSeries getSeriesInfo(String ncode){
        String sql = String.format("select * from t_novel_series where scode in (select scode from t_novel_series_bind where ncode='%s')",STR(ncode));
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
    public List<String> getSeriesNcode(String scode){
        String sql = String.format("select ncode from t_novel_series_bind where scode='%s'",STR(scode));
        Cursor c = query(sql);

        List<String> list = new ArrayList<String>();
        while(c.moveToNext()){
            list.add(c.getString(0));
        }
        c.close();
        return list;
    }
    public void optimisation(){
        String sql;
        sql = "delete from t_novel_sub where ncode not in(select ncode from t_bookmark union select ncode from t_novel_history)";
        exec(sql);
        sql = "delete from t_novel_content where ncode not in(select ncode from t_bookmark union select ncode from t_novel_history)";
        exec(sql);
        sql = "delete from t_novel_read where ncode not in(select ncode from t_bookmark union select ncode from t_novel_history)";
        exec(sql);
        sql = "delete from t_novel_info where ncode not in(\n" +
                "                select ncode from t_bookmark union\n" +
                "                select ncode from t_novel_history union\n" +
                "                select ncode from t_novel_ranking_info union\n" +
                "                select ncode from t_novel_search)";
        exec(sql);
        vacuum();
    }
}
