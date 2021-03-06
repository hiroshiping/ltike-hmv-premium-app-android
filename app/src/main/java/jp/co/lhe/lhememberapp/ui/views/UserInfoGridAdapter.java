package jp.co.lhe.lhememberapp.ui.views;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.ui.models.QuestionItemModel;

public class UserInfoGridAdapter extends BaseAdapter {

    private List<QuestionItemModel> mItemList;
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean mIsPrefecture;

    public UserInfoGridAdapter(Context context, boolean isPrefecture) {
        mContext = context;
        mIsPrefecture = isPrefecture;
        mInflater = LayoutInflater.from(context);
    }

    public void setAdapter(List<QuestionItemModel> items) {
        mItemList = items;
    }

    public List<QuestionItemModel> getAdapterItemList() {
        return mItemList;
    }
    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mItemList.get(position).id);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final UserInfoGridAdapter.ViewHolder viewHolder;
        final ImageView checkBoxBtn;
        if (convertView == null) {
            if (mIsPrefecture) {
                convertView = mInflater.inflate(R.layout.item_user_info_grid_cell, null);
            } else {
                convertView = mInflater.inflate(R.layout.item_user_info_grid_cell_s, null);
            }
            viewHolder = new UserInfoGridAdapter.ViewHolder();
            viewHolder.itemDto = mItemList.get(position);
            RelativeLayout checkBox = (RelativeLayout) convertView.findViewById(R.id.checkBox);
            TextView checkBoxTxt = (TextView) checkBox.findViewById(R.id.check_box_label);
            checkBoxTxt.setText(viewHolder.itemDto.label);
            checkBoxBtn = (ImageView) checkBox.findViewById(R.id.check_box_button);
            checkBoxBtn.setBackground(setCheckBoxFace(viewHolder.itemDto.selected));
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (UserInfoGridAdapter.ViewHolder) convertView.getTag();
            RelativeLayout checkBox = (RelativeLayout) convertView.findViewById(R.id.checkBox);
            TextView checkBoxTxt = (TextView) checkBox.findViewById(R.id.check_box_label);
            checkBoxTxt.setText(viewHolder.itemDto.label);
            checkBoxBtn = (ImageView) checkBox.findViewById(R.id.check_box_button);
            checkBoxBtn.setBackground(setCheckBoxFace(viewHolder.itemDto.selected));
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.itemDto.selected = !viewHolder.itemDto.selected;
                if (!mIsPrefecture && mContext.getString(R.string.fav_service_id_win_rate_up).equals(viewHolder.itemDto.id) && viewHolder.itemDto.selected) {
                    //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    String savedPayStatus = ((LMApplication)mContext.getApplicationContext()).getLmaSharedPreference().getPayStatus();
                    String contractCourse = ((LMApplication)mContext.getApplicationContext()).getLmaSharedPreference().getContractCourse();
                    if ("1".equals(savedPayStatus) && "0".equals(contractCourse)) {
                        //??????????????????????????????????????????1??????????????????????????????0???????????????
                        new AlertDialog.Builder(mContext)
                                .setTitle("??????")
                                .setMessage("??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????")
                                .setCancelable(false)
                                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                }
                checkBoxBtn.setBackground(setCheckBoxFace(viewHolder.itemDto.selected));
            }
        });


        return convertView;
    }

    private Drawable setCheckBoxFace(boolean selected) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if (selected) {
                return mContext.getDrawable(R.drawable.checkbox_marked);
            } else {
                return mContext.getDrawable(R.drawable.checkbox_blank_outline);
            }
        }
        else {
            if (selected) {
                return mContext.getResources().getDrawable(R.drawable.checkbox_marked);
            } else {
                return mContext.getResources().getDrawable(R.drawable.checkbox_blank_outline);
            }
        }

    }

    static class ViewHolder {
        QuestionItemModel itemDto;
    }


}
