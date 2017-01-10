package jp.ac.chiba_fjb.x14b_c.naroreader.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TbnReader {
    static String decodeHtml(String src){
        return src.replaceAll("&#39;" , "'" )
                .replaceAll("&quot;", "\"")
                .replaceAll("&gt;"  , ">" )
                .replaceAll("&lt;"  , "<" )
                .replaceAll("&amp;" , "&" );
    }

    //クッキーの分解
    public static Map getCookie(HttpURLConnection con){
        Map<String, List<String>>  headers = con.getHeaderFields();
        if(headers == null)
            return null;
        List<String> cookiesHeader = headers.get("Set-Cookie");
        Map<String,String> cookie = new HashMap<String,String>();
        //Iterator it = cookiesHeader.iterator();

        Pattern p = Pattern.compile("([^=]+)=(.*?);.*");
        for(String c : cookiesHeader){
            Matcher m = p.matcher(c);
            if(m.matches())
                cookie.put(m.group(1),m.group(2));
        }
        return cookie;
    }

    //ログイン処理
    public static String getLoginHash(String id, String pass){
        try {
            URL url = new URL("https://ssl.syosetu.com/login/login/");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.setRequestMethod("POST");
            con.setDoOutput(true);
           // con.connect();

            String parameterString = String.format("id=%s&pass=%s",id,pass);
            OutputStream out = con.getOutputStream();
            PrintWriter printWriter = new PrintWriter(out);
            printWriter.print(parameterString);
            printWriter.flush();
            printWriter.close();
            out.close();

            StringBuilder sb = new StringBuilder();
            BufferedReader	br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String str;
            while ( null != ( str = br.readLine() ) ) {
                sb.append(str+"\n");
            }
            br.close();

            con.disconnect();

            Map cookie = getCookie(con);
            if(cookie != null)
                return (String)cookie.get("userl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    static class WebData{
        public String content;
        public Map<String,String> cookie;
    }


    //クッキー付きWEBアクセス
    //adr URL
    //hash ログイントークン
    //param パラメータ
    //cokkie クッキー情報
    public static WebData getContent2(String adr,String hash,Map<String,String> param,Map<String,String> cookie){
        try {
            URL url = new URL(adr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(true);
            con.setRequestMethod("POST");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            String cookieString = "";
            if(cookie != null){
                for(String name : cookie.keySet()){
                    cookieString += name+"="+cookie.get(name)+";";
                }
            }
            else
                cookieString += "userl="+hash;
            con.setRequestProperty("Cookie",cookieString);

            OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
            if(param != null) {
                for (String index : param.keySet()) {
                    String value = index + "=" + param.get(index);
                    os.write(value);
                }
            }
            os.flush();
            os.close();

            StringBuilder sb = new StringBuilder();
            BufferedReader	br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String str;
            while ( null != ( str = br.readLine() ) ) {
                sb.append(str+"\n");
            }
            br.close();
            con.disconnect();



            WebData webData = new WebData();
            webData.content = sb.toString();

            Map<String, List<String>>  headers = con.getHeaderFields();
            webData.cookie = getCookie(con);


            return webData;
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return null;
    }

    //認証付きWEBアクセス
    //adr URL
    //hash ログイントークン
    public static String getContent(String adr,String hash){
        try {
            URL url = new URL(adr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Cookie","userl="+hash);
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            StringBuilder sb = new StringBuilder();
            BufferedReader	br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String str;
            while ( null != ( str = br.readLine() ) ) {
                sb.append(str+"\n");
            }
            br.close();
            con.disconnect();

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //POSTメソッドによる認証付きWEBアクセス
    //adr URL
    //hash ログイントークン
    //param パラメータ
    public static String getContentPost(String adr,String hash,HashMap<String,String> param){
        try {
            URL url = new URL(adr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            con.setRequestProperty("Cookie","userl="+hash);

            OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
            if(param != null) {
                 for (String index : param.keySet()) {
                    String value = index + "=" + param.get(index);
                    os.write(value);
                }
            }
            os.flush();
            os.close();
            StringBuilder sb = new StringBuilder();
            BufferedReader	br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String str;
            while ( null != ( str = br.readLine() ) ) {
                sb.append(str+"\n");
            }
            br.close();
            con.disconnect();

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //httpアクセスでコンテンツの取得
    //adr URL
    public static String getContent(String adr){
        try {
            URL url = new URL(adr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            StringBuilder sb = new StringBuilder();
            BufferedReader	br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String str;
            while ( null != ( str = br.readLine() ) ) {
                sb.append(str+"\n");
            }
            br.close();
            con.disconnect();

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean setEvaluation(String hash,String ncode,int bunsyopoint,int storypoint){
        NovelInfo info = getNovelInfo(ncode);
        if(info == null)
            return false;

        String address;
        if(info.novel_type == 1)
            address = String.format("http://ncode.syosetu.com/%s/%d/",ncode,info.general_all_no);
        else
            address = String.format("http://ncode.syosetu.com/%s/",ncode);
        WebData webData = getContent2(address,hash,null,null);
        if(webData == null)
            return false;

        Pattern p = Pattern.compile("name=\"token\" value=\"(.*?)\"");
        Matcher m = p.matcher(webData.content);
        if(!m.find())
            return false;
        String token = m.group(1);
        System.out.println(token);

        address = String.format("http://ncode.syosetu.com/novelpoint/register/ncode/%d/?token=%s&bunsyopoint=%d&storypoint=%d",convertNcode(ncode),token,bunsyopoint,storypoint);
        getContent(address,hash);

        return true;
    }
    public static List<NovelEvaluation> getEvaluation(String hash){
        int id = getUserID(hash);
        if(id == 0)
            return null;

        String address = String.format("http://mypage.syosetu.com/mypagenovelhyoka/list/userid/%d/",id);
        String content = getContent(address);
        if(content == null)
            return null;

        List<NovelEvaluation> list = new ArrayList<NovelEvaluation>();

        Pattern p = Pattern.compile("<a href=\"http://ncode.syosetu.com/(.*?)/\">.*?ストーリー評価：(\\d?+)pt.*?文章評価：(\\d?+)pt",Pattern.DOTALL);
        Matcher m = p.matcher(content);
        while(m.find()){
            NovelEvaluation e = new NovelEvaluation();
            e.ncode = m.group(1);
            e.storypoint = Integer.parseInt(m.group(2));
            e.bunsyopoint = Integer.parseInt(m.group(3));
            list.add(e);
        }
        return list;
    }
    public static NovelEvaluation getEvaluation(String hash,String ncode){
        int id = getUserID(hash);
        if(id == 0)
            return null;

        String address = String.format("http://mypage.syosetu.com/mypagenovelhyoka/list/userid/%d/",id);
        String content = getContent(address);
        if(content == null)
            return null;

        Pattern p = Pattern.compile("<a href=\"http://ncode.syosetu.com/(.*?)/\">.*?ストーリー評価：(\\d?+)pt.*?文章評価：(\\d?+)pt",Pattern.DOTALL);
        Matcher m = p.matcher(content);
        while(m.find()){
            if(m.group(1).toUpperCase().equals(ncode)){
                NovelEvaluation e = new NovelEvaluation();
                e.ncode = m.group(1).toUpperCase();
                e.storypoint = Integer.parseInt(m.group(2));
                e.bunsyopoint = Integer.parseInt(m.group(3));
                return e;
            }
        }
        return null;
    }
    //ブックマークの設定
    //hash ログイントークン
    //ncode ノベルコード
    public static boolean setBookmark(String hash,String ncode){
        String address = String.format("http://ncode.syosetu.com/novelview/infotop/ncode/%s/",ncode);
        String content = getContent(address,hash);
        if(content == null)
            return false;
        Pattern p = Pattern.compile("<li class=\"booklist\"><a href=\"(.*?)\">ブックマーク");
        Matcher m = p.matcher(content);
        if(m.find()) {
            content = getContent(m.group(1),hash);
            return true;
        }
        return false;
    }
    //しおりの設定
    //hash ログイントークン
    //ncode ノベルコード
    public static boolean setBookmark2(String hash,String ncode,int index){
        String address = String.format("http://ncode.syosetu.com/%s/%d/",ncode,index);
        String content = getContent(address,hash);
        if(content == null)
            return false;
        Pattern p = Pattern.compile("<a href=\"(.*?)\">しおりを挿む");
        Matcher m = p.matcher(content);
        if(m.find()) {
            content = getContent(m.group(1),hash);
            return true;
        }
        return false;
    }

    //ブックマークの解除
    //hash ログイントークン
    //ncode ノベルコード
    public static boolean clearBookmark(String hash,String ncode){
        int code = convertNcode(ncode);
        String address = String.format("http://syosetu.com/favnovelmain/deleteconfirm/favncode/%d/",code);
        WebData webData = getContent2(address,hash,null,null);
        if(webData == null)
            return false;
        Pattern p = Pattern.compile("\\[ID:(.*?)\\] でログイン中</li>.*?<input type=\"hidden\" name=\"token\" value=\"(.*?)\"",Pattern.DOTALL);
        Matcher m = p.matcher(webData.content);
        if(m.find()) {
            HashMap<String,String> param = new HashMap<String,String>();
            param.put("token",m.group(2));
            webData = getContent2("http://syosetu.com/favnovelmain/delete/",hash,param,webData.cookie);
            return true;
        }
        return false;
    }
    public static boolean clearBookmark2(String hash,String ncode){
        int uid = getUserID(hash);
        int code = convertNcode(ncode);
        String address = String.format("http://syosetu.com/favnovelmain/updateinput/useridfavncode/%d_%s/",uid,code);
        WebData webData = getContent2(address,hash,null,null);
        if(webData == null)
            return false;
        Pattern p = Pattern.compile("<form action=\"(.*?)\" method=\"post\">");
        Matcher m = p.matcher(webData.content);
        if(m.find()) {
            HashMap<String,String> param = new HashMap<String,String>();
            param.put("shiori_del","しおりを解除する");
            webData = getContent2("http://syosetu.com/"+m.group(1),hash,param,webData.cookie);
            return true;
        }
        return false;
    }
    public static int getUserID(String hash){
        String content = getContent("http://syosetu.com/user/top/",hash);
        Pattern p = Pattern.compile("\\[ID:(.*?)\\] でログイン中</li>",Pattern.DOTALL);
        Matcher m = p.matcher(content);
        if(m.find())
            return Integer.parseInt(m.group(1));
        return 0;
    }
    //ブックマークの取得
    //hash ログイントークン
    public static List<NovelBookmark> getBookmark(String hash) {
        try {
            int[] marks = new int[10];


            String content = getContent("http://syosetu.com/favnovelmain/list/",hash);
            Pattern p = Pattern.compile("<ul class=\"category_box\">(.+?)</ul>",Pattern.DOTALL);
            Matcher m = p.matcher(content);
            if(m.find()){
                String datas = m.group(1);
                p = Pattern.compile("<a href=\"/favnovelmain/list/\\?nowcategory=(\\d+?)&order=updated_at\">.*\\((\\d+?)\\)</a>");
                m = p.matcher(datas);
                while(m.find()){
                    int no = Integer.parseInt(m.group(1));
                    int count = Integer.parseInt(m.group(2));
                    if(no > 10)
                        continue;
                    marks[no-1] = count;
                }
            }


            List<NovelBookmark> list = new ArrayList<NovelBookmark>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            for(int i=0;i<10;i++)
            {
                if(marks[i] == 0)
                    continue;
                if(i > 0)
                    content = getContent("http://syosetu.com/favnovelmain/list/?nowcategory="+(i+1),hash);

                p = Pattern.compile(
                        "<a class=\"title\" href=\"http://ncode.syosetu.com/(.+?)/\">(.+?)</a>.+?更新日：(.+?)\n.*?"+
                        "<span class=\"no\">\n(?:<a href=\"http://ncode.syosetu.com/.+?/(.+?)/\">|\n)",Pattern.DOTALL);
                m = p.matcher(content);

                while(m.find()){

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(sdf.parse(m.group(3)));
                        int bm = m.group(4) != null?Integer.parseInt(m.group(4)):0;
                        NovelBookmark bookmark = new NovelBookmark(m.group(1).toUpperCase(),i+1,cal,bm);
                        list.add(bookmark);
                }

            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
    public static NovelInfo getNovelInfo(String ncode){
        String address = String.format("http://api.syosetu.com/novelapi/api/?out=json&gzip=5&ncode=%s",ncode);
        NovelInfo[] json = Json.send(address,null,NovelInfo[].class);
        decodeHtml(json);
        if(json != null && json.length > 1)
            return json[1];
        return null;
    }
    public static List<NovelInfo> getNovelInfo(List<String> ncodeList){
	    if(ncodeList == null || ncodeList.size() == 0)
		    return null;

        StringBuilder sb = new StringBuilder();
        for(String ncode : ncodeList){
            if(sb.length() > 0)
                sb.append("-");
            sb.append(ncode);
        }


        String address = String.format("http://api.syosetu.com/novelapi/api/?out=json&lim=500&gzip=5&ncode=%s",sb.toString());
        NovelInfo[] json = Json.send(address,null,NovelInfo[].class);
        decodeHtml(json);
        if(json != null && json.length > 1){
			List<NovelInfo> list = new ArrayList(Arrays.asList(json));
	        list.remove(0);
            return list;
        }
        return null;
    }
    public static List<NovelInfo> getNovelInfoFromParam(String params){
        String address = String.format("http://api.syosetu.com/novelapi/api/?%s",params);
        NovelInfo[] json = Json.send(address,null,NovelInfo[].class);
        decodeHtml(json);
        if(json != null && json.length > 1){
            List<NovelInfo> list = new ArrayList(Arrays.asList(json));
            list.remove(0);
            return list;
        }
        return null;
    }
    public static void decodeHtml(NovelInfo[] list){
        if(list == null)
            return;
        for(int i=1;i<list.length;i++){
            NovelInfo info = list[i];
            info.title = decodeHtml(info.title);
            info.writer = decodeHtml(info.writer);
            info.story = decodeHtml(info.story);
        }
    }


    //本文の取得
    //ncode ノベルコード
    //index 話数(短編では未使用)
    public static NovelBody getNovelBody(String ncode,int index) {

        String address;
        if(index == 0)
            address = String.format("http://ncode.syosetu.com/%s/", ncode);
        else
            address = String.format("http://ncode.syosetu.com/%s/%d/", ncode, index);
        String content = getContent(address);
        if (content == null)
            return null;


        Pattern p = Pattern.compile(
                "(?:<p class=\"novel_title\">(.*?)</p>|<p class=\"novel_subtitle\">(.*?)</p>).*?"+
                "(?:<!--novel_writername-->)*?\n\n\n"+
                "(?:<div id=\"novel_p\" class=\"novel_view\">(.*?)</div>|\n).*?"+
                "<div id=\"novel_honbun\" class=\"novel_view\">(.*?)</div>\n\n"+
                "(?:<div id=\"novel_a\" class=\"novel_view\">(.*?)</div>|\n).*?"+
                "<!--novel_color-->\n\n\n(.*?)\n\n<div class=\"koukoku_auto\">", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        if (!m.find())
            return null;

        NovelBody body = new NovelBody();
        body.title = m.group(1)!=null?m.group(1):m.group(2);
        body.preface = m.group(3);
        body.body =  m.group(4);
        body.trailer =  m.group(5);
        body.tag = m.group(6);

        return body;
    }

    //ノベルコードから数値系コードに変換
    //ncode ノベルコード
    public static int convertNcode(String ncode) {
        //小文字に変換
        ncode = ncode.toLowerCase();

        int length = ncode.length();
        int value = 0;
        int i;
        //整数部分の計算
        for(i=1;i<length;i++){
            char c = ncode.charAt(i);
            if(c >='0' && c<='9')
                value = value*10+ c - '0';
            else
                break;
        }
        for(;i<length;i++){
            char c = ncode.charAt(i);
            int v = (9999 * (int)Math.pow(26,length-i-1) * (c - 'a'));
            value += v;
        }
        return value;
    }

    //サブタイトルの取得
    //ncode ノベルコード
    public static List<NovelSubTitle> getSubTitle(String ncode){
        return getSubTitle(ncode,null);
    }
    public static List<NovelSubTitle> getSubTitle(String ncode,String[] scode){
        String address;
        address = String.format("http://ncode.syosetu.com/%s/", ncode);

        String content = getContent(address);
        if (content == null)
            return null;

        //シリーズ情報を取得
        if(scode != null){
            Pattern p = Pattern.compile("<p class=\"series_title\"><a href=\"/(.*?)/\">");
            Matcher m = p.matcher(content);
            if (m.find()){
                scode[0] = m.group(1);
            }
        }

        //サブタイトル情報を取得
        ArrayList<NovelSubTitle> list = new ArrayList<NovelSubTitle>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年 MM月 dd日");
        Pattern p = Pattern.compile("<dd class=\"subtitle\"><a href=\".*?\">(.*?)</a></dd>.*?<dt class=\"long_update\">\n(.*?)\n.*?(?=<span title=\"(.*?) 改稿\">|).*?</dl>", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        try {
            while (m.find()){
                NovelSubTitle subTitle = new NovelSubTitle();
                subTitle.title = decodeHtml(m.group(1));
                subTitle.date = format.parse(m.group(2));
                if(m.group(3) != null)
                subTitle.update = format.parse(m.group(3));

                list.add(subTitle);
            }
        } catch (ParseException e) {
            return null;
        }
        if(list.size() == 0) {
            //短編処理
            NovelInfo novelInfo = getNovelInfo(ncode);
            if(novelInfo == null)
                return null;

            NovelSubTitle subTitle = new NovelSubTitle();
            subTitle.title = novelInfo.title;
            subTitle.date = novelInfo.general_firstup;
            if(!novelInfo.general_firstup.equals(novelInfo.novelupdated_at))
                subTitle.update = novelInfo.novelupdated_at;
            list.add(subTitle);
        }
        return list;
    }
    //シリーズコードの取得
    //ncode ノベルコード
    public static String getSeries(String ncode){
        String address;
        address = String.format("http://ncode.syosetu.com/%s/", ncode);

        String content = getContent(address);
        if (content == null)
            return null;

        Pattern p = Pattern.compile("<p class=\"series_title\"><a href=\"/(.*?)/\">");
        Matcher m = p.matcher(content);
        if (!m.find())
            return null;
        return m.group(1);
    }

    //シリーズ情報の取得
    // scode シリーズコード
    public static NovelSeries getSeriesInfo(String scode){
        String address;
        address = String.format("http://ncode.syosetu.com/%s/", scode);

        String content = getContent(address);
        if (content == null)
            return null;

        NovelSeries series = new NovelSeries();
        series.scode = scode;

        Pattern p = Pattern.compile("<div class=\"series_title\">(.*?)</div>.*?作成ユーザ：<a href=\"http://mypage.syosetu.com/(.*?)/\">.*?<div class=\"novel_ex\">(.*?)</div>", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        if(!m.find())
            return null;
        series.title = decodeHtml(m.group(1));
        series.writer = Integer.parseInt(m.group(2));
        series.info =decodeHtml(m.group(3));
        series.novelList = new ArrayList<String>();

        p = Pattern.compile("<div class=\"title\"><a href=\"/(.*?)/\">");
        m = p.matcher(content);
        while (m.find()){
            series.novelList.add(m.group(1).toUpperCase());
        }
        return series;
    }


    public static List<NovelRanking> getRanking(int f1,int f2,int f3){
        String address;
        address = getRankingUrl(f1,f2,f3);

        String content = getContent(address);
        if (content == null)
            return null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        ArrayList<NovelRanking> list = new ArrayList<NovelRanking>();


        Pattern p = Pattern.compile(
                "<a class=\"tl\" id=\"best\\d*?\" target=\"_blank\" href=\"http://ncode.syosetu.com/(.*?)/\">.*?"+
                "<span class=\"attention\">(.*?)</span>.*?", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        try {
            while (m.find()){
                NovelRanking ranking = new NovelRanking();
                ranking.ncode = m.group(1).toUpperCase();
                ranking.point = NumberFormat.getInstance().parse(m.group(2)).intValue();
                list.add(ranking);
            }
        } catch (Exception e) {
            return null;
        }

        return list;

    }

    public static String getRankingUrl(int f1,int f2,int f3){
        final String[] RANKING_FILTER1_URL=
            {
                "http://yomou.syosetu.com/rank/list/type/",
                "http://yomou.syosetu.com/rank/genrelist/type/",
                "http://yomou.syosetu.com/rank/isekailist/type/"
            };

        final String[][] RANKING_FILTER2_URL=
            {
                {
                    "daily_",
                    "weekly_",
                    "monthly_",
                    "quarter_",
                    "yearly_",
                    "total_"
                },
                {
                    "daily_",
                    "weekly_",
                    "monthly_",
                    "quarter_",
                    "yearly_"
                },
                {
                    "daily_",
                    "weekly_",
                    "monthly_",
                    "quarter_",
                    "yearly_"
                }
            };
        final String[][] RANKING_FILTER3_URL=
            {
                {
                    "total",
                    "t",
                    "r",
                    "er"
                },
                {
                    "101",
                    "102",
                    "201",
                    "202",
                    "301",
                    "302",
                    "303",
                    "304",
                    "305",
                    "306",
                    "401",
                    "402",
                    "403",
                    "404",
                    "9901",
                    "9902",
                    "9903",
                    "9999",
                },
                {
                    "1",
                    "2",
                    "o"
                },

            };
        return RANKING_FILTER1_URL[f1]+RANKING_FILTER2_URL[f1][f2]+RANKING_FILTER3_URL[f1][f3];

    }
}