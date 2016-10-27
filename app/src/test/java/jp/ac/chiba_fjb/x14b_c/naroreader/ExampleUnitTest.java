package jp.ac.chiba_fjb.x14b_c.naroreader;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBody;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSeries;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSubTitle;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;

import static android.R.id.list;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


        static String userPass="sakura39";
        static String userId = "904973";
    //@Test
    public void testBookmark() throws Exception {

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
    //@Test
    public void testSetBookmark() throws Exception {
        String userId = "";
        String userPass = "";

        String hash = TbnReader.getLoginHash(userId,userPass);
        if(hash == null){
            System.out.println("ログイン失敗");
        }else{
            System.out.format("ハッシュコード: %s\n",hash);

            if(TbnReader.setBookmark(hash,"n1973dd"))
                System.out.println("ブックマーク成功");
            else
                System.out.println("ブックマーク失敗");
        }

    }
    //@Test
    public void testNovelInfo(){
        NovelInfo info = TbnReader.getNovelInfo("n7733dl");
        if(info != null){
            System.out.format("%s\n%s\n",info.title,info.story);
        }
    }
    //@Test
    public void testNovelBody(){
        NovelBody body = TbnReader.getNovelBody("n7733dl",1);
        //NovelBody body = TbnReader.getNovelBody("n3998dn",0);

        System.out.println(body.title);
        System.out.println("-----------------------------");
        System.out.println(body.body);
        System.out.println("-----------------------------");
        System.out.println(body.ranking);
        System.out.println("-----------------------------");

    }
   // @Test
    public void testClearBookmark(){
        String userId = "";
        String userPass = "";

        String hash = TbnReader.getLoginHash(userId,userPass);
        if(hash == null){
            System.out.println("ログイン失敗");
        }else {
            System.out.format("ハッシュコード: %s\n", hash);

            if (TbnReader.clearBookmark(hash, "n6576dm"))
                System.out.println("ブックマーク成功");
            else
                System.out.println("ブックマーク失敗");
        }

    }
   // @Test
    public void testSubTitle(){
        List<NovelSubTitle> list = TbnReader.getSubTitle("n1027cz");
        for(NovelSubTitle item : list){
            System.out.format("%s %s %s\n",item.title,item.date.toString(),item.update!=null?item.update.toString():"-");
        }
    }
    @Test
    public void testSeries(){
        String s = TbnReader.getSeries("n1027cz");
        if(s == null)
            System.out.println("シリーズ無し");
        else {
            System.out.println(s);
            NovelSeries info = TbnReader.getSeriesInfo(s);
            System.out.println(info.title);
            System.out.println(info.info);
            for(String n : info.novelList){
                System.out.println(n);
            }
        }
    }
}