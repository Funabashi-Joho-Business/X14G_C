package jp.ac.chiba_fjb.x14b_c.naroreader;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBody;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    //@Test
    public void testBookmark() throws Exception {
        String userId = "";
        String userPass = "";

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
    @Test
    public void testCode(){

        System.out.println(TbnReader.convertNcode("n7733dl"));
        //897644
        //2869914

    }
}