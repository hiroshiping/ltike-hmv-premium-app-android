package jp.co.lhe.lhememberapp.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.text.ParseException;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.ui.events.OnCalledViewCloseEvent;
import jp.co.lhe.lhememberapp.ui.events.OnMenuTapEvent;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.utils.DateUtil;
import jp.co.lhe.lhememberapp.utils.LogUtil;
import jp.co.lhe.lhememberapp.utils.SharedPrefsUtils;
import jp.co.lhe.lhememberapp.utils.StringUtils;

public class PopUpAlertDialog extends Dialog {
    public static final String POPUP_IS_CLOSE = "POPUP_IS_CLOSE";
    public static final String POPUP_CONFIRM_TIME = "POPUP_CONFIRM_TIME";
    public static final String POPUP_LOGIN_START = "POPUP_LOGIN_START";
    private Context mContext;
    private PopUpAlertDialog mDialog = this;
    private View popupView;
    private ImageView ivContent;
    private ImageButton ibClose;
    private ImageView ivConfirm;

    private boolean isPrepared = false;
    private boolean isClose = false;
    private boolean isTimeout = false;
    private boolean disableShowOnce = false;

    public PopUpAlertDialog(Context context) {
        super(context, R.style.PopupDialog);
        mContext = context;
        popupView = LayoutInflater.from(context)
                .inflate(R.layout.component_popup_dialog, null);
        ivContent = popupView.findViewById(R.id.iv_content);
        ivConfirm = popupView.findViewById(R.id.iv_confirm);
        popupView.setBackgroundResource(android.R.color.transparent);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        double size = (isTablet(context.getApplicationContext()) ? 0.6 : 0.75);
        int width = (int) (dm.widthPixels * size);
        int height = (int) (width * 1.4);
        int imgHeight = (int) ((height-30*dm.density) * 10 / 12);
        int btnHeight = (int) (height * 2 / 12);

        LogUtil.logDebug( "initPopupVew: width=" + width+ ", height=" + height);
        LogUtil.logDebug( "initPopupVew: imgHeight=" + imgHeight+ ", btnHeight=" + btnHeight);
        ivContent.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, imgHeight));
        ivConfirm.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, btnHeight));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
        lp.gravity = Gravity.CENTER;
        setContentView(popupView, lp);
        setCancelable(false);
    }

    public void initVew(String urlBanner, String urlButton, final String href, boolean disableShowOnce, final String view, boolean sso){
        LogUtil.logDebug( "initPopupVew: banner=" + urlBanner+ ", button=" + urlButton+ ", href=" + href+ ", showOnce"+ !disableShowOnce+ ", view=" + view);
        this.disableShowOnce = disableShowOnce;
        if(StringUtils.isNullOrEmpty(urlBanner) || StringUtils.isNullOrEmpty(href) ||
            StringUtils.isNullOrEmpty(view) || StringUtils.isNullOrEmpty(urlButton)){
            isPrepared = false;
            return;
        }
        Glide.with(popupView)
                .load(urlBanner)
                .into(((ImageView)popupView.findViewById(R.id.iv_content)));
        Glide.with(popupView)
                .load(urlButton)
                .into(((ImageView)popupView.findViewById(R.id.iv_confirm)));
        popupView.findViewById(R.id.ib_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefsUtils.setBooleanPreference(mContext, POPUP_IS_CLOSE, true);
                SharedPrefsUtils.setLongPreference(mContext, POPUP_CONFIRM_TIME, System.currentTimeMillis());
                dismiss();
            }
        });
        popupView.findViewById(R.id.iv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefsUtils.setBooleanPreference(mContext, POPUP_IS_CLOSE, true);
                SharedPrefsUtils.setLongPreference(mContext, POPUP_CONFIRM_TIME, System.currentTimeMillis());
                dismiss();
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(
                        href,
                        sso,
                        view,
                        OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
            }
        });
        isPrepared = true;
    }

    public void show(){
        long confirm = SharedPrefsUtils.getLongPreference(mContext, POPUP_CONFIRM_TIME, -1);
        isClose = SharedPrefsUtils.getBooleanPreference(mContext, POPUP_IS_CLOSE, false);
        boolean isLoginStart = SharedPrefsUtils.getBooleanPreference(mContext, POPUP_LOGIN_START, false);
        //バックグラウンドから復帰した時にwebview表示中だった場合、タイムアウトチェックを行う
        long timeoutMinutes = ((LMApplication) mContext.getApplicationContext()).getLmaSharedPreference().getOperationTimeOutTime();
        long lastOperationTime = ((LMApplication) mContext.getApplicationContext()).getLmaSharedPreference().getLastOperationTime();
        LogUtil.logDebug("show: timeoutMinutes=" + timeoutMinutes+ ", lastOperationTime=" + lastOperationTime);
        try {
            // 当日２３：５９：５９後、ポップアップ再表示する
            if(isClose){
                if(confirm > 0 && !DateUtils.isToday(confirm)){
                    isClose = false;
                }
            }
            LogUtil.logDebug("show: isClose=" + isClose);
            // 一定時間操作がなかった場合は ポップアップが表示されない
            if(lastOperationTime>0 && DateUtil.isTimeOver(timeoutMinutes, lastOperationTime)
                    && !DateUtils.isToday(confirm)){
                isTimeout = true;
            }else{
                isTimeout = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            isClose = true;
        }finally {
            if(isPrepared ) {
                if(disableShowOnce) {
                    LogUtil.logDebug( "show: isLoginStart=" + isLoginStart+ ", isTimeout=" + isTimeout);
                    // ポップアップ表示する制限なし
                    if(isLoginStart && !isTimeout) {
                        super.show();
                    }
                } else {
                    LogUtil.logDebug("show: isClose=" + isClose+ ", isTimeout=" + isTimeout);
                    // ポップアップ一日一回のみ表示する
                    if (!isClose && !isTimeout) {
                        super.show();
                    }
                }
            }
        }
    }

    private boolean isTablet(Context context){
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        float width = display.widthPixels;
        float height = display.heightPixels;
        double x = Math.pow(width/display.xdpi, 2);
        double y = Math.pow(height/display.ydpi, 2);
        double inch = Math.sqrt(x + y);
        Log.d(getClass().getSimpleName(), "the screen is inch "+ inch);

        boolean isTablet = (context.getResources().getConfiguration().screenLayout& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        Log.d(getClass().getSimpleName(), "the screen is tablet "+ isTablet);
        if(isTablet || inch > 6){
            return true;
        }
        return false;
    }
}
