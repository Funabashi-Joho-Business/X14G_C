package jp.ac.chiba_fjb.x14b_c.naroreader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class NovelItem {
    public NovelItem(String ns, int ni, String n, Date d) {
        name = n;
        ncodeI = ni;
        ncodeS = ns;
        update = d;
    }
    public NovelItem(){}

    //いらない
    public int ncodeI;
    public String ncodeS;
    public String name;
    public Date update;
    public int evaluation;
    public int bookmark;
    public int review;
    public int impression;
    public int point1;
    public int point2;
    public int countP;
    public int countU;
    public Date date;
    public int[] weekCount = new int[7];

    public List<NovelAccess> access = new ArrayList<NovelAccess>();
    public List<NovelSubItem> sub = new ArrayList<NovelSubItem>();

    public static class SortUpdate implements Comparator<NovelItem> {
        public int compare(NovelItem a, NovelItem b) {
            if(a.update == null || b.update == null)
                return 0;
            if (a.update.before(b.update)) {
                return 1;

            } else if (a.update.equals(b.update)) {
                return 0;

            } else {
                return -1;
            }
        }
    }
}

