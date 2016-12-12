package jp.ac.chiba_fjb.x14b_c.naroreader.Ranking;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelRanking;

/**
リサイクルビューに使用するデータ関連づけ用アダプター
 */

public class RankingAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    public interface OnItemClickListener{
        public void onItemClick(NovelRanking item);
        public void onItemLongClick(NovelRanking item);
    }
    void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    private OnItemClickListener mListener;
    private List<NovelRanking> mValues;


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //レイアウトを設定
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new RecyclerView.ViewHolder(view){}; //本当はここでアイテム設定を実装するのだけれど、簡単にするためスルー
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //positionから必要なデータをビューに設定する

        NovelRanking b = mValues.get(position);
        String dateString = new SimpleDateFormat("yyyy年MM月dd日").format(b.update);
        holder.itemView.setTag(R.layout.ranking_item,position);
        ((TextView)holder.itemView.findViewById(R.id.textRank)).setText((position+1)+"位");
        ((TextView)holder.itemView.findViewById(R.id.textCode)).setText(b.ncode+1);
        ((TextView)holder.itemView.findViewById(R.id.textGenre)).setText(""+b.genre);
        ((TextView)holder.itemView.findViewById(R.id.textCount)).setText(b.novelCount+"話");
        ((TextView)holder.itemView.findViewById(R.id.textPoint)).setText(NumberFormat.getNumberInstance().format(b.point)+"pt");
        ((TextView)holder.itemView.findViewById(R.id.textWritter)).setText(""+b.writerName);
        ((TextView)holder.itemView.findViewById(R.id.textDate)).setText(dateString);
        ((TextView)holder.itemView.findViewById(R.id.textTitle)).setText(b.title);
        ((TextView)holder.itemView.findViewById(R.id.textInfo)).setText(b.info);
    }

    @Override
    public int getItemCount() {
        if(mValues == null)
            return 0;
        return mValues.size();
    }



    public void setRanking(List<NovelRanking> ranking){
        mValues = ranking;
    }

    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.ranking_item);
            NovelRanking item = mValues.get(pos);
            mListener.onItemClick(item);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.ranking_item);
            NovelRanking item = mValues.get(pos);
            mListener.onItemLongClick(item);
        }
        return false;
    }

}
