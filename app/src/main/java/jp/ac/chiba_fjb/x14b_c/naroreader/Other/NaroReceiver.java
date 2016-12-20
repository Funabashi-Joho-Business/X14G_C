package jp.ac.chiba_fjb.x14b_c.naroreader.Other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jp.ac.chiba_fjb.x14b_c.naroreader.MainActivity;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBody;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelRanking;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSubTitle;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;
import to.pns.lib.LogService;
import to.pns.lib.Notify;

/*
色々な通信系の処理をこのクラスで実装すること
ここで実装する理由は、タイマーによる定期的な処理も実装しやすいから
 */

public class NaroReceiver extends BroadcastReceiver {
    public static final String ACTION_BOOKMARK = "ACTION_BOOKMARK"; //このクラスが処理すべき命令
    public static final String NOTIFI_BOOKMARK = "NOTIFI_BOOKMARK"; //処理終了後の通知

    public static final String ACTION_NOVELINFO = "ACTION_NOVELINFO"; //ノベル情報の取得
    public static final String NOTIFI_NOVELINFO = "NOTIFI_NOVELINFO"; //ノベル情報取得終了後の通知

    public static final String ACTION_NOVELSUB = "ACTION_NOVELSUB"; //ノベルサブタイトルの取得
    public static final String NOTIFI_NOVELSUB = "NOTIFI_NOVELSUB"; //サブタイトル取得終了後の通知

    public static final String ACTION_NOVELCONTENT = "ACTION_NOVELCONTENT"; //本文の取得
    public static final String NOTIFI_NOVELCONTENT = "NOTIFI_NOVELCONTENT"; //本文取得終了後の通知
    public static final String ACTION_NOVELCONTENT_STOP = "ACTION_NOVELCONTENT_STOP"; //本文の取得停止

    public static final String ACTION_RANKING = "ACTION_RANKING"; //ランキングの取得
    public static final String NOTIFI_RANKING = "NOTIFI_RANKING"; //ランキング取得終了後の通知

    private static boolean mDownload;

    public NaroReceiver() {
    }


    public static void updateBookmark(Context con){
        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_BOOKMARK));
    }
    public static void updateNovelInfo(Context con){
        NovelDB db = new NovelDB(con);
        List<NovelBookmark> boolmarks = db.getBookmark();
        List<NovelInfo> novelInfos = db.getHistorys();
        db.close();
        ArrayList<String> list = new ArrayList<String>();
        for(NovelBookmark b : boolmarks)
            list.add(b.getCode());
        for(NovelInfo t : novelInfos)
            list.add(t.ncode);

        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_NOVELINFO).putExtra("ncode",list));
    }
    public static void updateNovelInfo(Context con,String ncode){
        ArrayList<String> list = new ArrayList<String>();
        list.add(ncode);
        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_NOVELINFO).putExtra("ncode",list));
    }
    public static void updateNovelInfoHistory(Context con){
        NovelDB db = new NovelDB(con);
        List<NovelInfo> novelInfos = db.getHistorys();
        db.close();
        ArrayList<String> list = new ArrayList<String>();
        for(NovelInfo t : novelInfos)
            list.add(t.ncode);

        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_NOVELINFO).putExtra("ncode",list));
    }
    public static void updateNovelInfoBookmark(Context con){
        NovelDB db = new NovelDB(con);
        List<NovelBookmark> boolmarks = db.getBookmark();
        db.close();
        ArrayList<String> list = new ArrayList<String>();
        for(NovelBookmark b : boolmarks)
            list.add(b.getCode());
        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_NOVELINFO).putExtra("ncode",list));
    }
    public static void download(Context con, ArrayList<String> list) {
        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_NOVELCONTENT).putExtra("ncodes",list));

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
                        db.clearBookmark();
                        for(NovelBookmark b : bookmarks){
                            db.addBookmark(b.getCode(),b.getUpdate().getTime(),b.getCategory());
                        }
                        db.close();
                        LogService.output(context,"ブックマーク情報の読み込み完了");
                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_BOOKMARK).putExtra("result",true));
                        //作品情報を取得
                        updateNovelInfoBookmark(context);
                    }
                }.start();
                break;

            case ACTION_NOVELINFO:
                new Thread(){
                    @Override
                    public void run() {
                        LogService.output(context,"ノベル情報の取得");
                        List<String> ncodes = (List<String>)intent.getSerializableExtra("ncode");

                        //取得した検索情報をDBに保存
                        List<NovelInfo> info = TbnReader.getNovelInfo(ncodes);
                        if(info != null) {
                            //DBを利用
                            NovelDB db = new NovelDB(context);
                            db.addNovelInfo(info);
                            db.close();
                            LogService.output(context, "ノベル情報の取得完了");
                            //更新完了通知
                            context.sendBroadcast(new Intent().setAction(NOTIFI_NOVELINFO).putExtra("result",true));
                        }else
                            //更新完了通知
                            context.sendBroadcast(new Intent().setAction(NOTIFI_NOVELINFO).putExtra("result",false));

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
                        recvSubtitle(context,ncode);
                        LogService.output(context,"ノベルサブタイトルの取得");



                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_NOVELSUB).putExtra("result",true));
                    }
                }.start();
                break;
            case ACTION_NOVELCONTENT_STOP:
                mDownload = false;
                break;
            case ACTION_NOVELCONTENT:
                Intent intent1 =  new Intent(context,NaroReceiver.class).setAction(ACTION_NOVELCONTENT_STOP);
                //ステータスバー表示用
                final Notify notify = new Notify(context,intent1,R.layout.status_layout,R.mipmap.ic_launcher);
                notify.setRemoteText(R.id.textTitle,context.getString(R.string.app_name));
                notify.setRemoteImage(R.id.imageNotify, R.mipmap.ic_launcher, 0);
                notify.setIcon(R.mipmap.ic_launcher,0);

                notify.output("受信開始",false);

                mDownload = true;
                new Thread(){
                    @Override
                    public void run() {
                        List<String> ncodes = (List<String>) intent.getSerializableExtra("ncodes");
                        if(ncodes != null){
                            //複数のデータを取得

                            notify.setRemoteText(R.id.textMsg,"サブタイトル確認中");
                            //サブタイトルの受信
                            for(String ncode : ncodes){
                                recvSubtitle(context,ncode);
                            }

                            NovelDB db = new NovelDB(context);
                            List<NovelIndex> list = db.getContentNull(ncodes);
                            db.close();

                            int count=0;
                            for(NovelIndex novelIndex : list){
                                notify.setRemoteText(R.id.textMsg,String.format("受信 %d/%d",count++,list.size()));
                                notify.update();
                                if(!mDownload)
                                    break;
                                recvContent(context,novelIndex.ncode,novelIndex.index);
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(mDownload) {
                                notify.setRemoteText(R.id.textMsg, String.format("受信完了"));
                                notify.output("受信完了", true);
                            }else{
                                notify.setRemoteText(R.id.textMsg, String.format("受信中断"));
                                notify.output("受信中断", true);
                            }

                        }
                        else{
                            //単発処理
                            String ncode = intent.getStringExtra("ncode");
                            int index = intent.getIntExtra("index",0);
                            if(ncode == null)
                                return;
                            recvContent(context,ncode,index);

                        }


                    }
                }.start();
                break;

            case ACTION_RANKING:
                new Thread(){
                    @Override
                    public void run() {
                        int kind1 = intent.getIntExtra("kind1",1);
                        int kind2 = intent.getIntExtra("kind2",1);
                        int kind3 = intent.getIntExtra("kind3",1);

                        final List<NovelRanking> rankList = TbnReader.getRanking(kind1,kind2,kind3);

                        Intent intent = new Intent().setAction(NOTIFI_RANKING);
                        if(rankList != null){
                            NovelDB db = new NovelDB(context);
                            db.addRanking(kind1,kind2,kind3,rankList);
                            db.close();
                            LogService.format(context,"ランキングの取得");
                            context.sendBroadcast(intent.putExtra("result",true));
                        }
                        else {
                            LogService.format(context,"ランキングの失敗");
                            context.sendBroadcast(intent.putExtra("result", false));
                        }
                    }
                }.start();
                break;
        }





    }
    boolean recvSubtitle(Context context,String ncode){
        //取得した検索情報をDBに保存
        List<NovelSubTitle> titles = TbnReader.getSubTitle(ncode);
        if(titles == null)
            return false;

        //DBを利用
        NovelDB db = new NovelDB(context);
        db.addSubTitle(ncode,titles);
        db.close();
        LogService.output(context, ncode+"のサブタイトルの取得完了");
        return true;
    }
    void recvContent(Context context,String ncode,int index){
        LogService.format(context,"%s(%d)の本文の取得",ncode,index);
        NovelBody body = TbnReader.getNovelBody(ncode,index);
        Intent intent = new Intent().setAction(NOTIFI_NOVELCONTENT).putExtra("index",index);
        if(body != null){
            if(index == 0)
                index = 1;
            NovelDB db = new NovelDB(context);
            db.addNovelContents(ncode,index,body);
            db.close();

            context.sendBroadcast(intent.putExtra("result",true));
        }
        else
            context.sendBroadcast(intent.putExtra("result",false));
    }

}
