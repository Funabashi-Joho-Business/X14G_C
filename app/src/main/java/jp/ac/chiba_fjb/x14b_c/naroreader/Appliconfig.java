package jp.ac.chiba_fjb.x14b_c.naroreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Appliconfig extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //////////////////アプリ設定画面の処理////////////////////
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliconfig);
        TextView fontconfig = (TextView)findViewById(R.id.config1);
        TextView fontcolor = (TextView)findViewById(R.id.config2);
        TextView haikei = (TextView)findViewById(R.id.config3);
        Button exitconfig = (Button)findViewById(R.id.exitconfigration);

        fontconfig.setOnClickListener(this);
        fontcolor.setOnClickListener(this);
        haikei.setOnClickListener(this);
        exitconfig.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.config1:
                break;
            case R.id.config2:
                break;
            case R.id.config3:
                break;
            case R.id.exitconfigration:
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
        }


    }
}
