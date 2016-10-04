package jp.ac.chiba_fjb.x14b_c.naroreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Appliconfig extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //////////////////アプリ設定画面の処理////////////////////
        super.onCreate(savedInstanceState);
        TextView fontconfig = (TextView)findViewById(R.id.Config1);
        setContentView(R.layout.activity_appliconfig);
    }
}
