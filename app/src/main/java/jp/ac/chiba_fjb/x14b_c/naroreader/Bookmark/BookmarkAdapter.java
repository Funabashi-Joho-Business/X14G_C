package jp.ac.chiba_fjb.x14b_c.naroreader.Bookmark;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;

/**
リサイクルビューに使用するデータ関連づけ用アダプター
 */

public class BookmarkAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    public interface OnItemClickListener{
        public void onItemClick(NovelBookmark bookmark);
        public void onItemLongClick(NovelBookmark bookmark);
    }
    private OnItemClickListener mListener;
    private List<NovelBookmark> mBookmarks;
    private Map<String,NovelInfo> mNovelInfos;
    void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //レイアウトを設定
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new RecyclerView.ViewHolder(view){}; //本当はここでアイテム設定を実装するのだけれど、簡単にするためスルー
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //positionから必要なデータをビューに設定する

        NovelBookmark b = mBookmarks.get(position);
        String dateString = new SimpleDateFormat("yyyy年MM月dd日(E)").format(b.getUpdate().getTime());
        holder.itemView.setTag(R.layout.bookmark_item,position);
       // ((TextView)holder.itemView.findViewById(R.id.textCode)).setText(b.getCode());
//        ((TextView)holder.itemView.findViewById(R.id.textGenre)).setText(""+b.getCategory());
        ((TextView)holder.itemView.findViewById(R.id.textDate)).setText(dateString);
        ((TextView)holder.itemView.findViewById(R.id.textTitle)).setText(b.getName());

        //ノベル情報の読み出し
        NovelInfo novelInfo = mNovelInfos.get(b.getCode().toUpperCase());
        if(novelInfo != null){
            ((TextView)holder.itemView.findViewById(R.id.textWritter)).setText(novelInfo.writer);
            ((TextView)holder.itemView.findViewById(R.id.textPoint)).setText(NumberFormat.getNumberInstance().format(novelInfo.all_point)+"pt");
            ((TextView)holder.itemView.findViewById(R.id.textCount)).setText(novelInfo.general_all_no+"話");
        }else{
            ((TextView)holder.itemView.findViewById(R.id.textWritter)).setText("");
            ((TextView)holder.itemView.findViewById(R.id.textPoint)).setText("pt");
            ((TextView)holder.itemView.findViewById(R.id.textCount)).setText("話");
        }
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
    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.bookmark_item);
            NovelBookmark bookmark = mBookmarks.get(pos);
            mListener.onItemClick(bookmark);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.bookmark_item);
            NovelBookmark bookmark = mBookmarks.get(pos);
            mListener.onItemLongClick(bookmark);
        }
        return false;
    }
}
