package jp.co.lhe.lhememberapp.ui.events;

public class OnQuestionTapEvent {
    public static final String INIT = "init";
    public static final String CLOSE = "close";
    public static final String ERR = "error";

    public String message;
    public OnQuestionTapEvent(String message) {
        this.message = message;
    }
}
