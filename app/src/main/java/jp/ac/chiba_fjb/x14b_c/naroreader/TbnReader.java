package jp.ac.chiba_fjb.x14b_c.naroreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
        Iterator it = cookiesHeader.iterator();

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
}