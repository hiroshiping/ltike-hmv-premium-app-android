package jp.co.lhe.lhememberapp.ui.events;

public class OnLicensePageEvent {

    private boolean isBack;

    public OnLicensePageEvent(){
        this.isBack=false;
    }

    public OnLicensePageEvent(boolean status){
        this.isBack=status;
    }

    public boolean isBack() {
        return isBack;
    }
}
