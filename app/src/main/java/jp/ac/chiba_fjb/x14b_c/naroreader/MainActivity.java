package jp.ac.chiba_fjb.x14b_c.naroreader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;

import jp.ac.chiba_fjb.x14b_c.naroreader.Bookmark.BookmarkFragment;
import to.pns.lib.LogService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(Gravity.LEFT);

        mBundle = new Bundle();

        changeFragment(BookmarkFragment.class);

        LogService.output(getApplicationContext(),"アプリ起動");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_bookmark:
                changeFragment(BookmarkFragment.class);
                break;
            case R.id.nav_log:
                changeFragment(FragmentLog.class);
                break;
        }
       ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
        return true;
    }


    void changeFragment(Class c){
        Bundle bundle = new Bundle();
        Fragment f = getSupportFragmentManager().getFragment(bundle,c.getName());
        try {
            if(f == null)
                f = (Fragment) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_area,f);
        ft.commit();
    }



}
