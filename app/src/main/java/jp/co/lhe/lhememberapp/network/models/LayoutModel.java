package jp.co.lhe.lhememberapp.network.models;

import android.text.TextUtils;

public class LayoutModel {
    public String txt;
    public String src;
    public String href;
    public String href_android;
    public boolean sso;
    public String view;
    public String title;
    public String categoryName;
    public String categoryColor;
    public int count;

    public String getRealHref() {
        if (href_android!=null && !TextUtils.isEmpty(href_android)){
            return href_android;
        }
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
