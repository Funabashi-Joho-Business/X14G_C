package jp.ac.chiba_fjb.x14b_c.naroreader.Bookmark;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.chiba_fjb.x14b_c.naroreader.History.HistoryFragment;
import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSeries;

/**
リサイクルビューに使用するデータ関連づけ用アダプター
 */

public class BookmarkAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {



    public void setOnItemClickListener(BookmarkFragment listener) {
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
        mListener.onItemCheck();
    }

    public interface OnItemClickListener{
        public void onItemClick(NovelBookmark bookmark);
        public void onItemLongClick(NovelBookmark bookmark);
        public void onItemCheck();
    }
    private OnItemClickListener mListener;
    private List<NovelBookmark> mBookmarks;
    private Map<String,NovelInfo> mNovelInfos;
    private Map<String, NovelSeries> mNovelSeries;
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //positionから必要なデータをビューに設定する

        NovelBookmark b = mBookmarks.get(position);


        NumberFormat nf = NumberFormat.getNumberInstance();
        String dateString = new SimpleDateFormat("yyyy年MM月dd日(E)").format(b.getUpdate().getTime());
        holder.itemView.setTag(R.layout.item_title,position);
        ((TextView)holder.itemView.findViewById(R.id.textDate)).setText(dateString);
        //ノベル情報の読み出し
        String ncode = b.getCode().toUpperCase();
        NovelInfo novelInfo = mNovelInfos.get(ncode);
        NovelSeries novelSeries = mNovelSeries.get(ncode);
        if(novelInfo != null){
            ((TextView)holder.itemView.findViewById(R.id.textTitle)).setText(novelInfo.title);
            ((TextView)holder.itemView.findViewById(R.id.textWritter)).setText(novelInfo.writer);
            ((TextView)holder.itemView.findViewById(R.id.textPoint)).setText(nf.format(novelInfo.global_point));
            ((TextView)holder.itemView.findViewById(R.id.textCount)).setText(nf.format(novelInfo.general_all_no));
            ((TextView)holder.itemView.findViewById(R.id.textBookmark)).setText(nf.format(novelInfo.fav_novel_cnt));
            ((TextView)holder.itemView.findViewById(R.id.textEva)).setText(
                    novelInfo.review_cnt==0?"0":String.format("%1.1f",(float)novelInfo.all_point/novelInfo.all_hyoka_cnt/2));
            ((TextView)holder.itemView.findViewById(R.id.textEvaCount)).setText(nf.format(novelInfo.all_hyoka_cnt));
            ((TextView)holder.itemView.findViewById(R.id.textReview)).setText(nf.format(novelInfo.review_cnt));
            ((TextView)holder.itemView.findViewById(R.id.textLength)).setText(nf.format(novelInfo.length));

            if(novelSeries!=null){
                ((TextView)holder.itemView.findViewById(R.id.textSeries)).setText(novelSeries.title);
            }
            else
                ((TextView)holder.itemView.findViewById(R.id.textSeries)).setText("");

        }else{
            ((TextView)holder.itemView.findViewById(R.id.textTitle)).setText("");
            ((TextView)holder.itemView.findViewById(R.id.textWritter)).setText("");
            ((TextView)holder.itemView.findViewById(R.id.textPoint)).setText("");
            ((TextView)holder.itemView.findViewById(R.id.textCount)).setText("");
            ((TextView)holder.itemView.findViewById(R.id.textBookmark)).setText("");
            ((TextView)holder.itemView.findViewById(R.id.textEva)).setText("");
            ((TextView)holder.itemView.findViewById(R.id.textEvaCount)).setText("");
            ((TextView)holder.itemView.findViewById(R.id.textReview)).setText("");
            ((TextView)holder.itemView.findViewById(R.id.textLength)).setText("");
            ((TextView)holder.itemView.findViewById(R.id.textSeries)).setText("");
        }

        CheckBox checkBox = (CheckBox)holder.itemView.findViewById(R.id.checkBox);
        checkBox.setChecked(mCheckSet.contains(ncode));
        checkBox.setTag(ncode);
    }

    @Override
    public int getItemCount() {
        if(mBookmarks == null)
            return 0;
        return mBookmarks.size();
    }



    public void setBookmarks(List<NovelBookmark> bookmarks){
        mBookmarks = bookmarks;
    }
    public void setNovelInfos(Map<String,NovelInfo> novelInfos){
        mNovelInfos = novelInfos;
    }
    public void setNovelSeries(Map<String,NovelSeries> novelSeries){
        mNovelSeries = novelSeries;
    }
    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.item_title);
            NovelBookmark bookmark = mBookmarks.get(pos);
            mListener.onItemClick(bookmark);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.item_title);
            NovelBookmark bookmark = mBookmarks.get(pos);
            mListener.onItemLongClick(bookmark);
        }
        return false;
    }
    public Set<String> getChecks(){
        return mCheckSet;
    }
}
