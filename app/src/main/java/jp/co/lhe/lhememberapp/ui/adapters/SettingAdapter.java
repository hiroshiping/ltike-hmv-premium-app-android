package jp.co.lhe.lhememberapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.ui.models.MenuItem;
import jp.co.lhe.lhememberapp.utils.ScreenUtil;

/**
 * Created by lhedev on 2018/03/05.
 */

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.MenuVh> {

    public interface OnItemClickListener {
        public void onClick(int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }

    List<MenuItem> mItems;

    public SettingAdapter(List<MenuItem> items) {
        mItems = items;
    }

    @Override
    public MenuVh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MenuVh(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_setting, parent, false));
    }

    @Override
    public void onBindViewHolder(MenuVh holder, int position) {
        MenuItem menuItem = mItems.get(position);
        holder.iv.setImageResource(menuItem.getIcon());
        holder.iv.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class MenuVh extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView iv;

        public MenuVh(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.getLayoutParams().height= (ScreenUtil.getScreenWidth()- 2* LMApplication.getApplication().getResources().getDimensionPixelSize(R.dimen.menu_margin))/4;
            itemView.getLayoutParams().width= (ScreenUtil.getScreenWidth()- 2*LMApplication.getApplication().getResources().getDimensionPixelSize(R.dimen.menu_margin))/4;
        }

        @OnClick(R.id.iv_icon)
        public void OnItemClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClick((Integer) view.getTag());
            }
        }
    }
}
