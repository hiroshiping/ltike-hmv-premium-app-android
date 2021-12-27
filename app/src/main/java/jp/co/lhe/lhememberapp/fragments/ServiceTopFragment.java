package jp.co.lhe.lhememberapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.fragments.carousel.CarouselView;
import jp.co.lhe.lhememberapp.network.models.AppTopModel;
import jp.co.lhe.lhememberapp.network.models.EventPopup;
import jp.co.lhe.lhememberapp.network.models.LayoutModel;
import jp.co.lhe.lhememberapp.network.models.TopicsColumnModel;
import jp.co.lhe.lhememberapp.network.models.TopicsContentsModel;
import jp.co.lhe.lhememberapp.network.models.TopicsRowModel;
import jp.co.lhe.lhememberapp.ui.events.OnCalledViewCloseEvent;
import jp.co.lhe.lhememberapp.ui.events.OnMenuTapEvent;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.ui.views.BlueClickableSpan;
import jp.co.lhe.lhememberapp.ui.views.CustomerTagHandler;
import jp.co.lhe.lhememberapp.ui.views.HtmlParser;
import jp.co.lhe.lhememberapp.ui.views.PopUpAlertDialog;
import jp.co.lhe.lhememberapp.utils.SharedPrefsUtils;

/**
 * サービストップFragment
 */
public class ServiceTopFragment extends BaseFragment {
    private static final String TAG = "ServiceTopFragment";

    private static final double IMAGE_SIZE_RATE = 1.3;

    private OnFragmentInteractionListener mListener;

    private static final String ARGS_PUSH_PARAM_URL = "pushParamURL";
    private static final String DESPLAY_WIDTH = "width";
    private static final String DESPLAY_HEIGHT = "height";
    private String mPushParamUrl = "";
    private int width;
    private int height;

    public ServiceTopFragment() {
    }

    public static ServiceTopFragment newInstance(String pushParamUrl, int wedth, int height) {
        ServiceTopFragment fragment = new ServiceTopFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_PUSH_PARAM_URL, pushParamUrl);
        args.putInt(DESPLAY_WIDTH, wedth);
        args.putInt(DESPLAY_HEIGHT, height);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service_top, container, false);
        if (getArguments() != null) {
            mPushParamUrl = getArguments().getString(ARGS_PUSH_PARAM_URL);
            width         = getArguments().getInt(DESPLAY_WIDTH);
            height        = getArguments().getInt(DESPLAY_HEIGHT);
        }
        Log.d(getClass().getSimpleName(), "view size: width=" + width + ", height=" + height);

        //  レイアウトの設定
        int MP = ViewGroup.LayoutParams.MATCH_PARENT;
        int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

        //レイアウトJSONのデータを取得
        final AppTopModel model = ((LMApplication)getActivity().getApplicationContext()).getLmaSharedPreference().getAppTopJson();
        if(model != null) {
            EventPopup eventPopup = model.eventPopup;
            if(eventPopup != null) {
                if(mEventPopup == null) {
                    mEventPopup = new PopUpAlertDialog(getActivity());
                }
                mEventPopup.initVew(eventPopup.banner_img, eventPopup.button_img, eventPopup.href, eventPopup.disableShowOnce, eventPopup.view, eventPopup.sso);
            }
        }
        //スクロール内のレイアウト
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.fragment_servicetop_layout);

        ImageView leftIcon = (ImageView) view.findViewById(R.id.logo_image);
        ViewGroup.LayoutParams iconParams = leftIcon.getLayoutParams();

        iconParams.width = height / 20;
        iconParams.height = height / 20;

        leftIcon.setLayoutParams(iconParams);

        /**
         *  ８個のメニューボタン
         **/
        //チケット先行抽選
        ImageButton menuTicketBtn = (ImageButton) view.findViewById(R.id.menu_ticket_button);

        ViewGroup.LayoutParams buttonParams = menuTicketBtn.getLayoutParams();
        ViewGroup.MarginLayoutParams mbuttonParams = (ViewGroup.MarginLayoutParams)buttonParams;

        mbuttonParams.setMargins(0,0,1,1);
        buttonParams.height = height / 11;

        menuTicketBtn.setLayoutParams(buttonParams);
        menuTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.ticketLucky.href, model.ticketLucky.sso, model.ticketLucky.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));

            }
        });
        //レジャー施設割引
        ImageButton menuLeisureDiscountBtn = (ImageButton) view.findViewById(R.id.menu_leisurediscount_button);
        menuLeisureDiscountBtn.setLayoutParams(buttonParams);
        menuLeisureDiscountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.ticketLeisure.href, model.ticketLeisure.sso, model.ticketLeisure.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));

            }
        });
        //映画館割引
        ImageButton menuMovieDiscountBtn = (ImageButton) view.findViewById(R.id.menu_moviediscount_button);
        menuMovieDiscountBtn.setLayoutParams(buttonParams);
        menuMovieDiscountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.ticketMovie.href, model.ticketMovie.sso, model.ticketMovie.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));

            }
        });
        //安心サポート
        ImageButton menuSupportBtn = (ImageButton) view.findViewById(R.id.menu_support_button);
        menuSupportBtn.setLayoutParams(buttonParams);
        menuSupportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.ticketInsurance.href, model.ticketInsurance.sso, model.ticketInsurance.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));

            }
        });
        //映画・ドラマ
        ImageButton menuMovieBtn = (ImageButton) view.findViewById(R.id.menu_movie_button);
        menuMovieBtn.setLayoutParams(buttonParams);
        menuMovieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.serviceVideo.href, model.serviceVideo.sso, model.serviceVideo.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
            }
        });
        //雑誌
        ImageButton menuMagazineBtn = (ImageButton) view.findViewById(R.id.menu_magazine_button);
        menuMagazineBtn.setLayoutParams(buttonParams);
        menuMagazineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火　タブホ起動判断
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.serviceMagazine.href, model.serviceMagazine.sso, model.serviceMagazine.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW, true));
            }
        });
        //500円分クーポン
        ImageButton menuCouponBtn = (ImageButton) view.findViewById(R.id.menu_coupon_button);
        menuCouponBtn.setLayoutParams(buttonParams);
        menuCouponBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.coupon.href, model.coupon.sso, model.coupon.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
            }
        });
        //プレゼント
        ImageButton menuPresentBtn = (ImageButton) view.findViewById(R.id.menu_present_button);
        menuPresentBtn.setLayoutParams(buttonParams);
        menuPresentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.present.href, model.present.sso, model.present.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
            }
        });

        //トピックス
        for(int i=0; i<model.topics.contents.size(); i++){
            TopicsContentsModel contentsModel = model.topics.contents.get(i);
            switch (contentsModel.type){

                //カルーセル
                case "0":
                    layout.addView(showTopicsCarousel(container, contentsModel));
                    break;

                //カラム１
                case "1":
                    layout.addView(showTopicsOneColumn(inflater, container, contentsModel), createParam(MP,WC));
                    break;

                //カラム２
                case "2":
                    layout.addView(showTopicsTwoColumn(inflater, container, contentsModel), createParam(MP,WC));
                    break;
                case "3":
                    View htmlView = LayoutInflater.from(getActivity()).inflate(R.layout.rv_item_html, null);
                    final TextView tv = (TextView) htmlView.findViewById(R.id.tv_html);
                    if (contentsModel == null || contentsModel.rows==null || contentsModel.rows.size()==0 ||contentsModel.rows.get(0).columns == null ||contentsModel.rows.get(0).columns.size() == 0){
                        break;
                    }

                    final TopicsColumnModel columnModel =contentsModel.rows.get(0).columns.get(0);

                    if(columnModel.description == null || columnModel.description.size() == 0){
                        break;
                    }

                    Spanned sp = HtmlParser.buildSpannedText(columnModel.description.get(0), new CustomerTagHandler());
                    SpannableStringBuilder spannableStringBuilder = setTextLinkOpenByWebView(getActivity(), sp,columnModel.view);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(spannableStringBuilder);

                    layout.addView(htmlView);
                    break;
            }
        }

        return view;
    }

    private PopUpAlertDialog mEventPopup;
    @Override
    public void onResume() {
        super.onResume();
        if (mPushParamUrl != null && !mPushParamUrl.isEmpty()) {
            //URLのページをWebViewで表示
            Log.d(getClass().getSimpleName(), "▲▼▲▼▲▼▲▼▲ url is " + mPushParamUrl);
            //HomeActivityへ発火
            EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(mPushParamUrl, true, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
        }
        Logger.d("fragment load finish");
        if(mEventPopup != null){
            mEventPopup.show();
        }
    }

    @Override
    public String getTitle() {
        return "top";
    }

    /**
     * addView時のサイズ設定
     * @param w　横
     * @param h　縦
     * @return :LinearLayoutのサイズ返却
     */
    private LinearLayout.LayoutParams createParam(int w, int h){
        return new LinearLayout.LayoutParams(w, h);
    }

    public  SpannableStringBuilder setTextLinkOpenByWebView(final Context context, Spanned htmlString, String view) {
        if (htmlString != null) {
            if (htmlString instanceof SpannableStringBuilder) {
                SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) htmlString;
                if ("0".equals(view)){
                    // 取得与a标签相关的Span
                    Object[] objs = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), URLSpan.class);
                    if (null != objs && objs.length != 0) {
                        for (Object obj : objs) {
                            int start = spannableStringBuilder.getSpanStart(obj);
                            int end = spannableStringBuilder.getSpanEnd(obj);
                            if (obj instanceof URLSpan) {
                                //先移除这个Span，再新添加一个自己实现的Span。
                                URLSpan span = (URLSpan) obj;
                                final String url = span.getURL();
                                spannableStringBuilder.removeSpan(obj);
                                spannableStringBuilder.setSpan(new BlueClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        Logger.d(url);
                                        EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(url, false, "0", OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
                                    }
                                }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }
                }
                return spannableStringBuilder;
            }
        }
        return null;
    }

    /**
     * カルーセルの設定。
     *
     * @param container
     * @param contentsModel
     * @return :component_topics_carousel.xml
     */
    private View showTopicsCarousel(ViewGroup container, TopicsContentsModel contentsModel) {
        Log.d(TAG, "contentsModel: " + contentsModel);
        if(contentsModel == null){
            return null;
        }

        CarouselView carouselView = new CarouselView(container.getContext());
        carouselView.updateCarouselData(contentsModel, width);
        return carouselView;
    }

    /**
     * カラム１の設定
     * @param inflater
     * @param container
     * @param contentsModel
     * @return :component_topics_one_column.xml
     */
    private View showTopicsOneColumn(LayoutInflater inflater, ViewGroup container, TopicsContentsModel contentsModel) {
        final TopicsColumnModel columnModel = contentsModel.rows.get(0).columns.get(0);
        View contentsView = inflater.inflate(R.layout.component_topics_one_column, container, false);

        // 画像の設定
        ImageView columnImage = (ImageView) contentsView.findViewById(R.id.onecolumn_image);
        columnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(columnModel.href, columnModel.sso, columnModel.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
            }
        });

        changeWidthMatchParentAndShowPicture(columnImage, contentsView, columnModel.src);

        return contentsView;
    }

    private void changeWidthMatchParentAndShowPicture(ImageView columnImage, View parent, String url) {
        //show view
        Glide.with(parent).load(url).into(columnImage);

        //set width match parent
        ViewGroup.LayoutParams params = columnImage.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        columnImage.setLayoutParams(params);
    }

    /**
     * カラム２の設定
     * @param inflater
     * @param container
     * @param contentsModel
     * @return :component_topics_two_column.xml
     */
    private View showTopicsTwoColumn(LayoutInflater inflater, ViewGroup container, TopicsContentsModel contentsModel) {
        TopicsRowModel rowModel = contentsModel.rows.get(0);
        View contentsView = inflater.inflate(R.layout.component_topics_two_column, container, false);

        // 左の画像
        ImageView columnImage1 = (ImageView) contentsView.findViewById(R.id.twocolumn_image1);
        final TopicsColumnModel columnModel1 = rowModel.columns.get(0);
        columnImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(columnModel1.href, columnModel1.sso, columnModel1.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
            }
        });
        changeWidthMatchParentAndShowPicture(columnImage1, contentsView, columnModel1.src);

        // 右の画像
        ImageView columnImage2 = (ImageView) contentsView.findViewById(R.id.twocolumn_image2);
        final TopicsColumnModel columnModel2 = rowModel.columns.get(1);
        columnImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(columnModel2.href, columnModel2.sso, columnModel2.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
            }
        });
        changeWidthMatchParentAndShowPicture(columnImage2, contentsView, columnModel2.src);

        return contentsView;
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

    @Override
    public void onPause() {
        super.onPause();
        // ログイン実行しない、Fragment切り替えする
        SharedPrefsUtils.setBooleanPreference(getActivity(), PopUpAlertDialog.POPUP_LOGIN_START, false);
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
}
