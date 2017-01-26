package to.pns.naroencounter.Other;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import to.pns.naroencounter.MainActivity;
import to.pns.naroencounter.R;
import to.pns.naroencounter.data.NovelBody;
import to.pns.naroencounter.data.NovelBookmark;
import to.pns.naroencounter.data.NovelInfo;
import to.pns.naroencounter.data.NovelRanking;
import to.pns.naroencounter.data.NovelSeries;
import to.pns.naroencounter.data.NovelSubTitle;
import to.pns.naroencounter.data.TbnReader;
import to.pns.lib.LogService;
import to.pns.lib.Notify;

/*
色々な通信系の処理をこのクラスで実装すること
ここで実装する理由は、タイマーによる定期的な処理も実装しやすいから
 */

public class NaroReceiver extends BroadcastReceiver {
    public static final String ACTION_BOOKMARK = "ACTION_BOOKMARK"; //このクラスが処理すべき命令
    public static final String NOTIFI_BOOKMARK = "NOTIFI_BOOKMARK"; //処理終了後の通知
    public static final String ACTION_DEL_BOOKMARK = "ACTION_DEL_BOOKMARK"; //ブックマーク削除

    public static final String ACTION_SET_BOOKMARK2 = "ACTION_SET_BOOKMARK2"; //しおり設定
    public static final String NOTIFI_SET_BOOKMARK2 = "NOTIFI_SET_BOOKMARK2"; //しおり処理終了後の通知

    public static final String ACTION_CLEAR_BOOKMARK2 = "ACTION_CLEAR_BOOKMARK2"; //しおり設定
    public static final String NOTIFI_CLEAR_BOOKMARK2 = "NOTIFI_CLEAR_BOOKMARK2"; //しおり処理終了後の通知

    public static final String ACTION_NOVELINFO = "ACTION_NOVELINFO"; //ノベル情報の取得
    public static final String NOTIFI_NOVELINFO = "NOTIFI_NOVELINFO"; //ノベル情報取得終了後の通知

    public static final String ACTION_NOVELSUB = "ACTION_NOVELSUB"; //ノベルサブタイトルの取得
    public static final String NOTIFI_NOVELSUB = "NOTIFI_NOVELSUB"; //サブタイトル取得終了後の通知

    public static final String ACTION_NOVELCONTENT = "ACTION_NOVELCONTENT"; //本文の取得
    public static final String NOTIFI_NOVELCONTENT = "NOTIFI_NOVELCONTENT"; //本文取得終了後の通知
    public static final String ACTION_NOVELCONTENT_STOP = "ACTION_NOVELCONTENT_STOP"; //本文の取得停止

    public static final String ACTION_RANKING = "ACTION_RANKING"; //ランキングの取得
    public static final String NOTIFI_RANKING = "NOTIFI_RANKING"; //ランキング取得終了後の通知

    public static final String ACTION_UPDATE_CHECK = "ACTION_UPDATE_CHECK"; //ランキングの取得
    public static final String NOTIFI_UPDATE_CHECK = "NOTIFI_UPDATE_CHECK"; //ランキング取得終了後の通知

    public static final String ACTION_UPDATE_SETTING = "ACTION_UPDATE_SETTING"; //ランキングの取得

    public static final String ACTION_SERIES = "ACTION_SERIES"; //シリーズ情報の取得
    public static final String NOTIFI_SERIES = "NOTIFI_SERIES"; //シリーズ取得終了後の通知

    public static final String ACTION_SEARCH = "ACTION_SEARCH"; //検索の開始
    public static final String NOTIFI_SEARCH = "NOTIFI_SEARCH"; //検索終了の通知

    private static boolean mDownload;

    public NaroReceiver() {
    }


    public static void updateBookmark(Context con){
        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_BOOKMARK));
    }
    public static void updateNovelInfo(Context con){
        NovelDB db = new NovelDB(con);
        List<NovelBookmark> boolmarks = db.getBookmark();
        List<NovelDB.NovelInfoBookmark> novelInfos = db.getHistorys();
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
        List<NovelDB.NovelInfoBookmark> novelInfos = db.getHistorys();
        db.close();
        ArrayList<String> list = new ArrayList<String>();
        for(NovelInfo t : novelInfos)
            list.add(t.ncode);

        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_NOVELINFO).putExtra("ncode",list));
    }

    public static void download(Context con, ArrayList<String> list) {
        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_NOVELCONTENT).putExtra("ncodes",list));
    }
    public static void search(Context con,String params,int writer) {
        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_SEARCH).putExtra("params",params).putExtra("writer",writer));
    }
    public static void setBookmark2(Context con,String ncode,int index){
        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_SET_BOOKMARK2).putExtra("ncode",ncode).putExtra("index",index));
    }
    public static void clearBookmark2(Context con,String ncode){
        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_CLEAR_BOOKMARK2).putExtra("ncode",ncode));
    }
    public static void delBookmark(Context con, ArrayList<String> list) {
        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_DEL_BOOKMARK).putExtra("ncodes",list));
    }
    @Override
    public void onReceive(final Context context, final Intent intent) {
        //処理要求の確認
        switch(intent.getAction()){
            case Intent.ACTION_BOOT_COMPLETED:
                LogService.output(context,"端末起動を確認");
            case ACTION_UPDATE_SETTING:
                //アップデートチェックを起動
                startUpdateCheck(context);
                break;
            case ACTION_UPDATE_CHECK:
                final Handler hanlder = new Handler();
                new Thread(){
                    @Override
                    public void run() {
                        LogService.output(context,"アップデートチェックを開始");

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
                        final List<NovelBookmark> bookmarks = TbnReader.getBookmark(hash);

                        //DBに保存
                        NovelDB db = new NovelDB(context);
                        Map<String, NovelBookmark> map = db.getBookmarkMap();
                        db.addBookmark(bookmarks);
                        db.close();
                        //更新チェック
                        for(final NovelBookmark b : bookmarks){
                            NovelBookmark old = map.get(b.getCode());
                            if(old == null || b.getUpdate().getTimeInMillis() >  old.getUpdate().getTimeInMillis() ){
                                //更新通知
                                hanlder.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        NovelDB db = new NovelDB(context);
                                        NovelInfo info = db.getNovelInfo(b.getCode());
                                        db.close();

                                        PendingIntent pending = PendingIntent.getActivity(context,0,
                                                new Intent(context,MainActivity.class),0);
                                        final Notify notify = new Notify(context,234,pending,R.layout.status_layout,R.mipmap.ic_launcher);
                                        notify.setRemoteImage(R.id.imageNotify, R.mipmap.ic_launcher, 0);
                                        notify.setRemoteText(R.id.textTitle,context.getString(R.string.app_name));
                                        notify.setIcon(R.mipmap.ic_launcher,0);

                                        String dateString = new SimpleDateFormat("yyyy年MM月dd日(E)").format(b.getUpdate().getTime());
                                        String msg;
                                        if(info != null)
                                            msg = String.format("「%s」が%sに更新",info.title,dateString);
                                        else
                                            msg = String.format("「%s」が%sに更新",b.getCode(),dateString);
                                        notify.setRemoteText(R.id.textMsg,msg);
                                        notify.update(true);
                                    }
                                });
                            }
                        }
                        LogService.output(context,"アップデートチェックを完了");
                        //ノベル情報の更新
                        updateNovelInfoBookmark(context);
                    }
                }.start();
                //次回タイマー起動
                startUpdateCheck(context);
                break;
            case ACTION_SERIES:
                new Thread(){
                    @Override
                    public void run() {
                        String ncode = intent.getStringExtra("ncode");
                        if(ncode == null)
                            return;
                        LogService.output(context,"シリーズ情報の読み込み開始");
                        //取得したブックマーク情報をDBに保存
                        String scode = TbnReader.getSeries(ncode);
                        if(scode == null){
                            LogService.output(context,"シリーズ情報無し");
                            context.sendBroadcast(new Intent().setAction(NOTIFI_SERIES).putExtra("result",false));
                            return;
                        }
                        NovelSeries seriesInfo = TbnReader.getSeriesInfo(scode);
                        if(scode == null){
                            context.sendBroadcast(new Intent().setAction(NOTIFI_SERIES).putExtra("result",false));
                            return;
                        }

                        //DBを利用
                        NovelDB db = new NovelDB(context);
                        db.addSeries(seriesInfo);
                        db.close();
                        LogService.output(context,"シリーズ情報の読み込み完了");
                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_SERIES).putExtra("result",true));
                    }
                }.start();
                break;
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
                        db.addBookmark(bookmarks);
                        db.close();
                        LogService.output(context,"ブックマーク情報の読み込み完了");
                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_BOOKMARK).putExtra("result",true));
                        //作品情報を取得
                        updateNovelInfoBookmark(context);
                    }
                }.start();
                break;
            case ACTION_DEL_BOOKMARK:
                new Thread(){
                    @Override
                    public void run() {
                        List<String> ncodes = (List<String>) intent.getSerializableExtra("ncodes");
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

                        for(String ncode : ncodes){
                            if(TbnReader.clearBookmark(hash,ncode))
                                LogService.output(context,ncode+"ブックマーク削除");
                            else
                                LogService.output(context,ncode+"ブックマーク削除失敗");
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }


                        LogService.output(context,"ブックマーク情報の読み込み開始");
                        //取得したブックマーク情報をDBに保存
                        List<NovelBookmark> bookmarks = TbnReader.getBookmark(hash);
                        //DBを利用
                        NovelDB db = new NovelDB(context);
                        db.addBookmark(bookmarks);
                        db.close();
                        LogService.output(context,"ブックマーク情報の読み込み完了");
                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_BOOKMARK).putExtra("result",true));
                        //作品情報を取得
                        updateNovelInfoBookmark(context);
                    }
                }.start();
                break;
            case ACTION_SET_BOOKMARK2:
                new Thread(){
                    @Override
                    public void run() {
                        String ncode = intent.getStringExtra("ncode");
                        int index = intent.getIntExtra("index",0);

                        NovelDB settingDB = new NovelDB(context);
                        String id = settingDB.getSetting("loginId","");
                        String pass = settingDB.getSetting("loginPass","");
                        settingDB.close();

                        //ログイン処理
                        String hash = TbnReader.getLoginHash(id,pass);

                        if(hash == null) {
                            context.sendBroadcast(new Intent().setAction(NOTIFI_SET_BOOKMARK2).putExtra("result",false));
                            LogService.output(context,"ログイン失敗");
                            return;
                        }
                        LogService.output(context,"しおりの設定開始");
                        boolean flag = TbnReader.setBookmark2(hash,ncode,index);
                        if(flag)
                            LogService.output(context,"しおり設定完了");
                        else
                            LogService.output(context,"しおり設定エラー");
                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_SET_BOOKMARK2).putExtra("result",flag));
                        //ブックマーク更新処理
                        updateBookmark(context);
                    }
                }.start();
                break;
            case ACTION_CLEAR_BOOKMARK2:
                new Thread(){
                    @Override
                    public void run() {
                        String ncode = intent.getStringExtra("ncode");
                        int index = intent.getIntExtra("index",0);

                        NovelDB settingDB = new NovelDB(context);
                        String id = settingDB.getSetting("loginId","");
                        String pass = settingDB.getSetting("loginPass","");
                        settingDB.close();

                        //ログイン処理
                        String hash = TbnReader.getLoginHash(id,pass);

                        if(hash == null) {
                            context.sendBroadcast(new Intent().setAction(NOTIFI_CLEAR_BOOKMARK2).putExtra("result",false));
                            LogService.output(context,"ログイン失敗");
                            return;
                        }
                        LogService.output(context,"しおりの解除開始");
                        boolean flag = TbnReader.clearBookmark2(hash,ncode);
                        if(flag)
                            LogService.output(context,"しおり設定完了");
                        else
                            LogService.output(context,"しおり設定エラー");
                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_CLEAR_BOOKMARK2).putExtra("result",flag));
                        //ブックマーク更新処理
                        updateBookmark(context);
                    }
                }.start();
                break;
            case ACTION_NOVELINFO:
                new Thread(){
                    @Override
                    public void run() {
                        List<String> ncodes = (List<String>)intent.getSerializableExtra("ncode");
                        boolean flag = getNovelInfo(context,ncodes);
                        context.sendBroadcast(new Intent().setAction(NOTIFI_NOVELINFO).putExtra("result",flag));
                    }
                }.start();
                break;
            case ACTION_SEARCH:
                new Thread(){
                    @Override
                    public void run() {
                        LogService.output(context,"検索開始");
                        String params = intent.getStringExtra("params");
                        int writer = intent.getIntExtra("writer",0);
                        //取得した検索情報をDBに保存
                        List<NovelInfo> info = TbnReader.getNovelInfoFromParam(params);

                        //違う作者を除去
                        if(writer > 0) {
                            List<NovelInfo> info2 = new ArrayList<NovelInfo>();
                            for (NovelInfo i : info) {
                                if (i.userid == writer)
                                    info2.add(i);
                            }
                            info = info2;
                        }

                        if(info != null) {
                            //DBを利用
                            NovelDB db = new NovelDB(context);
                            db.setNovelSearch(info);
                            db.close();
                            LogService.output(context, "検索完了");
                            //更新完了通知
                            context.sendBroadcast(new Intent().setAction(NOTIFI_SEARCH).putExtra("result",true));
                        }else
                            //更新完了通知
                            context.sendBroadcast(new Intent().setAction(NOTIFI_SEARCH).putExtra("result",false));

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
                        LogService.output(context,"ノベルサブタイトルの取得開始");
                        recvSubtitle(context,ncode);

                        //更新完了通知
                        context.sendBroadcast(new Intent().setAction(NOTIFI_NOVELSUB).putExtra("result",true));
                    }
                }.start();
                break;
            case ACTION_NOVELCONTENT_STOP:
                mDownload = false;
                break;
            case ACTION_NOVELCONTENT:
                mDownload = true;
                new Thread(){
                    @Override
                    public void run() {
                        List<String> ncodes = (List<String>) intent.getSerializableExtra("ncodes");
                        if(ncodes != null){
                            //複数のデータを取得
                            //ステータスバー表示用
                            PendingIntent pending = PendingIntent.getBroadcast(context,0,
                                    new Intent(context,NaroReceiver.class).setAction(ACTION_NOVELCONTENT_STOP),0);
                            final Notify notify = new Notify(context,123,pending,R.layout.status_layout,R.mipmap.ic_launcher);
                            notify.setRemoteText(R.id.textTitle,context.getString(R.string.app_name));
                            notify.setRemoteImage(R.id.imageNotify, R.mipmap.ic_launcher, 0);
                            notify.setIcon(R.mipmap.ic_launcher,0);

                            notify.output("受信開始",false);

                            notify.setRemoteText(R.id.textMsg,"サブタイトル確認中");
                            //サブタイトルの受信
                            for(String ncode : ncodes){
                                recvSubtitle(context,ncode);
                            }

                            NovelDB db = new NovelDB(context);
                            List<NovelDB.NovelIndex> list = db.getContentNull(ncodes);
                            db.close();

                            int count=0;
                            for(NovelDB.NovelIndex novelIndex : list){
                                notify.setRemoteText(R.id.textMsg,String.format("受信 %d/%d",count++,list.size()));
                                notify.update(false);
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
                            boolean flag = recvContent(context,ncode,index);
                            Intent intent = new Intent().setAction(NOTIFI_NOVELCONTENT).putExtra("index",index==0?1:index);
                            context.sendBroadcast(intent.putExtra("result",flag));

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

                            //ランキングデータの保存
                            NovelDB db = new NovelDB(context);
                            db.addRanking(kind1,kind2,kind3,rankList);
                            db.close();
                            //ランキングncodeをリスト化
                            ArrayList<String> list = new ArrayList<String>();
                            for(NovelRanking b : rankList)
                                list.add(b.ncode);
                            //ノベル詳細データの取得
                            getNovelInfo(context,list);

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

    public static void updateNovelInfoBookmark(Context con){
        NovelDB db = new NovelDB(con);
        List<NovelBookmark> boolmarks = db.getBookmark();
        db.close();
        ArrayList<String> list = new ArrayList<String>();
        for(NovelBookmark b : boolmarks)
            list.add(b.getCode());
        con.sendBroadcast(new Intent(con,NaroReceiver.class).setAction(NaroReceiver.ACTION_NOVELINFO).putExtra("ncode",list));
    }

    boolean recvSubtitle(Context context,String ncode){
        //データの取得
        String[] scode = new String[1];
        List<NovelSubTitle> titles = TbnReader.getSubTitle(ncode,scode);
        if(titles == null)
            return false;

        //シリーズ情報の処理
        NovelSeries seriesInfo = null;
        if(scode[0] != null){
            seriesInfo = TbnReader.getSeriesInfo(scode[0]);
        }

        //サブタイトル情報の保存
        NovelDB db = new NovelDB(context);
        db.addSubTitle(ncode,titles);
        if(seriesInfo!=null)
            db.addSeries(seriesInfo);
        db.close();
        LogService.output(context, ncode+"のサブタイトルの取得完了");



        return true;
    }
    boolean recvContent(Context context,String ncode,int index){
        LogService.format(context,"%s(%d)の本文の取得",ncode,index);
        NovelBody body = TbnReader.getNovelBody(ncode,index);

        if(body == null)
            return false;

        if(index == 0)
            index = 1;
        NovelDB db = new NovelDB(context);
        db.addNovelContents(ncode,index,body);
        db.close();

        return true;
    }
    void startUpdateCheck(Context context){
        AlarmManager alerm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long currentTimeMillis = System.currentTimeMillis();
        NovelDB db = new NovelDB(context);
        boolean updateCheck = db.getSetting("updateCheck",false);
        int updateTime = db.getSetting("updateTime",0)*60*1000;
        db.close();

        PendingIntent pending = PendingIntent.getBroadcast(context, 0,
                new Intent(context,NaroReceiver.class).setAction(ACTION_UPDATE_CHECK), 0);

        if(updateCheck && updateTime > 0){
            if (Build.VERSION.SDK_INT >= 21) {
                alerm.setAlarmClock(new AlarmManager.AlarmClockInfo(currentTimeMillis+updateTime, pending), pending);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alerm.setExact(AlarmManager.RTC_WAKEUP, currentTimeMillis+updateTime, pending);
            } else {
                alerm.set(AlarmManager.RTC_WAKEUP, currentTimeMillis+updateTime, pending);
            }
        }
        else{
            alerm.cancel(pending);
        }
    }
    boolean getNovelInfo(Context context,List<String> ncodes) {
        LogService.output(context, "ノベル情報の取得");

        //取得した検索情報をDBに保存
        List<NovelInfo> info = TbnReader.getNovelInfo(ncodes);
        if (info == null)
            return false;

        //DBを利用
        NovelDB db = new NovelDB(context);
        db.addNovelInfo(info);
        db.close();
        LogService.output(context, "ノベル情報の取得完了");
        return true;
    }
}
