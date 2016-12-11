package jp.ac.chiba_fjb.x14b_c.naroreader.Subtitle;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import jp.ac.chiba_fjb.x14b_c.naroreader.R;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelInfo;
import jp.ac.chiba_fjb.x14b_c.naroreader.data.NovelSubTitle;

/**
 リサイクルビューに使用するデータ関連づけ用アダプター
 */

public class SubtitleAdapter extends RecyclerView.Adapter implements View.OnClickListener {


    private String mNCode;
    private int mSort;
    private NovelInfo mNovelInfo;
    private View mInfoView;

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
        ViewGroup viewInfo = (ViewGroup) holder.itemView.findViewById(R.id.layoutInfo);
        ViewGroup viewSub =  (ViewGroup) holder.itemView.findViewById(R.id.layoutSubtitle);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd(E)");

        if(position == 0) {
            viewInfo.setVisibility(View.VISIBLE);
            viewSub.setVisibility(View.GONE);

            if(mInfoView == null)
                mInfoView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_info, null, false);
            if(mInfoView.getParent() != null)
                ((ViewGroup)mInfoView.getParent()).removeView(mInfoView);
            viewInfo.addView(mInfoView);

            ((TextView)mInfoView.findViewById(R.id.textTitle)).setText(mNovelInfo.title);
            ((TextView)mInfoView.findViewById(R.id.textInfo)).setText(mNovelInfo.story);
            ((TextView)mInfoView.findViewById(R.id.textDate)).setText(sdf.format(mNovelInfo.updated_at));
            ((TextView)mInfoView.findViewById(R.id.textWritter)).setText(mNovelInfo.writer);
            ((TextView)mInfoView.findViewById(R.id.textPoint)).setText(mNovelInfo.global_point+"pt");
        }
        else{
            if(viewInfo.getChildCount() > 0)
                viewInfo.removeAllViews();

            //positionから必要なデータをビューに設定する
            int pos;
            if(mSort == 0)
                pos = position;
            else
                pos = mValues.size()-position+1;


            NovelSubTitle v = mValues.get(pos-1);

            holder.itemView.findViewById(R.id.layoutSubtitle).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.layoutInfo).setVisibility(View.GONE);

            holder.itemView.setTag(R.layout.item_bookmark,pos-1);
            ((TextView)holder.itemView.findViewById(R.id.textNo)).setText(""+pos);
            ((TextView)holder.itemView.findViewById(R.id.textTitle)).setText(v.title);


            String dateString = sdf.format(v.date);
            if (v.update != null)
                dateString += "  改稿"+sdf.format(v.update);
            ((TextView)holder.itemView.findViewById(R.id.textDate)).setText(dateString);
        }


    }

    @Override
    public int getItemCount() {
        if(mValues == null)
            return 0;
        return mValues.size()+1;
    }

    public void setValues(String ncode, NovelInfo info, List<NovelSubTitle> values) {
        mNCode = ncode;
        mNovelInfo = info;
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
