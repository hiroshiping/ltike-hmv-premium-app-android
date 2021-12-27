package jp.co.lhe.lhememberapp.ui.events;

import jp.funnelpush.sdk.response.MessagesResponse;

public class OnNoticeItemTapEvent {

    public MessagesResponse messagesResponse;

    public OnNoticeItemTapEvent(MessagesResponse messagesResponse) {
        this.messagesResponse = messagesResponse;
    }
}
