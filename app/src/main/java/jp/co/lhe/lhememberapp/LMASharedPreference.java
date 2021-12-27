package jp.co.lhe.lhememberapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.google.gson.Gson;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import jp.co.lhe.lhememberapp.network.models.AppHelpModel;
import jp.co.lhe.lhememberapp.network.models.AppLoginModel;
import jp.co.lhe.lhememberapp.network.models.AppSettingsModel;
import jp.co.lhe.lhememberapp.network.models.AppTopModel;
import jp.co.lhe.lhememberapp.network.models.EventPopup;
import jp.co.lhe.lhememberapp.network.models.LayoutModel;
import jp.co.lhe.lhememberapp.network.models.LayoutSettingModel;
import jp.co.lhe.lhememberapp.network.models.TopicsModel;
import jp.co.lhe.lhememberapp.network.models.LatestAppUpdateModel;
import jp.co.lhe.lhememberapp.network.models.UpdateAndroidModel;
import jp.co.lhe.lhememberapp.ui.views.PopUpAlertDialog;
import jp.co.lhe.lhememberapp.utils.EncryptUtil;
import jp.co.lhe.lhememberapp.utils.StringUtils;

/**
 * PremiumMember APP用SharedPreference
 */
public class LMASharedPreference {

    /**
     * デバッグログ用タグ
     */
    private final String TAG = getClass().getSimpleName();
    /**
     * コンテキスト
     */
    private Context mContext;
    /**
     * プリファレンス
     */
    private SharedPreferences mSharedPreference;

    /**
     * 配信を許可
     * ※端末の「通知を表示」とは同期しません
     */
    private final String FLG_Notification = "notificationFlg";

    /**
     * サウンドを再生
     */
    private final String FLG_SOUND = "soundFlg";

    /**
     * バイブレーション
     */
    private final String FLG_Vibration = "vibrationFlg";

    /**
     * 最後にアプリを操作した時間
     */
    private final String LAST_OPERATION_TIME = "lastOperationTime";
    /**
     * 最後にWebViewを操作した時間
     */
    private final String LAST_OPERATION_ON_WEB_VIEW_TIME = "lastOperationOnWebViewTime";

    /**
     * アンケート回答（都道府県）
     */
    private final String QUESTION_PREFECTURES = "questionPrefectures";
    /**
     * アンケート回答（利用したいサービス）
     */
    private final String QUESTION_SERVICES = "questionServices";

    /**
     * account managerの権限が付与されていない場合のアカウント情報保存用キー
     */
    private final String ACCOUNT_ID = "accountEmail";
    private final String ACCOUNT_PW = "accountPw";
    private final String ACCOUNT_LCUS_NO = "accountLcusNo";

    /**
     * アンケート登録画面で「登録」または「後で登録」を選択したかどうか
     */
    private final String FLG_QUESTIONNAIRE_DONE = "questionnaireDone";

    /**
     * アプリログイン済フラグ
     */
    private final String FLG_APP_ALREADY_LOGIN = "alreadyLoggedInFlg";


    /**
     * constoructor
     * @param mContext
     */
    public LMASharedPreference(Context mContext) {
        this.mContext = mContext;
        mSharedPreference = mContext.getSharedPreferences(
            mContext.getApplicationContext().getString(R.string.const_shared_preference_name),
            Activity.MODE_PRIVATE);
    }

    /**
     * [account managerの権限が付与されていない場合のアカウント情報管理]
     * アカウントIDを取得します。
     * @return アカウントID(emailアドレス)
     */
    public String getAccountId() {
        return mSharedPreference.getString(ACCOUNT_ID, "");
    }

    /**
     * [account managerの権限が付与されていない場合のアカウント情報管理]
     * パスワードを取得します。
     * @return パスワード
     */
    public String getAccountPw() {
        return mSharedPreference.getString(ACCOUNT_PW, "");
    }

    /**
     * [account managerの権限が付与されていない場合のアカウント情報管理]
     * アカウントIDを保存します。
     * @param accountId
     */
    public void setAccountId(String accountId) {
        mSharedPreference.edit().putString(ACCOUNT_ID, accountId).commit();
    }

    /**
     * [account managerの権限が付与されていない場合のアカウント情報管理]
     * パスワードを保存します。
     * @param accountPw
     */
    public void setAccountPw(String accountPw) {
        mSharedPreference.edit().putString(ACCOUNT_PW, accountPw).commit();
    }

    /**
     * [account managerの権限が付与されていない場合のアカウント情報管理]
     * ログイン情報を削除します。
     */
    public void removeAccountInfo() {
        mSharedPreference.edit().remove(ACCOUNT_ID).commit();
        mSharedPreference.edit().remove(ACCOUNT_PW).commit();
    }

    /**
     * 有料区分、契約コースを保存します。
     * @param payStatus
     * @param contractCourse
     */
    public void setLoginAccountInfo(String payStatus, String contractCourse) {
        mSharedPreference.edit().putString(mContext.getString(R.string.account_pay_status), payStatus).commit();
        mSharedPreference.edit().putString(mContext.getString(R.string.account_contract_course), contractCourse).commit();
    }

    /**
     * 有料区分を取得します。
     * @return 有料区分
     */
    public String getPayStatus() {
        return mSharedPreference.getString(mContext.getString(R.string.account_pay_status), "");
    }

    /**
     * 契約コースを取得します。
     * @return 契約コース
     */
    public String getContractCourse() {
        return mSharedPreference.getString(mContext.getString(R.string.account_contract_course), "");
    }

    /**
     * 保存されている有料区分、契約コースを削除します。
     */
    public void removeLoginAccountInfo() {
        mSharedPreference.edit().remove(mContext.getString(R.string.account_pay_status)).commit();
        mSharedPreference.edit().remove(mContext.getString(R.string.account_contract_course)).commit();
    }
    /**
     * アプリログイン済フラグを保存します。
     * @param flg
     */
    public void setAppLoggedInFlg(boolean flg) {
        mSharedPreference.edit().putBoolean(FLG_APP_ALREADY_LOGIN, flg).commit();
    }

    /**
     * アプリログイン済フラグを取得します。
     * @return
     */
    public boolean getAppLoggedInFlg() {
        return mSharedPreference.getBoolean(FLG_APP_ALREADY_LOGIN, false);
    }

    /**
     * アンケート登録画面で「登録」または「後で登録」を選択したかどうかを設定します。
     * @param flg
     */
    public void setQuestionDone(boolean flg) {
        mSharedPreference.edit().putBoolean(FLG_QUESTIONNAIRE_DONE, flg).commit();
    }

    /**
     * アンケート登録画面で「登録」または「後で登録」を選択したかどうかを取得します。
     * @return
     */
    public boolean getQuestionDone() {
        return mSharedPreference.getBoolean(FLG_QUESTIONNAIRE_DONE, false);
    }

    /**
     * アンケート登録済みフラグを削除します。
     */
    public void removeQuestionDone() {
        mSharedPreference.edit().remove(FLG_QUESTIONNAIRE_DONE).commit();
    }

    /**
     * 選択された都道府県
     * @param prefectures
     */
    public void setQuestionResultPref(String[] prefectures) {
        Gson gson = new Gson();
        String convPrefectures = gson.toJson(prefectures);
        mSharedPreference.edit().putString(QUESTION_PREFECTURES, encrypt(convPrefectures)).commit();
    }

    /**
     * 前回保存された都道府県
     * @return prefectures
     */
    public String[] getQuestionResultPref() {
        String[] prefectures;
        String savedPrefectures = decrypt(mSharedPreference.getString(QUESTION_PREFECTURES,""));
        Gson gson = new Gson();
        prefectures = gson.fromJson(savedPrefectures, String[].class);

        return prefectures;
    }

    /**
     * 選択された利用したいサービス
     * @param prefectures
     */
    public void setQuestionResultService(String[] prefectures) {
        Gson gson = new Gson();
        String convServices = gson.toJson(prefectures);
        mSharedPreference.edit().putString(QUESTION_SERVICES, encrypt(convServices)).commit();
    }

    /**
     * 前回保存された利用したいサービス
     * @return services
     */
    public String[] getQuestionResultService() {
        String[] services;
        String savedServices = decrypt(mSharedPreference.getString(QUESTION_SERVICES,""));
        Gson gson = new Gson();
        services = gson.fromJson(savedServices, String[].class);
        return services;
    }

    /**
     * LcusNoを保存します。
     * @param lcusNo
     */
    public void setLcusNo(String lcusNo) {
        mSharedPreference.edit().putString(ACCOUNT_LCUS_NO, lcusNo).commit();
    }

    /**
     * LcusNoを取得。
     * @return
     */
    public String getLcusNo() {
        return mSharedPreference.getString(ACCOUNT_LCUS_NO, "");
    }

    /**
     * 配信を許可 の状態を取得します。
     * @return true:通信を許可、false:通信を許可しない
     */
    public boolean getNotificationFlg() {
        return mSharedPreference.getBoolean(FLG_Notification, true);
    }

    /**
     * 配信を許可 の状態を設定します。
     * @param isNoticeOn true:通信を許可、false:通信を許可しない
     */
    public void setNotificationFlg(boolean isNoticeOn) {
        mSharedPreference.edit().putBoolean(FLG_Notification, isNoticeOn).commit();
    }

    /**
     * プッシュ通知受信時の動作:サウンドを再生 の状態を取得します。
     * @return true:サウンドを再生、false:サウンドを再生しない
     */
    public boolean getNotificationSoundFlg() {
        return mSharedPreference.getBoolean(FLG_SOUND, true);
    }

    /**
     * プッシュ通知受信時の動作:サウンドを再生 の状態を設定します。
     * @param isSoundOn true:通信を許可、false:通信を許可しない
     */
    public void setNotificationSoundFlg(boolean isSoundOn) {
        mSharedPreference.edit().putBoolean(FLG_SOUND, isSoundOn).commit();
    }

    /**
     * プッシュ通知受信時の動作:バイブレーション の状態を取得します。
     * @return true:サウンドを再生、false:サウンドを再生しない
     */
    public boolean getNotificationVibrationFlg() {
        return mSharedPreference.getBoolean(FLG_Vibration, true);
    }

    /**
     * プッシュ通知受信時の動作:バイブレーション の状態を設定します。
     * @param isVibrationOn true:バイブレーションする、false:バイブレーションしない
     */
    public void setNotificationVibrationFlg(boolean isVibrationOn) {
        mSharedPreference.edit().putBoolean(FLG_Vibration, isVibrationOn).commit();
    }

    /**
     * 操作時間timeout時間を取得します。
     * @return
     */
    public long getOperationTimeOutTime() {
        return mSharedPreference.getLong(this.mContext.getApplicationContext().getString(R.string.key_appConfig_timeout_defaultTimeoutSec),0);
    }

    /**
     * WebView操作時間timeout時間を取得します。
     * @return
     */
    public long getOperationWebViewTimeOutTime() {
        return mSharedPreference.getLong(this.mContext.getApplicationContext().getString(R.string.key_appConfig_timeout_WebViewTimeoutSec),0);
    }

    /**
     * 最後に操作した時間を取得します。
     * @return 最後に操作した時間
     */
    public long getLastOperationTime() {
        return mSharedPreference.getLong(LAST_OPERATION_TIME, 0);
    }

    /**
     * 最後に操作した時間を登録します。
     * @param lastOperationTime
     */
    public void setLastOperationTime(long lastOperationTime) {
        mSharedPreference.edit().putLong(LAST_OPERATION_TIME, lastOperationTime).commit();
    }

    /**
     * 最後にWebViewを操作した時間を取得します。
     * @return 最後に操作した時間
     */
    public long getLastOperationOnWebViewTime() {
        return mSharedPreference.getLong(LAST_OPERATION_ON_WEB_VIEW_TIME, 0);
    }

    /**
     * 最後にWebViewを操作した時間を登録します。
     * @param lastOperationTime
     */
    public void setLastOperationOnWebViewTime(long lastOperationTime) {
        mSharedPreference.edit().putLong(LAST_OPERATION_ON_WEB_VIEW_TIME, lastOperationTime).commit();
    }

    /**
     * 対応するアプリのバージョンを取得します。
     * @return
     */
    public String getLatestAppVersion() {
        return mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appRequirement_latestAppVersion), "");
    }

    /**
     * アップデート情報を取得します。
     * @return
     */
    public LatestAppUpdateModel getlatestAppUpdateModel() {
        Gson gson = new Gson();
        LatestAppUpdateModel model = gson.fromJson(mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appRequirement_latestAppUpdate), ""), LatestAppUpdateModel.class);
        return model;
    }

    /**
     * 現在メンテナンス中かどうかを取得します。
     * @return
     */
    public boolean getIsNowMaintenance() {
        return mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appMaintenance_maintenance), true);
    }

    /**
     * サービストップのバナーの遷移先URLを取得します。
     * @return
     */
    public String getBannerHref() {
        return mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_href), "");
    }

    /**
     * お知らせのURLを取得します。
     * @return url
     */
    public String getNotificationHref() {
        return mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appNotification_notification_href), "");
    }

    /**
     * サービス登録情報変更のURLを取得します。
     * @return
     */
    public AppSettingsModel getChangeSettings() {
        AppSettingsModel model = new AppSettingsModel();
        model.changeSettings = new LayoutModel();
        model.changeSettings.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appSettings_changeSettings_href),"");
        model.changeSettings.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appSettings_changeSettings_sso),true);
        model.mypage = new LayoutModel();
        model.mypage.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appSettings_mypage_href), "");
        model.mypage.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appSettings_mypage_sso), true);
        return model;
    }

    /**
     * ローチケマイページのURLを取得します。
     * @return
     */
    public String getMyPage() {
        return mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appSettings_mypage_href), "");
    }

    /**
     * ログイン画面から遷移するページの情報を取得します。
     * @return
     */
    public AppLoginModel getAppLoginModel() {
        AppLoginModel model = new AppLoginModel();
        model.tutorial = new LayoutModel();
        model.tutorial.href =  mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appLogin_tutorial_href), "");
        model.loginHelp = new LayoutModel();
        model.loginHelp.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appLogin_loginHelp_href), "");
        return model;
    }

    /**
     * ご利用案内メニューの情報を取得します。
     * @return
     */
    public AppHelpModel getAppHelpJson() {
        AppHelpModel model = new AppHelpModel();
        model.terms = new LayoutModel();
        model.terms.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appHelp_terms_href), "");
        model.help = new LayoutModel();
        model.help.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appHelp_help_href), "");
        model.faq = new LayoutModel();
        model.faq.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appHelp_faq_href), "");
        model.policy=new LayoutModel();
        model.policy.href=mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appHelp_policy_href), "");
        return model;
    }

    /**
     * お知らせ最大表示件数取得
     * @return 最大表示件数
     */
    public int getNotificationCount() {
        return mSharedPreference.getInt(this.mContext.getApplicationContext().getString(R.string.key_appNotification_listCount_count), 0);
    }

    /**
     * サービストップメニューの情報を取得します。
     * @return
     */
    public AppTopModel getAppTopJson() {
        AppTopModel model = new AppTopModel();
//        model.notification1 = new LayoutModel();
//        model.notification1.txt = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_notification1_txt), "");
//        model.notification2 = new LayoutModel();
//        model.notification2.txt = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_notification2_txt), "");
//        model.notification2.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_notification2_href), "");
//
//        model.bannerImg = new LayoutModel();
//        model.bannerImg.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_href), "");
//        model.bannerImg.src = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_src), mContext.getApplicationContext().getString(R.string.image_not_available_url));
//        model.bannerImg.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_sso), true);
//        model.bannerImg.txt = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_txt), "");
        model.eventPopup = new EventPopup();
        model.eventPopup.banner_img = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_banner), "0");
        model.eventPopup.button_img = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_button), "");
        model.eventPopup.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_href), "");
        model.eventPopup.disableShowOnce = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_disable_showonce), false);
        model.eventPopup.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_sso), true);
        model.eventPopup.view = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_view), "");
        if(StringUtils.isNullOrEmpty(model.eventPopup.banner_img) || StringUtils.isNullOrEmpty(model.eventPopup.button_img)
            || StringUtils.isNullOrEmpty(model.eventPopup.href)){
            model.eventPopup = null;
        }
        model.ticketLucky = new LayoutModel();
        model.ticketLucky.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLucky_href), "");
        model.ticketLucky.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLucky_sso), true);
        model.ticketLucky.view = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLucky_view), "");
        model.ticketLeisure = new LayoutModel();
        model.ticketLeisure.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLeisure_href), "");
        model.ticketLeisure.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLeisure_sso), true);
        model.ticketLeisure.view = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLeisure_view), "");
        model.ticketMovie = new LayoutModel();
        model.ticketMovie.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketMovie_href), "");
        model.ticketMovie.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketMovie_sso), true);
        model.ticketMovie.view = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketMovie_view), "");
        model.ticketInsurance = new LayoutModel();
        model.ticketInsurance.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketInsurance_href), "");
        model.ticketInsurance.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketInsurance_sso), true);
        model.ticketInsurance.view = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketInsurance_view), "");
        model.serviceMagazine = new LayoutModel();
        model.serviceMagazine.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMagazine_href), "");
        model.serviceMagazine.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMagazine_sso), true);
        model.serviceMagazine.view = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMagazine_view), "");
        model.serviceVideo = new LayoutModel();
        model.serviceVideo.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceVideo_href), "");
        model.serviceVideo.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceVideo_sso), true);
        model.serviceVideo.view = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceVideo_view), "");
        model.coupon = new LayoutModel();
        model.coupon.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_coupon_href), "");
        model.coupon.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_coupon_sso), true);
        model.coupon.view = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_coupon_view), "");
        model.present = new LayoutModel();
        model.present.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_present_href), "");
        model.present.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_present_sso), true);
        model.present.view = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_present_view), "");

        Gson gson = new Gson();
        TopicsModel topics = gson.fromJson(mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_topics), ""), TopicsModel.class);
        model.topics = topics;
        topics.contents.size();
//        model.invitation = new LayoutModel();
//        model.invitation.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_invitation_href), "");
//        model.invitation.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_invitation_sso), true);
//        model.serviceMusic = new LayoutModel();
//        model.serviceMusic.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMusic_href), "");
//        model.serviceMusic.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMusic_sso), true);


//        model.pickup1 = new LayoutModel();
//        model.pickup1.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_href), "");
//        model.pickup1.src = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_src), mContext.getApplicationContext().getString(R.string.image_not_available_url));
//        model.pickup1.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_sso), true);;
//        model.pickup1.title = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_title), "");
//        model.pickup1.categoryName = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_category_name), "");
//        model.pickup1.categoryColor = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_category_color), "");
//
//        model.pickup2 = new LayoutModel();
//        model.pickup2.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_href), "");
//        model.pickup2.src = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_src), mContext.getApplicationContext().getString(R.string.image_not_available_url));
//        model.pickup2.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_sso), true);;
//        model.pickup2.title = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_title), "");
//        model.pickup2.categoryName = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_category_name), "");
//        model.pickup2.categoryColor = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_category_color), "");
//
//        model.pickup3 = new LayoutModel();
//        model.pickup3.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_href), "");
//        model.pickup3.src = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_src), mContext.getApplicationContext().getString(R.string.image_not_available_url));
//        model.pickup3.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_sso), true);;
//        model.pickup3.title = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_title), "");
//        model.pickup3.categoryName = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_category_name), "");
//        model.pickup3.categoryColor = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_category_color), "");
//
//        model.pickup4 = new LayoutModel();
//        model.pickup4.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_href), "");
//        model.pickup4.src = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_src), mContext.getApplicationContext().getString(R.string.image_not_available_url));
//        model.pickup4.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_sso), true);;
//        model.pickup4.title = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_title), "");
//        model.pickup4.categoryName = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_category_name), "");
//        model.pickup4.categoryColor = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_category_color), "");
//
//        model.adBannerImg = new LayoutModel();
//        model.adBannerImg.href = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_adBannerImg_href), "");
//        model.adBannerImg.src = mSharedPreference.getString(this.mContext.getApplicationContext().getString(R.string.key_appTop_adBannerImg_src), "");
//        model.adBannerImg.sso = mSharedPreference.getBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_adBannerImg_sso), true);

        return model;
    }

    /**
     * レイアウトJSONを登録します。
     * @param model
     */
    public void setLayoutJsonData(LayoutSettingModel model) {
        //前回登録した分をクリア
        clearLayoutJsonData();

        //appRequirement
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appRequirement_latestAppVersion), model.appRequirement.latestAppVersion).commit();
        //appMaintenance
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appMaintenance_maintenance), model.appMaintenance.maintenance).commit();
        //appConfig
        long timeOutTime = Long.parseLong(model.appConfig.timeout.defaultTimeoutSec)*60000; //ミリ秒に変換
        mSharedPreference.edit().putLong(this.mContext.getApplicationContext().getString(R.string.key_appConfig_timeout_defaultTimeoutSec),timeOutTime).commit();
        long webViewTimeOutTime = Long.parseLong(model.appConfig.timeout.WebViewTimeoutSec)*60000;  //ミリ秒に変換
        mSharedPreference.edit().putLong(this.mContext.getApplicationContext().getString(R.string.key_appConfig_timeout_WebViewTimeoutSec),webViewTimeOutTime).commit();
        //appLogin
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appLogin_tutorial_href),model.appLogin.tutorial.href).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appLogin_loginHelp_href),model.appLogin.loginHelp.href).commit();
        //appTop
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_notification1_txt),model.appTop.notification1.txt).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_href),model.appTop.bannerImg.href).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_src),model.appTop.bannerImg.src).commit();
//        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_sso),model.appTop.bannerImg.sso).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_txt),model.appTop.bannerImg.txt).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_notification2_href),model.appTop.notification2.href).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_notification2_txt),model.appTop.notification2.txt).commit();

        if(model.appTop.eventPopup !=null) {
            mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_banner), model.appTop.eventPopup.banner_img).commit();
            mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_button), model.appTop.eventPopup.button_img).commit();
            mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_href), model.appTop.eventPopup.href).commit();
            mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_disable_showonce), model.appTop.eventPopup.disableShowOnce).commit();
            mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_sso), model.appTop.eventPopup.sso).commit();
            mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_view), model.appTop.eventPopup.view).commit();
        }
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketInsurance_href),model.appTop.ticketInsurance.getRealHref()).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketInsurance_sso),model.appTop.ticketInsurance.sso).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketInsurance_view),model.appTop.ticketInsurance.view).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLeisure_href),model.appTop.ticketLeisure.getRealHref()).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLeisure_sso),model.appTop.ticketLeisure.sso).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLeisure_view),model.appTop.ticketLeisure.view).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLucky_href),model.appTop.ticketLucky.getRealHref()).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLucky_sso),model.appTop.ticketLucky.sso).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLucky_view),model.appTop.ticketLucky.view).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketMovie_href),model.appTop.ticketMovie.getRealHref()).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketMovie_sso),model.appTop.ticketMovie.sso).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketMovie_view),model.appTop.ticketMovie.view).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMagazine_href),model.appTop.serviceMagazine.getRealHref()).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMagazine_sso),model.appTop.serviceMagazine.sso).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMagazine_view),model.appTop.serviceMagazine.view).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceVideo_href),model.appTop.serviceVideo.getRealHref()).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceVideo_sso),model.appTop.serviceVideo.sso).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceVideo_view),model.appTop.serviceVideo.view).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_coupon_href),model.appTop.coupon.getRealHref()).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_coupon_sso),model.appTop.coupon.sso).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_coupon_view),model.appTop.coupon.view).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_present_href),model.appTop.present.getRealHref()).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_present_sso),model.appTop.present.sso).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_present_view),model.appTop.present.view).commit();

        Gson gson = new Gson();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_topics),gson.toJson(model.appTop.topics)).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appRequirement_latestAppUpdate),gson.toJson(model.appRequirement.latestAppUpdate)).commit();

//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_invitation_href),model.appTop.invitation.href).commit();
//        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_invitation_sso),model.appTop.invitation.sso).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMusic_href),model.appTop.serviceMusic.href).commit();
//        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMusic_sso),model.appTop.serviceMusic.sso).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_href),model.appTop.pickup1.href).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_category_name),model.appTop.pickup1.categoryName).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_category_color),model.appTop.pickup1.categoryColor).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_src),model.appTop.pickup1.src).commit();
//        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_sso),model.appTop.pickup1.sso).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_title),model.appTop.pickup1.title).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_href),model.appTop.pickup2.href).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_category_name),model.appTop.pickup2.categoryName).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_category_color),model.appTop.pickup2.categoryColor).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_src),model.appTop.pickup2.src).commit();
//        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_sso),model.appTop.pickup2.sso).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_title),model.appTop.pickup2.title).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_href),model.appTop.pickup3.href).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_category_name),model.appTop.pickup3.categoryName).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_category_color),model.appTop.pickup3.categoryColor).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_src),model.appTop.pickup3.src).commit();
//        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_sso),model.appTop.pickup3.sso).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_title),model.appTop.pickup3.title).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_href),model.appTop.pickup4.href).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_category_name),model.appTop.pickup4.categoryName).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_category_color),model.appTop.pickup4.categoryColor).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_src),model.appTop.pickup4.src).commit();
//        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_sso),model.appTop.pickup4.sso).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_title),model.appTop.pickup4.title).commit();
//        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appTop_adBannerImg_sso),model.appTop.adBannerImg.sso).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_adBannerImg_href),model.appTop.adBannerImg.href).commit();
//        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appTop_adBannerImg_src),model.appTop.adBannerImg.src).commit();
        //appHelp
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appHelp_help_href),model.appHelp.help.href).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appHelp_help_sso),model.appHelp.help.sso).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appHelp_faq_href),model.appHelp.faq.href).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appHelp_faq_sso),model.appHelp.faq.sso).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appHelp_terms_href),model.appHelp.terms.href).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appHelp_terms_sso),model.appHelp.terms.sso).commit();
        if(model.appHelp.policy!=null){
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appHelp_policy_href),model.appHelp.policy.href).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appHelp_policy_sso),model.appHelp.policy.sso).commit();
        }
        //appSettings
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appSettings_changeSettings_href),model.appSettings.changeSettings.href).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appSettings_changeSettings_sso),model.appSettings.changeSettings.sso).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appSettings_mypage_href),model.appSettings.mypage.href).commit();
        mSharedPreference.edit().putBoolean(this.mContext.getApplicationContext().getString(R.string.key_appSettings_mypage_sso),model.appSettings.mypage.sso).commit();
        //appNotificatio　
        mSharedPreference.edit().putInt(this.mContext.getApplicationContext().getString(R.string.key_appNotification_listCount_count),model.appNotification.listCount.count).commit();
        mSharedPreference.edit().putString(this.mContext.getApplicationContext().getString(R.string.key_appNotification_notification_href),model.appNotification.notification.href).commit();


    }

    /**
     * 前回登録したレイアウトJSONデータを削除します。
     */
    private void clearLayoutJsonData() {
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appRequirement_latestAppVersion)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appRequirement_latestAppUpdate)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appMaintenance_maintenance)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appConfig_timeout_defaultTimeoutSec)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appConfig_timeout_WebViewTimeoutSec)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appLogin_tutorial_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appLogin_loginHelp_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_notification1_txt)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_src)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_bannerImg_txt)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_notification2_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_banner)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_button)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_disable_showonce)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_event_popup_view)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketInsurance_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketInsurance_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLeisure_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLeisure_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLucky_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketLucky_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketMovie_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_ticketMovie_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_coupon_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_coupon_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_present_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_present_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMusic_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMusic_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMagazine_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceMagazine_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceVideo_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_serviceVideo_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_topics)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_category_name)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_category_color)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_src)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup1_title)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_category_name)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_category_color)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_src)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup2_title)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_category_name)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_category_color)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_src)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup3_title)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_category_name)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_category_color)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_src)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_pickup4_title)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_adBannerImg_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_adBannerImg_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appTop_adBannerImg_src)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appHelp_help_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appHelp_help_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appHelp_faq_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appHelp_faq_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appHelp_terms_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appHelp_terms_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appHelp_policy_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appHelp_policy_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appSettings_changeSettings_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appSettings_changeSettings_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appSettings_mypage_href)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appSettings_mypage_sso)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appNotification_listCount_count)).commit();
        mSharedPreference.edit().remove(this.mContext.getApplicationContext().getString(R.string.key_appNotification_notification_href)).commit();

    }

    /**
     * 暗号化します。
     * @param val
     * @return
     */
    private String encrypt(String val) {
        String encrypted = "";
        try {
            //暗号化
            encrypted = EncryptUtil.encryptString(mContext, val);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return encrypted;
    }

    /**
     * 復号化します。
     * @param val
     * @return
     */
    private String decrypt(String val) {
        String decrypted = "";
        if (val != null && !val.isEmpty()) {
            try {
                decrypted = EncryptUtil.decryptString(mContext, val);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }
        }
        return decrypted;
    }

}
