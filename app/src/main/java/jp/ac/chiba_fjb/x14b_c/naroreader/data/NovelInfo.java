package jp.ac.chiba_fjb.x14b_c.naroreader.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown=true)
public class NovelInfo {
    public Integer allcount;

    public String title;
    public String ncode;
    public int userid;
    public String writer;
    public String story;
    public int biggenre;
    public int genre;
    public String gensaku;
    public String keyword;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date general_firstup;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date general_lastup;
    public int novel_type;
    public int end;
    public int general_all_no;
    public int length;
    public int time;
    public int isstop;
    public int isr15;
    public int isbl;
    public int isgl;
    public int iszankoku;
    public int istensei;
    public int istenni;
    public int pc_or_k;
    public int global_point;
    public int fav_novel_cnt;
    public int review_cnt;
    public int all_point;
    public int all_hyoka_cnt;
    public int sasie_cnt;
    public int kaiwaritu;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date novelupdated_at;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date updated_at;

}
