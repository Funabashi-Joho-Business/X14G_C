package jp.ac.chiba_fjb.x14b_c.naroreader.Other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBody;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSubTitle;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;
import to.pns.lib.LogService;

/*
色々な通信系の処理をこのクラスで実装すること
ここで実装する理由は、タイマーによる定期的な処理も実装しやすいから
 */

public class NaroReceiver extends BroadcastReceiver {
    public static final String ACTION_BOOKMARK = "ACTION_BOOKMARK"; //このクラスが処理すべき命令
    public static final String ACTION_NOVELINFO = "ACTION_NOVELINFO"; //ノベル情報の取得
    public static final String ACTION_NOVELSUB = "ACTION_NOVELSUB"; //ノベルサブタイトルの取得
    public static final String ACTION_NOVELCONTENT = "ACTION_NOVELCONTENT"; //ノベルサブタイトルの取得
    public static final String NOTIFI_BOOKMARK = "NOTIFI_BOOKMARK"; //処理終了後の通知
    public static final String NOTIFI_NOVELINFO = "NOTIFI_NOVELINFO"; //検索終了後の通知
    public static final String NOTIFI_NOVELSUB = "NOTIFI_NOVELSUB"; //サブタイトル取得終了後の通知
    public static final String NOTIFI_NOVELCONTENT = "NOTIFI_NOVELCONTENT"; //本文取得終了後の通知
    public NaroReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        //処理要求の確認
        switch(intent.getAction()){
            case ACTION_BOOKMARK:
                new Thread(){
                    @Override
                    public void run() {

                        NovelDB settingDB = new NovelDB(context);
                        String id = settingDB.getSetting("loginId","");
                        String pass = settingDB.getSetting("loginPass","");
                        settingDB.close();

                        //ログイン処理
                        String hash = TbnReader.getLoginHash(id,pass);

                        if(hash == null) {
                            context.sendBroadcast(new Intent().setAction(NOTIFI_BOOKMARK).putExtra("result",false));
                            LogService.output(context,"ログイン失敗");
                            return;
                        }
                        LogService.output(context,"ブックマーク情報の読み込み開始");
                        //取得したブックマーク情報をDBに保存
                        List<NovelBookmark> bookmarks = TbnReader.getBookmark(hash);
                        //DBを利用
                        NovelDB db = new NovelDB(context);
                        for(NovelBookmark b : bookmarks){
                            db.addBookmark(b.getCode(),b.getName(),b.getUpdate().getTime(),b.getCategory());
                        }
                        db.close();
                        LogService.output(context,"ブックマーク情報の読み込み完了");
                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_BOOKMARK).putExtra("result",true));
                    }
                }.start();
                break;


            case ACTION_NOVELINFO:
                new Thread(){
                    @Override
                    public void run() {
                        LogService.output(context,"ノベル情報の取得");
                        NovelDB db = new NovelDB(context);
                        List<String>list = db.getNovel();
                        db.close();


                        //取得した検索情報をDBに保存
                        List<NovelInfo> info = TbnReader.getNovelInfo(list);
                        if(info != null) {
                            //DBを利用
                            db = new NovelDB(context);
                            db.addNovelInfo(info);
                            db.close();
                            LogService.output(context, "ノベル情報の取得完了");
                        }
                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_NOVELINFO).putExtra("result",true));
                    }
                }.start();
                break;
            case ACTION_NOVELSUB:
                new Thread(){
                    @Override
                    public void run() {
                        String ncode = intent.getStringExtra("ncode");
                        if(ncode == null)
                            return;

                        LogService.output(context,"ノベルサブタイトルの取得");


                        //取得した検索情報をDBに保存
                        List<NovelSubTitle> titles = TbnReader.getSubTitle(ncode);
                        if(titles != null) {
                            //DBを利用
                            NovelDB db = new NovelDB(context);
                            db.addSubTitle(ncode,titles);
                            db.close();
                            LogService.output(context, "ノベルサブタイトルの取得完了");
                        }
                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_NOVELSUB).putExtra("result",true));
                    }
                }.start();
                break;
            case ACTION_NOVELCONTENT:
                new Thread(){
                    @Override
                    public void run() {
                        String ncode = intent.getStringExtra("ncode");
                        int index = intent.getIntExtra("index",0);
                        if(ncode == null)
                            return;

                        LogService.format(context,"%s(%d)の本分の取得",ncode,index);
                        NovelBody body = TbnReader.getNovelBody(ncode,index);
                        if(body != null){
                            NovelDB db = new NovelDB(context);
                            db.addNovelContents(ncode,index,body.body,body.tag);
                            db.close();
                        }
                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_NOVELCONTENT).putExtra("result",true));
                    }
                }.start();
                break;
        }





    }
}
