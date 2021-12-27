package jp.co.lhe.lhememberapp.ui.events;

public class OnSSOLoginFailedEvent {
    public Exception error;

    public OnSSOLoginFailedEvent(Exception error) {
        this.error = error;
    }
}
