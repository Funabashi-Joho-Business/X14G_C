package jp.ac.chiba_fjb.x14b_c.naroreader.History;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;

/**
リサイクルビューに使用するデータ関連づけ用アダプター
 */

public class HistoryAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    public interface OnItemClickListener{
        public void onItemClick(NovelInfo value);
        public void onItemLongClick(NovelInfo value);
    }
    private OnItemClickListener mListener;
    private List<NovelInfo> mValues;
    void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //レイアウトを設定
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new RecyclerView.ViewHolder(view){}; //本当はここでアイテム設定を実装するのだけれど、簡単にするためスルー
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //positionから必要なデータをビューに設定する

        NovelInfo value = mValues.get(position);
        String dateString = "";
        if(value.general_lastup != null) {
            Date date = value.general_lastup;
            dateString = new SimpleDateFormat("yyyy年MM月dd日").format(date);
        }
        holder.itemView.setTag(R.layout.item_history,position);  //現在位置の設定
        ((TextView)holder.itemView.findViewById(R.id.textCode)).setText(value.ncode);
        ((TextView)holder.itemView.findViewById(R.id.textTitle)).setText(value.title!=null?value.title:"");
        ((TextView)holder.itemView.findViewById(R.id.textDate)).setText(dateString);
    }

    @Override
    public int getItemCount() {
        if(mValues == null)
            return 0;
        return mValues.size();
    }



    public void setValues(List<NovelInfo> values){
        mValues = values;
    }

    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.item_history);
            NovelInfo value = mValues.get(pos);
            mListener.onItemClick(value);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.item_history);
            NovelInfo value = mValues.get(pos);
            mListener.onItemLongClick(value);
        }
        return false;
    }
}
