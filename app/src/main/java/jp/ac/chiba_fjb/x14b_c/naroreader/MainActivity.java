package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.BookmarkFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.HistoryFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.LogFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.Ranking.RankingFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.SearchFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.SearchPanelFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;
import to.pns.lib.LogService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Bundle mBundle;




    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        addBookmark(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        toolbar.measure(-1,-1);
//        findViewById(R.id.fragment_area).setPadding(0,toolbar.getMeasuredHeight(),0,0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        AdView adView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                                  .addTestDevice("6DB381F1EC310CCE075C40F06962EDFA-")
                                  .build();
        adView.loadAd(adRequest);

        mBundle = new Bundle();
        changeFragment(BookmarkFragment.class);
        LogService.output(getApplicationContext(),"アプリ起動");
        addBookmark(getIntent());


        //レシーバーに起動通知
        sendBroadcast(new Intent(this,NaroReceiver.class).setAction(NaroReceiver.ACTION_UPDATE_SETTING));

    }
    void bookmarkIntent(Intent intent){

    }
    private void addBookmark(Intent intent) {
        if(intent != null){
            if(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)){
                String url = intent.getStringExtra(Intent.EXTRA_TEXT);
                if(url != null){
                    Pattern p = Pattern.compile("ncode.syosetu.com/(.*?)/");
                    final Matcher m = p.matcher(url);
                    if (!m.find())
                        return;

                    NovelDB db = new NovelDB(this);
                    final String userId = db.getSetting("loginId","");
                    final String userPass = db.getSetting("loginPass","");
                    db.close();
                    Snackbar.make(findViewById(R.id.fragment_area), "ブックマーク追加中", Snackbar.LENGTH_SHORT).show();
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
            }
        }
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
                changeFragment(LogFragment.class);
                break;
            case R.id.nav_ranking:
                changeFragment(RankingFragment.class);
                break;
            case R.id.nav_search:
                changeFragment(SearchPanelFragment.class);
                break;
            case R.id.nav_search_result:
                changeFragment(SearchFragment.class);
                break;
            case R.id.nav_config:
                changeFragment(ConfigFragment.class);
                break;
            case R.id.nav_exit:
                finish();
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
            Fragment f;
            f = getSupportFragmentManager().findFragmentByTag(c.getSimpleName());
            if(f==null) {
                f = (Fragment) c.newInstance();
                if(budle != null)
                    f.setArguments(budle);
                else
                    f.setArguments(new Bundle());
            }
            else{
                if( f.getArguments() != null && budle!=null)
                    f.getArguments().putAll(budle);
            }


            //フラグ面tのの切り替え処理
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(
                R.anim.fragment_in,
               R.anim.fragment_out,
               R.anim.fragment_in,
               R.anim.fragment_out);
            ft.replace(R.id.fragment_area,f,c.getSimpleName());
            if(firstFlag)
                firstFlag = false;
            else
                ft.addToBackStack(c.getSimpleName());
            ft.commit();

            showAppBar(true);

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
        else {
            showAppBar(true);
            super.onBackPressed();
        }
    }

    public void showAppBar(boolean flag){
        if(flag) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            ((AppBarLayout.LayoutParams) toolbar.getLayoutParams()).setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        }
        AppBarLayout appbar = (AppBarLayout)findViewById(R.id.appbar);
        appbar.setExpanded(flag);

    }
    public void setAppBarScroll(boolean flag) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        int a = ((AppBarLayout.LayoutParams) toolbar.getLayoutParams()).getScrollFlags();
        if (flag)
            ((AppBarLayout.LayoutParams) toolbar.getLayoutParams()).setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL|AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        else {
            ((AppBarLayout.LayoutParams) toolbar.getLayoutParams()).setScrollFlags(0);
        }
        showAppBar(!flag);
    }

    @Override
    public void finish() {

        new AlertDialog.Builder(this)
                .setTitle("アプリの終了")
                .setMessage("終了しますか？")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        //super.finish();
    }
}
