package jp.co.lhe.lhememberapp.ui.views;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.ui.events.OnCalledViewCloseEvent;
import jp.co.lhe.lhememberapp.ui.events.OnMenuTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnNoticeItemTapEvent;
import jp.co.lhe.lhememberapp.ui.models.NoticeModel;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.utils.DateUtil;
import jp.funnelpush.sdk.FunnelPush;
import jp.funnelpush.sdk.callback.OnMessagesDetailApiListener;
import jp.funnelpush.sdk.response.MessagesResponse;

/**
 * お知らせ一覧用のListAdapter
 */

public class NoticeListAdapter extends ArrayAdapter<NoticeModel> {
    private LayoutInflater mInflater;
    private Context mContext;
    private int mResourceId;
    private List<NoticeModel> mData;
    private ProgressBar mProgressBar;

    public NoticeListAdapter(Context context, int resource, List objects, ProgressBar progressBar) {
        super(context, resource, objects);
        initComponent(context, resource, objects, progressBar);

    }

    private void initComponent(Context context, int resource, List objects, ProgressBar progressBar) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mResourceId = resource;
        this.mData = objects;
        this.mProgressBar = progressBar;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null ||  ((ViewHolder) convertView.getTag() != null && !((ViewHolder) convertView.getTag()).model.equals(mData.get(position)))) {
            convertView = this.mInflater.inflate(this.mResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.component_notice_item_title);
            holder.bodyView = (TextView) convertView.findViewById(R.id.component_notice_item_body);
            holder.sendDateView = (TextView) convertView.findViewById(R.id.component_notice_item_date);
            holder.noticeCellView = (RelativeLayout) convertView.findViewById(R.id.component_notice_list_cell);
            holder.arrowIconButton = (ImageButton) convertView.findViewById(R.id.component_notice_arrow_btn);
            holder.blankView = (FrameLayout) convertView.findViewById(R.id.component_notice_blank_blobk);
            holder.model = mData.get(position);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final NoticeModel model = mData.get(position);

        if (model.isNoticeItem && model.messageSummary != null) {
            if (model.messageSummary.getId() != null && !model.messageSummary.getId().isEmpty()) {
                //お知らせ
                //お知らせアイテム
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.messageSummary != null) {
                            if (model.cellSubMenuUrl != "") {
                                return;
                            }

                            //お知らせ
                            FunnelPush.getMessagesDetail(mContext.getApplicationContext(), model.messageSummary.getId(), new OnMessagesDetailApiListener() {
                                @Override
                                public void onSuccessGetMessageDetail(MessagesResponse messagesResponse) {
                                    mProgressBar.setVisibility(View.GONE);

                                    //お知らせ詳細表示
                                    //HomeActivityへ発火
                                    EventBusHolder.EVENT_BUS.post(new OnNoticeItemTapEvent(messagesResponse));
                                }

                                @Override
                                public void onFailGetMessageDetail(String s, int i) {
                                    mProgressBar.setVisibility(View.GONE);
                                    //ログイン画面へ遷移
                                    new AlertDialog.Builder(mContext)
                                            .setTitle("確認")
                                            .setMessage("お知らせの取得に失敗しました。")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();

                                }
                            });
                            mProgressBar.setVisibility(View.VISIBLE);
                        }

                    }

                });


                //お知らせ
                holder.bodyView.setText(model.messageSummary.getTitle());

                //エポック秒なのでエポックミリ秒にする
                holder.sendDateView.setText(DateUtil.convertSendDate((long) model.messageSummary.getDeliverAt() * 1000));
                holder.sendDateView.setVisibility(View.VISIBLE);

                if (!model.messageSummary.isRead()) {
                    //未読のお知らせを太文字で表示する
                    holder.bodyView.setTypeface(Typeface.DEFAULT_BOLD);
                    holder.sendDateView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    holder.bodyView.setTextColor(ContextCompat.getColor(mContext, R.color.noticeAlreadyRead));
                    holder.sendDateView.setTextColor(ContextCompat.getColor(mContext, R.color.noticeAlreadyRead));
                }
                holder.arrowIconButton.setVisibility(View.VISIBLE);
                holder.bodyView.setVisibility(View.VISIBLE);
                holder.noticeCellView.setVisibility(View.VISIBLE);

            } else {
                //お知らせ０件
                holder.bodyView.setText(model.messageSummary.getBody());
                holder.bodyView.setGravity(Gravity.CENTER_HORIZONTAL);
                holder.bodyView.setVisibility(View.VISIBLE);
                holder.noticeCellView.setVisibility(View.VISIBLE);
                holder.blankView.setVisibility(View.VISIBLE);
            }


        } else if (model.isNoticeItem && model.messageSummary == null) {
            //その他のお知らせ
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = "";
                    url = model.cellSubMenuUrl;
                    if (url != "") {
                        //HomeActivityへ発火
                        EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(url, true, OnCalledViewCloseEvent.SCREEN_NOTICE));
                    }
                }
            });
            holder.bodyView.setText(model.cellMenuSubTitle);
            holder.arrowIconButton.setVisibility(View.VISIBLE);
            holder.bodyView.setVisibility(View.VISIBLE);
            holder.noticeCellView.setVisibility(View.VISIBLE);
        } else {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    return;
                }
            });
            holder.titleView.setText(model.cellTitle);
            holder.titleView.setTextColor(ContextCompat.getColor(mContext,R.color.colorAccent));
            holder.titleView.setVisibility(View.VISIBLE);

        }



        return convertView;
    }

    class ViewHolder {
        TextView titleView;
        TextView bodyView;
        TextView sendDateView;
        RelativeLayout noticeCellView;
        ImageButton arrowIconButton;
        FrameLayout blankView;
        NoticeModel model;
    }
}
