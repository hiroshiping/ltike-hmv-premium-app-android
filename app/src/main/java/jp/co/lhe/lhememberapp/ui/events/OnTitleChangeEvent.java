package jp.co.lhe.lhememberapp.ui.events;

/**
 * Created by dts on 2018/03/15.
 */

public class OnTitleChangeEvent {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public OnTitleChangeEvent(String title) {
        this.title = title;
    }
}
