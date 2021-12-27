package jp.co.lhe.lhememberapp.ui.events;

public class OnCalledViewCloseEvent {
    public String message;
    public static final String SCREEN_NOTICE = "screenNotice";
    public static final String SCREEN_SERVICE_WEBVIEW = "screenServiceWebView";
    public static final String SCREEN_LOGIN = "screenLogin";
    public static final String SCREEN_SETTING = "screenSetting";
    public static final String SCREEN_TERMS = "screenTerms";
    public static final String SCREEN_HOME = "screenHome";

    public OnCalledViewCloseEvent(String message) {
        this.message = message;
    }
}
