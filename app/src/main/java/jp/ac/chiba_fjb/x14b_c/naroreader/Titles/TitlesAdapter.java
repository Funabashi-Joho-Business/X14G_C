package jp.ac.chiba_fjb.x14b_c.naroreader.Titles;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;

/**
リサイクルビューに使用するデータ関連づけ用アダプター
 */

public class TitlesAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {

    public interface OnItemClickListener{
        public void onItemClick(Map<String,String> value);
        public void onItemLongClick(Map<String,String> value);
    }
    private OnItemClickListener mListener;
    private List<Map<String,String>> mValues;
    void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //レイアウトを設定
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.titles_item, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new RecyclerView.ViewHolder(view){}; //本当はここでアイテム設定を実装するのだけれど、簡単にするためスルー
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //positionから必要なデータをビューに設定する

        Map<String,String> value = mValues.get(position);
        String dateString = "";
        if(value.get("general_lastup")!=null) {
            Date date = java.sql.Timestamp.valueOf(value.get("general_lastup"));
            dateString = new SimpleDateFormat("yyyy年MM月dd日").format(date);
        }
        holder.itemView.setTag(R.layout.titles_item,position);  //現在位置の設定
        ((TextView)holder.itemView.findViewById(R.id.textCode)).setText(value.get("ncode"));
        ((TextView)holder.itemView.findViewById(R.id.textTitle)).setText(value.get("title")!=null?value.get("title"):"");
        ((TextView)holder.itemView.findViewById(R.id.textDate)).setText(dateString);
    }

    @Override
    public int getItemCount() {
        if(mValues == null)
            return 0;
        return mValues.size();
    }



    public void setValues(List<Map<String,String>> values){
        mValues = values;
    }

    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.titles_item);
            Map<String,String> value = mValues.get(pos);
            mListener.onItemClick(value);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.titles_item);
            Map<String,String> value = mValues.get(pos);
            mListener.onItemLongClick(value);
        }
        return false;
    }
}
