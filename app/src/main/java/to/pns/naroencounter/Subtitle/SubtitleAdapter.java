package to.pns.naroencounter.Subtitle;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import to.pns.naroencounter.R;
import to.pns.naroencounter.Titles.TitleAdapter;
import to.pns.naroencounter.data.NovelInfo;
import to.pns.naroencounter.data.NovelSeries;
import to.pns.naroencounter.data.NovelSubTitle;

/**
 リサイクルビューに使用するデータ関連づけ用アダプター
 */

public class SubtitleAdapter extends RecyclerView.Adapter implements View.OnClickListener {


    private String mNCode;
    private int mSort;
    private NovelInfo mNovelInfo;
    private View mInfoView;
    private NovelSeries mSeries;

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
        NumberFormat nf = NumberFormat.getNumberInstance();
        if(position == 0) {
            viewInfo.setVisibility(View.VISIBLE);
            viewSub.setVisibility(View.GONE);

            if(mInfoView == null)
                mInfoView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_title, null, false);
            if(mInfoView.getParent() != null)
                ((ViewGroup)mInfoView.getParent()).removeView(mInfoView);
            viewInfo.addView(mInfoView);
            holder.itemView.setTag(R.layout.item_title,-1);
            TitleAdapter.setInfoToItem(mInfoView,mNovelInfo,mSeries,true);
        }
        else{
            viewSub.setVisibility(View.VISIBLE);
            viewInfo.setVisibility(View.GONE);
            if(viewInfo.getChildCount() > 0) {
                viewInfo.removeAllViews();
            }
            //positionから必要なデータをビューに設定する
            int pos;
            if(mSort == 0)
                pos = position;
            else
                pos = mValues.size()-position+1;


            NovelSubTitle v = mValues.get(pos-1);

            holder.itemView.setTag(R.layout.item_title,pos-1);
            ((TextView)holder.itemView.findViewById(R.id.textNo)).setText(""+pos);
            ((TextView)holder.itemView.findViewById(R.id.textTitle)).setText(v.title);

            String dateString = sdf.format(v.date);
            ((TextView)holder.itemView.findViewById(R.id.textRegDate)).setText(dateString);
            ((TextView)holder.itemView.findViewById(R.id.textUpdate)).setText(v.update!=null?sdf.format(v.update):"");
            ((TextView)holder.itemView.findViewById(R.id.textAlreadyDate)).setText(v.readDate!=null?sdf.format(v.readDate):"");
            ((TextView)holder.itemView.findViewById(R.id.textRecvDate)).setText(v.contentDate!=null?sdf.format(v.contentDate):"");

            if(v.originalSize != 0 && v.compressionSize!=0){
                ((TextView)holder.itemView.findViewById(R.id.textSize)).setText(
                        String.format("%.1fKB / %.1fKB",v.compressionSize/1024.0f,v.originalSize/1024.0f));
                ((TextView)holder.itemView.findViewById(R.id.textCompress)).setText(
                        String.format("%d%%",v.compressionSize*100/v.originalSize));
            }else{
                ((TextView)holder.itemView.findViewById(R.id.textSize)).setText("");
                ((TextView)holder.itemView.findViewById(R.id.textCompress)).setText("");
            }


        }


    }

    @Override
    public int getItemCount() {
        if(mValues == null)
            return 0;
        return mValues.size()+1;
    }

    public void setValues(String ncode, NovelInfo info, List<NovelSubTitle> values, NovelSeries series) {
        mNCode = ncode;
        mNovelInfo = info;
        mValues = values;
        mSeries = series;
    }


    @Override
    public void onClick(View view) {
        if(mListener != null) {
            int pos = (int) view.getTag(R.layout.item_title);
            if(pos >= 0)
                mListener.onItemClick(mValues.get(pos).index);
        }
    }
}
