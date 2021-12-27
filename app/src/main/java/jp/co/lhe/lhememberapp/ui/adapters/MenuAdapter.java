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

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuVh> {
    public interface OnItemClickListener {
        public void onClick(MenuItem menuItem);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }

    List<MenuItem> mItems;

    public MenuAdapter(List<MenuItem> items) {
        mItems = items;
    }

    @Override
    public MenuVh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MenuVh(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(MenuVh holder, int position) {
        MenuItem menuItem = mItems.get(position);
        holder.iv.setImageResource(menuItem.getIcon());
        holder.iv.setTag(menuItem);
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
            itemView.getLayoutParams().height= (ScreenUtil.getScreenWidth()- 2*LMApplication.getApplication().getResources().getDimensionPixelSize(R.dimen.menu_margin))/3;
            itemView.getLayoutParams().width= (ScreenUtil.getScreenWidth()- 2*LMApplication.getApplication().getResources().getDimensionPixelSize(R.dimen.menu_margin))/3;
        }

        @OnClick(R.id.iv_icon)
        public void OnItemClick(View view){
            if (mOnItemClickListener!=null){
                mOnItemClickListener.onClick((MenuItem) view.getTag());
            }
        }
    }
}
