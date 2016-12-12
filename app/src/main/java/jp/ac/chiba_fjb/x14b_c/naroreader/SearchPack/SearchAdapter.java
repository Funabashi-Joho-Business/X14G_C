package jp.ac.chiba_fjb.x14b_c.naroreader.SearchPack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSearch;

/**
 * Created by x14g019 on 2016/11/08.
 */

public class SearchAdapter extends RecyclerView.Adapter implements View.OnClickListener,View.OnLongClickListener{

    public interface OnItemClickListener{
        public void onItemClick(NovelSearch value);
        public void onItemLongClick(NovelSearch item);
    }
    void setOnItemClickListener(OnItemClickListener listener){mListener = listener;}

    private List<NovelSearch> mSearch;
    private OnItemClickListener mListener;


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //レイアウトを設定
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new RecyclerView.ViewHolder(view){}; //本当はここでアイテム設定を実装するのだけれど、簡単にするためスルー
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder,int position) {
        //positionから必要なデータをビューに設定する

        NovelSearch s = mSearch.get(position+1);        //position＝番地
        String dateString = new SimpleDateFormat("yyyy年MM月dd日(E)").format(s.novelupdated_at);
        holder.itemView.setTag(R.layout.item_search,position);
        ((TextView) holder.itemView.findViewById(R.id.textView)).setText(s.ncode);
        ((TextView) holder.itemView.findViewById(R.id.textView2)).setText(""+s.genre);
        ((TextView) holder.itemView.findViewById(R.id.textView3)).setText(dateString);
        ((TextView) holder.itemView.findViewById(R.id.textView4)).setText(s.title);
    }

    @Override
    public int getItemCount () {
        if (mSearch == null)
            return 0;
        return mSearch.size()-1;  //初回起動時はnullで帰ってくる
    }

    public void setSearch(List<NovelSearch> bookmarks){
            mSearch = bookmarks;
        }

    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.item_history);
            NovelSearch value = mSearch.get(pos);   //エラーだったらNovelSearchをMap<String,String>にすること
            mListener.onItemClick(value);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.item_search);
            NovelSearch item = mSearch.get(pos);
            mListener.onItemLongClick(item);
        }
        return false;
    }

}
