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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TbnReader {

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


    //ブックマークの設定
    //hash ログイントークン
    //ncode ノベルコード
    public static boolean setBookmark(String hash,String ncode){
        String address = String.format("http://ncode.syosetu.com/novelview/infotop/ncode/%s/",ncode);
        String content = getContent(address,hash);
        Pattern p = Pattern.compile("<li class=\"booklist\"><a href=\"(.*?)\">ブックマークに追加</a>");
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

                p = Pattern.compile("<a class=\"title\" href=\"http://ncode.syosetu.com/(.+?)/\">(.+?)</a>.+?更新日：(.+?)\n",Pattern.DOTALL);
                m = p.matcher(content);

                while(m.find()){

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(sdf.parse(m.group(3)));
                        NovelBookmark bookmark = new NovelBookmark(m.group(1),m.group(2),i+1,cal);
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
        String address = String.format("http://api.syosetu.com/novelapi/api/?out=json&ncode=%s",ncode);
        NovelInfo[] info = Json.send(address,null,NovelInfo[].class);
        if(info != null && info.length > 1)
            return info[1];
        return null;
    }

    //キーワード検索用
    public static NovelInfo[] getKeyword(String word){
        String address = String.format("http://api.syosetu.com/novelapi/api/?out=json&word=%s",word);
        NovelInfo[] Keyword = Json.send(address,null,NovelInfo[].class);
        if(Keyword != null && Keyword.length > 1)
            return Keyword;
        return null;
    }

    //評価ソート用
    public static NovelInfo getOrder(String order){
        String address = String.format("http://api.syosetu.com/novelapi/api/?out=json&order=%s",order);
        NovelInfo[] evorder = Json.send(address,null,NovelInfo[].class);
        if(evorder != null && evorder.length > 1)
            return evorder[1];
        return null;
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


        Pattern p = Pattern.compile("(?:<p class=\"novel_title\">(.*?)</p>|<p class=\"novel_subtitle\">(.*?)</p>).*?<div id=\"novel_honbun\" class=\"novel_view\">(.*?)</div>.*?<!--novel_color-->\n\n\n(.*?)\n\n<div class=\"koukoku_auto\">", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        if (!m.find())
            return null;

        NovelBody body = new NovelBody();
        body.title = m.group(1)!=null?m.group(1):m.group(2);
        body.body =  m.group(3);
        body.ranking = m.group(4);

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
        String address;
        address = String.format("http://ncode.syosetu.com/%s/", ncode);

        String content = getContent(address);
        if (content == null)
            return null;

        ArrayList<NovelSubTitle> list = new ArrayList<NovelSubTitle>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy年 MM月 dd日");

        Pattern p = Pattern.compile("<dd class=\"subtitle\"><a href=\".*?\">(.*?)</a></dd>.*?<dt class=\"long_update\">\n(.*?)\n.*?(?=<span title=\"(.*?) 改稿\">)?.*?</dl>", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        try {
            while (m.find()){
                NovelSubTitle subTitle = new NovelSubTitle();
                subTitle.title = m.group(1);
                subTitle.date = format.parse(m.group(2));
                if(m.group(3) != null)
                subTitle.update = format.parse(m.group(3));

                list.add(subTitle);
            }
        } catch (ParseException e) {
            return null;
        }
        if(list.size() == 0)
            return null;
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

        Pattern p = Pattern.compile("<div class=\"series_title\">(.*?)</div>.*?作成ユーザ：<a href=\"http://mypage.syosetu.com/(.*?)/\">.*?<div class=\"novel_ex\">(.*?)</div>", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        if(!m.find())
            return null;
        series.title = m.group(1);
        series.info = m.group(2);
        series.novelList = new ArrayList<String>();

        p = Pattern.compile("<div class=\"title\"><a href=\"/(.*?)/\">");
        m = p.matcher(content);
        while (m.find()){
            series.novelList.add(m.group(1));
        }
        return series;
    }
    public static List<NovelRanking> getRanking(String url){
        String address;
        address = url;

        String content = getContent(address);
        if (content == null)
            return null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        ArrayList<NovelRanking> list = new ArrayList<NovelRanking>();


        Pattern p = Pattern.compile(
                "<a class=\"tl\" id=\"best\\d*?\" target=\"_blank\" href=\"http://ncode.syosetu.com/(.*?)/\">(.*?)</a>.*?"+
                "小説情報</a>／作者：<a href=\"http://mypage.syosetu.com/(\\d*?)/\">(.*?)</a>.*?" +
                "<span class=\"attention\">(.*?)</span>.*?"+
                "<br />\\(全(\\d*?)部分\\).*?" +
                "<td class=\"ex\">\n(.*?)\n</td>.*?" +
                "<td>\n(.*?)\n</td>.*?" +
                "<td>\n最終更新日：(.*?)\n.*?" +
                "<span class=\"marginleft\">(.*?)文字</span>", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        try {
            while (m.find()){
                NovelRanking ranking = new NovelRanking();
                ranking.ncode = m.group(1);
                ranking.title = m.group(2);
                ranking.writerId = Integer.parseInt(m.group(3));
                ranking.writerName = m.group(4);
                ranking.point = NumberFormat.getInstance().parse(m.group(5)).intValue();
                ranking.novelCount = Integer.parseInt(m.group(6));
                ranking.info = m.group(7);
                ranking.genle = m.group(8);
                ranking.update = format.parse(m.group(9));
                ranking.textCount = NumberFormat.getInstance().parse(m.group(10)).intValue();
                list.add(ranking);
            }
        } catch (Exception e) {
            return null;
        }

        return list;

    }
}