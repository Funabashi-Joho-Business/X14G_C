package jp.ac.chiba_fjb.x14b_c.naroreader.Bookmark;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelBookmark;

/**
リサイクルビューに使用するデータ関連づけ用アダプター
 */

public class BookmarkAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    public interface OnItemClickListener{
        public void onItemClick(NovelBookmark bookmark);
    }
    private OnItemClickListener mListener;
    private List<NovelBookmark> mBookmarks;
    void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //レイアウトを設定
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        view.setOnClickListener(this);
        return new RecyclerView.ViewHolder(view){}; //本当はここでアイテム設定を実装するのだけれど、簡単にするためスルー
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //positionから必要なデータをビューに設定する

        NovelBookmark b = mBookmarks.get(position);
        String dateString = new SimpleDateFormat("yyyy年MM月dd日").format(b.getUpdate().getTime());
        holder.itemView.setTag(R.layout.bookmark_item,position);
        ((TextView)holder.itemView.findViewById(R.id.textView)).setText(b.getCode());
        ((TextView)holder.itemView.findViewById(R.id.textView2)).setText(""+b.getCategory());
        ((TextView)holder.itemView.findViewById(R.id.textView3)).setText(dateString);
        ((TextView)holder.itemView.findViewById(R.id.textView4)).setText(b.getName());
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

    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.bookmark_item);
            NovelBookmark bookmark = mBookmarks.get(pos);
            mListener.onItemClick(bookmark);
        }
    }
}
