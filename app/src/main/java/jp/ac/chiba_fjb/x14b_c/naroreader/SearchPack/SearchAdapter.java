package jp.ac.chiba_fjb.x14b_c.naroreader.SearchPack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;

/**
 * Created by x14g019 on 2016/11/08.
 */

public class SearchAdapter extends RecyclerView.Adapter{
    private NovelInfo[] mSearch;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent,int viewType) {
        //レイアウトを設定
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        return new RecyclerView.ViewHolder(view) {
        }; //本当はここでアイテム設定を実装するのだけれど、簡単にするためスルー
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder,int position) {
        //positionから必要なデータをビューに設定する

        NovelInfo s = mSearch[position+1];        //position＝番地
        String dateString = new SimpleDateFormat("yyyy年MM月dd日").format(s.general_lastup);

        ((TextView) holder.itemView.findViewById(R.id.textView)).setText(s.ncode);
        ((TextView) holder.itemView.findViewById(R.id.textView2)).setText("" + s.general_lastup);
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


}
