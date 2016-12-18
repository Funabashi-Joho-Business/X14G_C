package jp.ac.chiba_fjb.x14b_c.naroreader.History;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;

/**
リサイクルビューに使用するデータ関連づけ用アダプター
 */

public class HistoryAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {



    public interface OnItemClickListener{
        public void onItemClick(NovelInfo value);
        public void onItemLongClick(NovelInfo value);
        public void onItemCheck();
    }
    private OnItemClickListener mListener;
    private List<NovelInfo> mValues;
    void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
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

        NovelInfo novelInfo = mValues.get(position);


        holder.itemView.setTag(R.layout.item_title,position);  //現在位置の設定

        NumberFormat nf = NumberFormat.getNumberInstance();
        String dateString = new SimpleDateFormat("yyyy年MM月dd日(E)").format(novelInfo.general_lastup.getTime());
        ((TextView)holder.itemView.findViewById(R.id.textDate)).setText(dateString);
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

        CheckBox checkBox = (CheckBox)holder.itemView.findViewById(R.id.checkBox);
        checkBox.setChecked(mCheckSet.contains(novelInfo.ncode));
        checkBox.setTag(novelInfo.ncode);
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
            int pos = (int) view.getTag(R.layout.item_title);
            NovelInfo value = mValues.get(pos);
            mListener.onItemClick(value);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.item_title);
            NovelInfo value = mValues.get(pos);
            mListener.onItemLongClick(value);
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        String ncode = (String)compoundButton.getTag();
        if(b) {
            mCheckSet.add(ncode);
            mListener.onItemCheck();
        }
        else
            mCheckSet.remove(ncode);
    }
    public Set<String> getChecks(){
        return mCheckSet;
    }
}
