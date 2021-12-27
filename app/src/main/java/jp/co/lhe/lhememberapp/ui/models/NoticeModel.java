package jp.co.lhe.lhememberapp.ui.models;

import jp.funnelpush.sdk.response.MessagesListResponse;

public class NoticeModel {

    public String cellTitle = "";
    public String cellMenuSubTitle = "";
    public String cellSubMenuUrl = "";
    public boolean isNoticeItem = false;
    public MessagesListResponse.MessageSummary messageSummary;
}
