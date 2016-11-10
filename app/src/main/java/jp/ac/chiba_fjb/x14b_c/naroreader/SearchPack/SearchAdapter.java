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

public class SearchAdapter extends RecyclerView.Adapter{
    private List<NovelSearch> mSearch;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent,int viewType) {
        //レイアウトを設定
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        return new RecyclerView.ViewHolder(view) {
        }; //本当はここでアイテム設定を実装するのだけれど、簡単にするためスルー
    }

        @Override
        public void onBindViewHolder (RecyclerView.ViewHolder holder,int position){
            //positionから必要なデータをビューに設定する

            NovelSearch s = mSearch.get(position);
            String dateString = new SimpleDateFormat("yyyy年MM月dd日").format(s.getUpdate().getTime());

            ((TextView) holder.itemView.findViewById(R.id.textView)).setText(s.getCode());
            ((TextView) holder.itemView.findViewById(R.id.textView2)).setText("" + s.getCategory());
            ((TextView) holder.itemView.findViewById(R.id.textView3)).setText(dateString);
            ((TextView) holder.itemView.findViewById(R.id.textView4)).setText(s.getName());
        }

        @Override
        public int getItemCount () {
            if (mSearch == null)
                return 0;
            return mSearch.size();
        }

        public void setSearch(List<NovelSearch> bookmarks){
            mSearch = bookmarks;
        }
}
