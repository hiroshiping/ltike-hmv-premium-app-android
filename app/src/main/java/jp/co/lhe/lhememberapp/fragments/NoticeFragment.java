package jp.co.lhe.lhememberapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.lhe.lhememberapp.Constants;
import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.network.models.ToastInfo;
import jp.co.lhe.lhememberapp.ui.events.OnRefreshNoticeBadgeEvent;
import jp.co.lhe.lhememberapp.ui.models.NoticeModel;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.ui.views.NoticeListAdapter;
import jp.co.lhe.lhememberapp.utils.DateUtil;
import jp.funnelpush.sdk.FunnelPush;
import jp.funnelpush.sdk.callback.OnMessagesListApiListener;
import jp.funnelpush.sdk.response.MessagesListResponse;

//import android.support.v7.widget.LinearLayoutManager;

/**
 * お知らせ一覧Fragment
 */
public class NoticeFragment extends BaseFragment{
    private static final String ARG_PARAM_URL = "notification_url";
    private static final String ARG_PARAM_SSO = "notification_need_sso";

    private ListView mNoticeListView = null;
    private NoticeListAdapter mNoticeListAdapter = null;

    private ProgressBar mProgressBar;

    private String mParamNotificationHref;

    private int mMaxRowCount;

    private OnFragmentInteractionListener mListener;
    private List<NoticeModel> noticeList;

    public NoticeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment NoticeFragment.
     */
    public static NoticeFragment newInstance(String param1, boolean param2) {
        NoticeFragment fragment = new NoticeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_URL, param1);
        args.putBoolean(ARG_PARAM_SSO, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParamNotificationHref = getArguments().getString(ARG_PARAM_URL);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.content_web_view_progress_bar);

        mNoticeListView = (ListView) view.findViewById(R.id.fragment_notice_list);
        noticeList = new ArrayList<>();
        mNoticeListAdapter = new NoticeListAdapter(getActivity(), R.layout.component_notice_list_item, noticeList, mProgressBar);
        mNoticeListView.setAdapter(mNoticeListAdapter);

        mMaxRowCount = ((LMApplication)getActivity().getApplicationContext()).getLmaSharedPreference().getNotificationCount();

        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNetToast(ToastInfo.getTimeInfo());
                            showEmpty();
                        }
                    });
                }
            }
        };
        timer.schedule(task, 1000l*Constants.TIME_OUT);

        //お知らせ件数バッジの件数を更新
        //HomeActivityへ発火
        EventBusHolder.EVENT_BUS.post(new OnRefreshNoticeBadgeEvent(""));


        //メッセージ一覧の情報取得
        FunnelPush.getMessagesList(getActivity().getApplicationContext(), new OnMessagesListApiListener() {
            @Override
            public void onSuccessGetMessagesList(MessagesListResponse messagesListResponse) {
                mProgressBar.setVisibility(View.GONE);
                timer.cancel();
                setNoticeList(messagesListResponse.getMessageList());
            }

            @Override
            public void onFailGetMessagesList(String s, int i) {
                timer.cancel();
                mProgressBar.setVisibility(View.GONE);
            }
        });



        return view;

    }
    private void showNetToast(ToastInfo toastInfo) {
        Logger.d("showNetToast");
        mProgressBar.setVisibility(View.GONE);
        new AlertDialog.Builder(getActivity())
                .setTitle(toastInfo.getTitle())
                .setMessage(toastInfo.getMessage())
                .setCancelable(false)
                .setNegativeButton(getString(R.string.dialog_close2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
        return;
    }

    private void showEmpty(){
        NoticeModel noticeCell = new NoticeModel();
        noticeCell.cellTitle = "お知らせ一覧";
        noticeCell.messageSummary = null;
        noticeList.add(noticeCell);

        // お知らせが１件もなかった場合
        NoticeModel data = new NoticeModel();
        data.cellTitle = "";
        data.isNoticeItem = true;
        data.cellSubMenuUrl = "";
        data.messageSummary = new MessagesListResponse.MessageSummary();
        data.messageSummary.setBody("現在表示するお知らせはありません。");
        noticeList.add(data);

        NoticeModel otherCell = new NoticeModel();
        otherCell.cellTitle = "その他";
        otherCell.messageSummary = null;
        noticeList.add(otherCell);

        // お知らせが１件もなかった場合
        NoticeModel data2 = new NoticeModel();
        data2.cellTitle = "";
        data2.isNoticeItem = true;
        data2.cellSubMenuUrl = "";
        data2.messageSummary = new MessagesListResponse.MessageSummary();
        data2.messageSummary.setBody("現在表示するお知らせはありません。");
        noticeList.add(data2);

    }
    /**
     * お知らせ一覧をセットします。
     * @param messageList
     */
    private void setNoticeList(List<MessagesListResponse.MessageSummary> messageList) {
        noticeList.clear();
        NoticeModel noticeCell = new NoticeModel();
        noticeCell.cellTitle = "お知らせ一覧";
        noticeCell.messageSummary = null;
        noticeList.add(noticeCell);

        if (messageList.size() > 0) {
            int size = messageList.size();

            if (messageList.size() >= mMaxRowCount) {
                size = mMaxRowCount;
            }

            for (int i = 0; i < size; i++) {
                NoticeModel data = new NoticeModel();
                data.cellTitle = "";
                data.isNoticeItem = true;
                data.messageSummary = messageList.get(i);
                noticeList.add(data);
            }
        } else {
            // お知らせが１件もなかった場合
            NoticeModel data = new NoticeModel();
            data.cellTitle = "";
            data.isNoticeItem = true;
            data.cellSubMenuUrl = "";
            data.messageSummary = new MessagesListResponse.MessageSummary();
            data.messageSummary.setBody("現在表示するお知らせはありません。");
            noticeList.add(data);

        }

        NoticeModel otherCell = new NoticeModel();
        otherCell.cellTitle = "その他";
        otherCell.messageSummary = null;
        noticeList.add(otherCell);
        NoticeModel otherSubCell = new NoticeModel();
        otherSubCell.cellMenuSubTitle = "チケットに関するお知らせ";
        otherSubCell.cellSubMenuUrl = mParamNotificationHref;
        otherSubCell.isNoticeItem = true;
        otherSubCell.messageSummary = null;
        noticeList.add(otherSubCell);
        mNoticeListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("fragment load finish");
    }

    @Override
    public void onPause() {
        super.onPause();
        //バックグラウンドへ行く前に最終操作時間を保存
        DateUtil.setLastOperationOnWebViewTime(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }

    @Override
    public String getTitle() {
        return getString(R.string.notice_title);
    }

}
