package jp.co.lhe.lhememberapp.network.models;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;

public class ToastInfo {
    private String title;
    private   String message;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ToastInfo(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public static ToastInfo getNetErrorInfo(){
//        return new ToastInfo(LMApplication.getApplication().getString(R.string.net_error_title),LMApplication.getApplication().getString(R.string.net_error_msg));
        return getTimeInfo();
    }


    public static ToastInfo getTimeInfo(){
        return new ToastInfo("ネットワーク接続エラー","電波状況の良いところでお使いください");
    }

}
