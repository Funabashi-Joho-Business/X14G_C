package jp.ac.chiba_fjb.x14b_c.naroreader.Subtitle;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSubTitle;

/**
 リサイクルビューに使用するデータ関連づけ用アダプター
 */

public class SubtitleAdapter extends RecyclerView.Adapter implements View.OnClickListener {


    private String mNCode;
    private int mSort;
    public void setSort(int v) {
        mSort = v;
    }

    public interface OnItemClickListener{
        public void onItemClick(int value);
    }
    private OnItemClickListener mListener;
    private List<NovelSubTitle> mValues;
    void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //レイアウトを設定
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtitles, parent, false);
        view.setOnClickListener(this);
        return new RecyclerView.ViewHolder(view){}; //本当はここでアイテム設定を実装するのだけれど、簡単にするためスルー
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //positionから必要なデータをビューに設定する
        int pos;
        if(mSort == 0)
            pos = position;
        else
            pos = mValues.size()-position-1;


        NovelSubTitle v = mValues.get(pos);

        holder.itemView.setTag(R.layout.item_bookmark,pos);
        ((TextView)holder.itemView.findViewById(R.id.textTitle)).setText(v.title);

        String dateString;
        if (v.update == null){
            dateString = v.date.toString();
        } else {
            dateString = v.update.toString();
        }
        ((TextView)holder.itemView.findViewById(R.id.textDate)).setText(dateString);
        ((TextView)holder.itemView.findViewById(R.id.textNo)).setText(""+(pos+1));
    }

    @Override
    public int getItemCount() {
        if(mValues == null)
            return 0;
        return mValues.size();
    }

    public void setValues(String ncode, List<NovelSubTitle> values) {
        mNCode = ncode;
        mValues = values;
    }


    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.item_bookmark);
            mListener.onItemClick(mValues.get(pos).index);
        }
    }
}
