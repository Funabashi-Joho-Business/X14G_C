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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TbnReader {

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
    public static String getLoginHash(String id, String pass){
        try {
            URL url = new URL("https://ssl.syosetu.com/login/login/");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
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

    public static WebData getContent2(String adr,String hash,Map<String,String> param,Map<String,String> cookie){
        try {
            URL url = new URL(adr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");


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
    public static String getContent(String adr,String hash){
        try {
            URL url = new URL(adr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Cookie","userl="+hash);

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
    public static String getContentPost(String adr,String hash,HashMap<String,String> param){
        try {
            URL url = new URL(adr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
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
    public static String getContent(String adr){
        try {
            URL url = new URL(adr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");

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

    public static String getHashToId(String hash){
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(hash);
        if(m.find()){
            return m.group(1);
        }
        return null;
    }
    public  static boolean getNobelStat(NovelItem novelItem,String hash){
        String content = getContent(String.format("http://syosetu.com/usernovelmanage/top/ncode/%d/",novelItem.ncodeI),hash);
        if(content == null)
            return false;
        NumberFormat num = NumberFormat.getInstance();
        Pattern p;
        Matcher m;
        int pt = 0;
        p = Pattern.compile(
                "<td class=\"title\"><a href=\"/usernoveldatamanage/top/ncode/.+?/noveldataid/(.*?)/\">(.*?)</a></td>\n\n" +
                "<td class=\"update\">(.+?)</td>");
        m = p.matcher(content);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd　HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH");
        while(m.find()){
            try {
                NovelSubItem sub = new NovelSubItem();
                sub.codeI = Integer.parseInt(m.group(1));
                sub.name = m.group(2);
                String d = m.group(3);
                Matcher m2 = Pattern.compile("<span class=\"yoyaku\">予約中&nbsp;(.+?)時</span>").matcher(d);
                if(m2.find()) {
                    sub.reserve = true;
                    sub.date = sdf2.parse(m2.group(1));
                }else {
                    sub.reserve = false;
                    sub.date = sdf.parse(d);
                }
                novelItem.sub.add(sub);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            pt = m.end();
        }


        m.usePattern(Pattern.compile(
                "<dt>感想</dt>\n<dd>\n([\\d,]+)件.*?" +
                "<dt>レビュー</dt>\n<dd>\n([\\d+,])件.*?" +
                "評価者数：([\\d,]+)人.*?" +
                "ブックマーク登録：([\\d,]+)件.*?" +
                "(?:まだ評価されていません|<span class=\"marginleft\">合計：([\\d,]+)pt</span>).*?"+
                "(?:まだ評価されていません|<span class=\"marginleft\">合計：([\\d,]+)pt</span>)",Pattern.DOTALL));
        m.region(pt, m.regionEnd());

        if (!m.find())
            return false;
        try {
            novelItem.impression = num.parse(m.group(1)).intValue();
            novelItem.review = num.parse(m.group(2)).intValue();
            novelItem.evaluation = num.parse(m.group(3)).intValue();
            novelItem.bookmark = num.parse(m.group(4)).intValue();
            if(m.group(5) != null){
                novelItem.point1 = num.parse(m.group(5)).intValue();
                novelItem.point2 = num.parse(m.group(6)).intValue();
            }
            else{
                novelItem.point1 = 0;
                novelItem.point2 = 0;
            }



            return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean getNobelAccess(NovelItem novelItem,String hash){
        try {
            String content = getContent(String.format("http://kasasagi.hinaproject.com/access/top/ncode/%s/",novelItem.ncodeS),hash);
            if(content == null)
                return false;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

            Pattern p;
            Matcher m;
            p = Pattern.compile("<p class=\"novelview_h3\">本日\\((.*?)\\)のアクセス解析</p>");
            m = p.matcher(content);
            if (!m.find())
                return false;
            Date date = sdf.parse(m.group(1));
            novelItem.date = (Date)date.clone();
            int pt;
            NumberFormat num = NumberFormat.getInstance();
            //二日それぞれの合計数
            for(int j = 0;j<2;j++){
                NovelAccess na = new NovelAccess();
                na.date = (Date) date.clone();
                pt = m.end();
                m.usePattern(Pattern.compile("<td class=\"pv\">(.*?)</td>"));
                m.region(pt, m.regionEnd());
                for(int i=0;i<24;i++){
                    if (!m.find())
                        return false;
                    na.countHour[i] = num.parse(m.group(1)).intValue();
                }
                novelItem.access.add(na);
                date.setDate(date.getDate()-1);
            }

            pt = m.end();
            m.usePattern(Pattern.compile("<td>累計</td>\n<td class=\"right\">([\\d,]+).*?<td class=\"right\">([\\d,]+)",Pattern.DOTALL));
            m.region(pt, m.regionEnd());
            if (!m.find())
                return false;
            novelItem.countP = num.parse(m.group(1)).intValue();
            novelItem.countU = num.parse(m.group(2)).intValue();


            pt = m.end();
            m.usePattern( Pattern.compile("<td class=\"pv\">([\\d,]+?)</td>"));
            m.region(pt, m.regionEnd());
            //週間アクセス数
            for(int i = 0;i<7;i++){
                if (!m.find())
                    return false;
                novelItem.weekCount[7-i-1] = num.parse(m.group(1)).intValue();
            }

            return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static List<NovelItem> getNobels(String hash){
        List list = new ArrayList<NovelItem>();

        try {
            String content = getContent("http://syosetu.com/usernovel/list/",hash);
            Pattern p = Pattern.compile("<table id=\"novellist\">\n");
            Matcher m = p.matcher(content);
            if(m.find()){
                p = Pattern.compile("<td class=\"ncode\">(.*?)</td>\n<td class=\"title\"><a href=\"/usernovelmanage/top/ncode/(\\d+)/\">(.*?)</a>\n(?:<span class=\"announcement\">.*?</span>\n)?</td>\n\n<td class=\"update\">(.*?)</td>");
                m = p.matcher( content.substring(m.end()));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

                while(m.find()){
                    try{
                        NovelItem nd = new NovelItem(m.group(1),Integer.parseInt(m.group(2)),m.group(3),sdf.parse(m.group(4)));
                        list.add(nd);
                    }catch(Exception e){ }

                 }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

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
}