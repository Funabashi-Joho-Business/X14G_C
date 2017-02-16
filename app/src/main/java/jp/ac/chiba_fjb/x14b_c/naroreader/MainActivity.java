package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.BottomDialog;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.LogFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NaroReceiver;
import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.BookmarkFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.HistoryFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.RankingFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.SearchFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSeries;
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
            f = getSupportFragmentManager().findFragmentByTag(BottomDialog.class.getName());
            if(f != null)
                ((BottomDialog)f).getDialog().cancel();

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

            //ソフトキーボードを非表示
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            FrameLayout toolFrame = (FrameLayout)findViewById(R.id.toolFrame);
            toolFrame.removeAllViews();
            showAppBar(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        //メニューが開いていたら閉じる
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(Gravity.LEFT))
            drawer.closeDrawer(Gravity.LEFT);
        else {
            FrameLayout toolFrame = (FrameLayout) findViewById(R.id.toolFrame);
            toolFrame.removeAllViews();
            showAppBar(true);
            if (!getSupportFragmentManager().popBackStackImmediate()) {
                super.onBackPressed();
            }
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
    }

    public boolean enterMenu(int id,String ncode){
        Uri uri;
        Intent i;

        switch(id){
            case R.id.menu_bbs:
                uri = Uri.parse("http://novelcom.syosetu.com/impression/list/ncode/"+ TbnReader.convertNcode(ncode)+"/");
                i = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i);
                break;
            case R.id.menu_review:
                uri = Uri.parse("http://novelcom.syosetu.com/novelreview/list/ncode/"+ TbnReader.convertNcode(ncode)+"/");
                i = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i);
                break;
            case R.id.menu_access:
                uri = Uri.parse("http://kasasagi.hinaproject.com/access/top/ncode/"+ ncode+"/");
                i = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i);
                break;
            case R.id.menu_info_writer: {
                NovelDB db = new NovelDB(this);
                NovelInfo info = db.getNovelInfo(ncode);
                db.close();
                if(info != null) {
                    uri = Uri.parse("http://mypage.syosetu.com/" + info.userid + "/");
                    i = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(i);
                }
                break;
            }
            case R.id.rank_point:
                Bundle bn = new Bundle();
                bn.putString("ncode",ncode);
                RankPointFragment f = new RankPointFragment();
                f.setArguments(bn);
                f.show(getSupportFragmentManager(),"");
                break;
            case R.id.menu_download:
                ArrayList<String> list = new ArrayList();
                list.add(ncode);
                NaroReceiver.download(this,list);
                break;
            case R.id.menu_clear_bookmark2:
                NaroReceiver.clearBookmark2(this,ncode);
                break;
            case R.id.menu_bookmark_add:
                AddBookmarkFragment.show(this,ncode,true);
                break;
            case R.id.menu_bookmark_del:
                AddBookmarkFragment.show(this,ncode,false);
                break;
            case R.id.menu_search_writer: {
                NovelDB db = new NovelDB(this);
                NovelInfo info = db.getNovelInfo(ncode);
                if (info != null) {
                    Bundle bundle = new Bundle();
                    try {
                        bundle.putInt("writer",info.userid);
                        bundle.putString("params", "out=json&gzip=5&wname=1&word=" + URLEncoder.encode(info.writer, "UTF-8"));
                        changeFragment(SearchFragment.class, bundle);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
                db.close();
                break;
            }
            case R.id.menu_search_series: {
                NovelDB db = new NovelDB(this);
                NovelSeries series = db.getSeriesInfo(ncode);
                if (series != null) {
                    List<String> ncodes = db.getSeriesNcode(series.scode);
                    db.close();
                    StringBuilder sb = new StringBuilder();
                    for (String n : ncodes) {
                        if (sb.length() > 0)
                            sb.append("-");
                        sb.append(n);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt("writer",0);
                    bundle.putString("params", "out=json&gzip=5&ncode=" + sb.toString());
                    changeFragment(SearchFragment.class, bundle);
                } else
                    Snackbar.make(findViewById(R.id.coordinator), "シリーズ情報無し", Snackbar.LENGTH_SHORT).show();
                db.close();
                break;
            }

        }
        return false;
    }
}
