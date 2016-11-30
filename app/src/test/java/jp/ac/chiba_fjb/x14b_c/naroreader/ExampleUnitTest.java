package jp.ac.chiba_fjb.x14b_c.naroreader;

import org.junit.Test;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBody;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelRanking;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSeries;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSubTitle;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.TbnReader;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
	@Test
	public void testEvo(){
		String userId = "";
		String userPass = "";

		String hash = TbnReader.getLoginHash(userId,userPass);
		if(hash == null){
			System.out.println("ログイン失敗");
		}else{
			System.out.format("ハッシュコード: %s\n",hash);
		}
		//List<NovelEvaluation> list = TbnReader.getEvaluation(hash);
		//TbnReader.setEvaluation(hash,"n7975cr",5,5);
	}

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
    //@Test
	public void testSubTitle(){
		List<NovelSubTitle> list = TbnReader.getSubTitle("n1027cz");
		for(NovelSubTitle item : list){
			System.out.format("%s %s %s\n",item.title,item.date.toString(),item.update!=null?item.update.toString():"-");
		}
	}
	//@Test
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

	//@Test
	public void classPut(){
		Class c = NovelInfo.class;
		for(Field f : c.getFields()) {
			System.out.println(f.getName()+" "+f.getType().getName());
		}
	}


    //@Test
	public void getRankList(){


        final String[] RANKING_FILTER1_NAME=
            {
                "総合",
                "ジャンル別",
                "異世界転生/転移"
            };

        final String[][] RANKING_FILTER2_NAME=
            {
                {
                    "日間",
                    "週間",
                    "月間",
                    "四半期",
                    "年間",
                    "累計"
                },
                {
                    "日間",
                    "週間",
                    "月間",
                    "四半期",
                    "年間"
                },
                {
                    "日間",
                    "週間",
                    "月間",
                    "四半期",
                    "年間"
                }
            };
        final String[][] RANKING_FILTER3_NAME =
            {
                {
                    "すべて",
                    "短編",
                    "連載中",
                    "完結済"

                },
                {
                    "恋愛(異世界)",
                    "恋愛(現実世界)",
                    "ハイファンタジー",
                    "ローファンタジー",
                    "純文学",
                    "ヒューマンドラマ",
                    "歴史",
                    "推理",
                    "ホラー",
                    "アクション",
                    "コメディー",
                    "VRゲーム",
                    "宇宙",
                    "空想科学",
                    "パニック",
                    "その他",
                    "童話",
                    "詩",
                    "エッセイ",
                    "その他"

                },
                {
                    "恋愛",
                    "ファンタジー",
                    "文芸・SF・その他"

                }

            };


        Scanner sc = new Scanner(System.in);

        System.out.println("大分類");
        for(int i=0;i<RANKING_FILTER1_NAME.length;i++)
            System.out.format("%d:%s\n",i,RANKING_FILTER1_NAME[i]);
        int filter1 = 1;//sc.nextInt();

        System.out.println("中分類");
        for(int i=0;i<RANKING_FILTER2_NAME[filter1].length;i++)
            System.out.format("%d:%s\n",i,RANKING_FILTER2_NAME[filter1][i]);
        int filter2 = 0;//sc.nextInt();

        System.out.println("小分類");
        for(int i=0;i<RANKING_FILTER3_NAME[filter1].length;i++)
            System.out.format("%d:%s\n",i,RANKING_FILTER3_NAME[filter1][i]);
        int filter3 = 2;//sc.nextInt();


        List<NovelRanking> list = TbnReader.getRanking(filter1,filter2,filter3);
		if(list == null)
			System.out.println("データ取得失敗");
		else {
			for(NovelRanking r : list){
				System.out.format("%s %s\n",r.ncode,r.title);
			}
		}
	}
}