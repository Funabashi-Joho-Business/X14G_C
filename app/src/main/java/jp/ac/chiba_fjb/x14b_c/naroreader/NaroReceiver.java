package jp.ac.chiba_fjb.x14b_c.naroreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;

public class NaroReceiver extends BroadcastReceiver {
    public NaroReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
//        if(intent.getAction() == "READ_BOOKMARK"){
//
//        }
        new Thread(){
            @Override
            public void run() {
                String hash = TbnReader.getLoginHash("","");
                if(hash == null)
                    return;
                NovelDB db = new NovelDB(context);

                List<NovelBookmark> bookmarks = TbnReader.getBookmark(hash);
                for(NovelBookmark b : bookmarks){
                    db.addBookmark(b.getCode(),b.getName(),b.getUpdate().getTime(),b.getCategory());
                }
                db.close();
            }
        }.start();


    }
}
