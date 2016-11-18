package jp.ac.chiba_fjb.x14b_c.naroreader.Other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;


import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;
import to.pns.lib.LogService;

/*
色々な通信系の処理をこのクラスで実装すること
ここで実装する理由は、タイマーによる定期的な処理も実装しやすいから
 */

public class NaroReceiver extends BroadcastReceiver {
    public static final String ACTION_BOOKMARK = "ACTION_BOOKMARK"; //このクラスが処理すべき命令
    public static final String ACTION_SEARCH = "ACTION_SEARCH"; //検索用クラスの命令を書く
    public static final String NOTIFI_BOOKMARK = "NOTIFI_BOOKMARK"; //処理終了後の通知
    public static final String NOTIFI_SEARCH = "NOTIFI_SEARCH"; //検索終了後の通知
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

            case ACTION_SEARCH:
                new Thread(){
                    @Override
                    public void run() {

                        //まだブックマーク情報が混在中・・・

                        String word = intent.getStringExtra("data");
                        LogService.output(context,"検索開始");
                        //取得した検索情報をDBに保存
                        NovelInfo[] Snovels = TbnReader.getKeyword(word);
                        //DBを利用
                        NovelDB db = new NovelDB(context);
                        for(NovelInfo b : Snovels){
                            db.addSearch(b.ncode,b.title,b.general_lastup,b.biggenre);
                        }
                        db.close();
                        LogService.output(context,"検索終了");
                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_SEARCH).putExtra("result",true));
                    }
                }.start();
                break;
        }





    }
}
