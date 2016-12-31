package jp.ac.chiba_fjb.x14b_c.naroreader.Titles;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.chiba_fjb.x14b_c.naroreader.Other.NovelDB;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSeries;

/**
リサイクルビューに使用するデータ関連づけ用アダプター
 */

public class TitleAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
    final static Map<Integer,String> GENRE = new HashMap<Integer,String>(){
        {put(101, "恋愛 異世界");}
        {put(102, "恋愛 現実世界");}
        {put(201, "ハイファンタジー");}
        {put(202, "ローファンタジー");}
        {put(301, "純文学");}
        {put(302, "ヒューマンドラマ");}
        {put(304, "推理");}
        {put(305, "ホラー");}
        {put(306, "アクション");}
        {put(307, "コメディー");}
        {put(401, "VRゲーム");}
        {put(402, "宇宙");}
        {put(403, "空想科学");}
        {put(404, "パニック");}
        {put(9901, "童話");}
        {put(9902, "詩");}
        {put(9903, "エッセイ");}
        {put(9904, "リプレイ");}
        {put(9999, "その他");}
        {put(9801, "ノンジャンル");}
    };


    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        String ncode = (String)compoundButton.getTag();
        if(b) {
            mCheckSet.add(ncode);
        }
        else
            mCheckSet.remove(ncode);
    }


    public interface OnItemClickListener{
        public void onItemClick(NovelInfo bookmark);
        public void onItemLongClick(NovelInfo bookmark);
    }
    private OnItemClickListener mListener;
    private List<? extends NovelInfo> mNovelInfo;
    private Map<String, NovelSeries> mNovelSeries;
    private boolean mInfo = false;
    HashSet<String> mCheckSet = new HashSet<String>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //レイアウトを設定
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_title, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        ((CheckBox)view.findViewById(R.id.checkBox)).setOnCheckedChangeListener(this);
        return new RecyclerView.ViewHolder(view){}; //本当はここでアイテム設定を実装するのだけれど、簡単にするためスルー
    }
    public static void setInfoToItem(View itemView, NovelInfo novelInfo,NovelSeries novelSeries,boolean info){

        NumberFormat nf = NumberFormat.getNumberInstance();
        String dateString = new SimpleDateFormat("yyyy年MM月dd日(E) HH時mm分").format(novelInfo.general_lastup.getTime());
        ((TextView)itemView.findViewById(R.id.textDate)).setText(dateString);
        ((TextView)itemView.findViewById(R.id.textTitle)).setText(novelInfo.title);
        ((TextView)itemView.findViewById(R.id.textWritter)).setText(novelInfo.writer);
        ((TextView)itemView.findViewById(R.id.textPoint)).setText(nf.format(novelInfo.global_point));
        ((TextView)itemView.findViewById(R.id.textCount)).setText(nf.format(novelInfo.general_all_no));
        ((TextView)itemView.findViewById(R.id.textBookmark)).setText(nf.format(novelInfo.fav_novel_cnt));
        ((TextView)itemView.findViewById(R.id.textEva)).setText(
                novelInfo.review_cnt==0?"0":String.format("%1.1f",(float)novelInfo.all_point/novelInfo.all_hyoka_cnt/2));
        ((TextView)itemView.findViewById(R.id.textEvaCount)).setText(nf.format(novelInfo.all_hyoka_cnt));
        ((TextView)itemView.findViewById(R.id.textReview)).setText(nf.format(novelInfo.review_cnt));
        ((TextView)itemView.findViewById(R.id.textLength)).setText(nf.format(novelInfo.length));
        ((TextView)itemView.findViewById(R.id.textGenre)).setText(GENRE.get(novelInfo.genre));
        if(novelSeries!=null){
            itemView.findViewById(R.id.layoutSeries).setVisibility(View.VISIBLE);
            ((TextView)itemView.findViewById(R.id.textSeries)).setText(novelSeries.title);
        }
        else {
            itemView.findViewById(R.id.layoutSeries).setVisibility(View.GONE);
            ((TextView) itemView.findViewById(R.id.textSeries)).setText("");
        }
        if(info || novelInfo instanceof NovelDB.NovelInfoRanking){
            itemView.findViewById(R.id.textInfo).setVisibility(View.VISIBLE);
            ((TextView) itemView.findViewById(R.id.textInfo)).setText(novelInfo.story);
        }
        else{
            itemView.findViewById(R.id.textInfo).setVisibility(View.GONE);
        }
        String bookmark = "";
        if(novelInfo instanceof NovelDB.NovelInfoBookmark){
            NovelDB.NovelInfoBookmark infoBookmark = (NovelDB.NovelInfoBookmark)novelInfo;
            if(infoBookmark.b_category != 0) {
                if (infoBookmark.b_mark != 0)
                    bookmark = "しおり" + infoBookmark.b_mark + "話";
                else
                    bookmark = "ブックマーク";
            }
        }
        ((TextView) itemView.findViewById(R.id.textStat)).setText(bookmark);

        if(novelInfo instanceof NovelDB.NovelInfoRanking){
            ((TextView)itemView.findViewById(R.id.textRank)).setText(((NovelDB.NovelInfoRanking)novelInfo).ranking_index+"位");
        }
        else
            ((TextView)itemView.findViewById(R.id.textRank)).setText("");
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //positionから必要なデータをビューに設定する
        NovelInfo novelInfo = mNovelInfo.get(position);
        String ncode = novelInfo.ncode;



        holder.itemView.setTag(R.layout.item_title,position);

        //ノベル情報の読み出し
        NovelSeries novelSeries = null;
        if(mNovelSeries != null)
            novelSeries = mNovelSeries.get(ncode);
        setInfoToItem(holder.itemView,novelInfo,novelSeries,mInfo);

        CheckBox checkBox = (CheckBox)holder.itemView.findViewById(R.id.checkBox);
        checkBox.setChecked(mCheckSet.contains(ncode));
        checkBox.setTag(ncode);


    }

    @Override
    public int getItemCount() {
        if(mNovelInfo == null)
            return 0;
        return mNovelInfo.size();
    }



    public void setValues(List<? extends NovelInfo> values){
        mNovelInfo = values;
    }
    public void setNovelSeries(Map<String,NovelSeries> novelSeries){
        mNovelSeries = novelSeries;
    }
    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.item_title);
            NovelInfo info = mNovelInfo.get(pos);
            mListener.onItemClick(info);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.item_title);
            NovelInfo info = mNovelInfo.get(pos);
            mListener.onItemLongClick(info);
        }
        return false;
    }
    public Set<String> getChecks(){
        return mCheckSet;
    }
    public void showInfo(boolean flag){
        mInfo = flag;
    }
}
