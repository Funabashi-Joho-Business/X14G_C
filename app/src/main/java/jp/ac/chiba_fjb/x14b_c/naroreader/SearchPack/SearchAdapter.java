package jp.ac.chiba_fjb.x14b_c.naroreader.SearchPack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.Titles.TitlesAdapter;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;

import static android.R.attr.value;

/**
 * Created by x14g019 on 2016/11/08.
 */

public class SearchAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    public interface OnItemClickListener{
        public void onItemClick(NovelInfo value);
    }
    private OnItemClickListener mListener;
    void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    private NovelInfo[] mSearch;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent,int viewType) {
        //レイアウトを設定
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        view.setOnClickListener(this);
        return new RecyclerView.ViewHolder(view) {}; //本当はここでアイテム設定を実装するのだけれど、簡単にするためスルー
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder,int position) {
        //positionから必要なデータをビューに設定する

        NovelInfo s = mSearch[position+1];        //position＝番地
        String dateString = new SimpleDateFormat("yyyy年MM月dd日").format(s.general_lastup);

        holder.itemView.setTag(R.layout.search_item,position);  //現在位置の設定
        ((TextView) holder.itemView.findViewById(R.id.textView)).setText(s.ncode);
        ((TextView) holder.itemView.findViewById(R.id.textView2)).setText(""+s.genre);
        ((TextView) holder.itemView.findViewById(R.id.textView3)).setText(dateString);
        ((TextView) holder.itemView.findViewById(R.id.textView4)).setText(s.title);
    }

    @Override
    public int getItemCount () {
        if (mSearch == null)
            return 0;
        return mSearch.length-1;  //初回起動時はnullで帰ってくる
    }

    public void setSearch(NovelInfo[] bookmarks){
            mSearch = bookmarks;
        }

    public void setValues(NovelInfo[] values){
        mSearch = values;
    }

    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.search_item);
            NovelInfo value = mSearch[pos];
            mListener.onItemClick(value);
        }
    }

}
