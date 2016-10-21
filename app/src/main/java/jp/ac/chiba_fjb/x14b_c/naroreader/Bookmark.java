package jp.ac.chiba_fjb.x14b_c.naroreader;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import static jp.ac.chiba_fjb.x14b_c.naroreader.R.id.textView;


/**
 * A simple {@link Fragment} subclass.
 */

//データベース管理用クラス
class TestDB extends SQLite
{

    public TestDB(Context context) {
        //ここでデータベースのファイル名とバージョン番号を指定
        super(context, "test.db",1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //初期テーブルの作成
        db.execSQL("create table test(name text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //バージョン番号を変えた場合に呼び出される
    }

}

public class Bookmark extends Fragment implements View.OnClickListener {


    public Bookmark() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_bookmark, container, false);

        Button Bookmarkbutton = (Button)view.findViewById(R.id.BookmarkButton1);
        //インスタンスの取得
        Bookmarkbutton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        ////////////////////////////////////////////////////////////////////////////////////////////
        //////////////ブックマーク件数を抽出・件数分TextViewを生成し関連付けしたい//////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////

        //インスタンスの取得
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.BookmarkLayout);
        //動的なコントロールの生成


        /*TextView sikiri = new TextView(getContext());
        sikiri.setText("------------------------");
        //TextViewをBookmark数ぶんだけ生成（予定）
        //データベースに接続
        TestDB db = new TestDB(this);
        //データの挿入
        db.exec("insert into test values('あいうえお');");

        //クエリーの発行
        Cursor res = db.query("select * from test;");
        //データがなくなるまで次の行へ
        while(res.moveToNext())
            {
            //0列目を取り出し
            textView.append(res.getInt(0)+"\n");
            }



        //カーソルを閉じる
        res.close();
        //データベースを閉じる
        db.close();*/


    }
}
