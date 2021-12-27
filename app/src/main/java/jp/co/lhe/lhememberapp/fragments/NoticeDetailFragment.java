package jp.co.lhe.lhememberapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.ui.events.OnCalledViewCloseEvent;
import jp.co.lhe.lhememberapp.ui.events.OnMenuTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnNoticeDetailCloseTapEvent;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.utils.DateUtil;
import jp.funnelpush.sdk.response.MessagesResponse;

/**
 * お知らせ詳細画面
 */
public class NoticeDetailFragment extends BaseFragment {
    private static final String ARG_PARAM_NOTICE_BODY = "param_notice_body";
    private static final String ARG_PARAM_NOTICE_TITLE = "param_notice_title";
    private static final String ARG_PARAM_NOTICE_SEND_DATE = "param_notice_send_date";
    private static final String ARG_PARAM_NOTICE_ID = "param_notice_id";
    private static final String ARG_PARAM_NOTICE_URL = "param_notice_url";

    private String mTitle = "";
    private String mBody = "";
    private String mSendDate = "";
    private String mId = "";
    private String mUrl = "";

    public NoticeDetailFragment() {
        // Required empty public constructor
    }

    /**
     * お知らせ詳細画面fragment
     * @param messagesResponse お知らせ詳細情報
     * @return fragment
     */
    public static NoticeDetailFragment newInstance(MessagesResponse messagesResponse) {
        NoticeDetailFragment fragment = new NoticeDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_NOTICE_ID, messagesResponse.getMessage().getId());
        args.putString(ARG_PARAM_NOTICE_TITLE, messagesResponse.getMessage().getTitle());
        args.putString(ARG_PARAM_NOTICE_BODY, messagesResponse.getMessage().getBody());
        args.putString(ARG_PARAM_NOTICE_SEND_DATE, DateUtil.convertSendDate((long)messagesResponse.getMessage().getDeliverAt()*1000));
        args.putString(ARG_PARAM_NOTICE_URL, messagesResponse.getMessage().getUrl());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_PARAM_NOTICE_TITLE);
            mBody = getArguments().getString(ARG_PARAM_NOTICE_BODY);
            mSendDate = getArguments().getString(ARG_PARAM_NOTICE_SEND_DATE);
            mId = getArguments().getString(ARG_PARAM_NOTICE_ID);
            mUrl = getArguments().getString(ARG_PARAM_NOTICE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notice_detail, container, false);
        ButterKnife.bind(this,view);
        TextView sendDateTextView = (TextView) view.findViewById(R.id.fragment_notice_detail_send_date);
        sendDateTextView.setText(mSendDate);
        TextView titleTextView = (TextView) view.findViewById(R.id.fragment_notice_detail_title);
        titleTextView.setText(mTitle);
        TextView bodyTextView = (TextView) view.findViewById(R.id.fragment_notice_detail_body);
        bodyTextView.setText(mBody);
        TextView urlLinkView = (TextView) view.findViewById(R.id.fragment_notice_detail_url);
        urlLinkView.setText(mUrl);
        urlLinkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //WebViewでURLのページを表示する
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(mUrl, true, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @OnClick(R.id.iv_back)
    public void onBackClick(){
        EventBusHolder.EVENT_BUS.post(new OnNoticeDetailCloseTapEvent(""));
    }
}
