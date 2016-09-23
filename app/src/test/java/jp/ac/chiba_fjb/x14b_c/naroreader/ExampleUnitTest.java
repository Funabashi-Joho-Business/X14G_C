package jp.ac.chiba_fjb.x14b_c.naroreader;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.R.id.list;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String userId = "904973";
        String userPass = "sakura39";

        String hash = TbnReader.getLoginHash(userId,userPass);
        if(hash == null){
            System.out.println("ログイン失敗");
        }else{
            System.out.format("ハッシュコード: %s\n",hash);
        }

        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        List<NovelBookmark> list = TbnReader.getBookmark(hash);
        for(NovelBookmark b : list){
            System.out.format("%s %02d %s %s\n",b.getCode(),b.getCategory(),f.format(b.getUpdate().getTime()),b.getName());
        }
    }
}