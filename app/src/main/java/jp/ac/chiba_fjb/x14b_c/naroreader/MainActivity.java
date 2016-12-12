package jp.ac.chiba_fjb.x14b_c.naroreader;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ac.chiba_fjb.x14b_c.naroreader.Bookmark.BookmarkFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.FragmentLog;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.Ranking.RankingFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.SearchPack.SearchFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.History.HistoryFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;
import to.pns.lib.LogService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Bundle mBundle;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null){
            if(intent.getAction().equals(Intent.ACTION_SEND)){
                String url = intent.getStringExtra(Intent.EXTRA_TEXT);
                if(url != null){
                    addBookmark(url);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //drawer.openDrawer(Gravity.LEFT);//起動時にドロわーを開く

        mBundle = new Bundle();

        changeFragment(BookmarkFragment.class);

        LogService.output(getApplicationContext(),"アプリ起動");

    }

    private void addBookmark(String url) {
        Pattern p = Pattern.compile("ncode.syosetu.com/(.*?)/");
        final Matcher m = p.matcher(url);
        if (!m.find())
            return;

        NovelDB db = new NovelDB(this);
        final String userId = db.getSetting("loginId","");
        final String userPass = db.getSetting("loginPass","");
        db.close();

        new Thread(){
            @Override
            public void run() {
                String hash = TbnReader.getLoginHash(userId,userPass);
                if(hash != null){
                    TbnReader.setBookmark(hash,m.group(1).toUpperCase());
                    NaroReceiver.updateBookmark(MainActivity.this);
                }
            }
        }.start();



    }

    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        super.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_titles:
                changeFragment(HistoryFragment.class);
                break;
            case R.id.nav_bookmark:
                changeFragment(BookmarkFragment.class);
                break;
            case R.id.nav_log:
                changeFragment(FragmentLog.class);
                break;
            case R.id.nav_ranking:
                changeFragment(RankingFragment.class);
                break;
            case R.id.nav_search:
                changeFragment(SearchFragment.class);
                break;
            case R.id.nav_config:
                changeFragment(ConfigFragment.class);
                break;
        }

       ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
        return true;
    }

    boolean firstFlag = true;
    public void changeFragment(Class c){
        changeFragment(c,null);
    }
    public void changeFragment(Class c,Bundle budle){

        try {
            //フラグメントの作成
            Fragment f = (Fragment) c.newInstance();
            f.setArguments(budle);
            //フラグ面tのの切り替え処理
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(
                R.anim.fragment_in,
                R.anim.fragment_out);
            ft.replace(R.id.fragment_area,f);
            if(firstFlag)
                firstFlag = false;
            else
                ft.addToBackStack(null);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        //メニューが開いていたら閉じる
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(Gravity.LEFT))
            drawer.closeDrawer(Gravity.LEFT);
        else
            super.onBackPressed();
    }

}
