package jp.co.lhe.lhememberapp.ui.events;

public class OnMenuTapEvent {
    public String url;
    public boolean sso;
    public String view;
    public String exScreen;
    public boolean tabuho = false;
    public boolean isClearFragment;


    /**
     *
     * @param url
     * @param sso
     * @param exScreen
     */
    public OnMenuTapEvent(String url, boolean sso, String exScreen) {
        this.url = url;
        this.sso = sso;
        this.view = "0";
        this.exScreen = exScreen;

    }

    /**
     * view有り
     * @param url
     * @param sso
     * @param view: hrefを開く際にどのツールを使うか（0：WebView,1：外部ブラウザ）
     * @param exScreen
     */
    public OnMenuTapEvent(String url, boolean sso, String view, String exScreen) {
        this.url = url;
        this.sso = sso;
        this.view = view;
        this.exScreen = exScreen;

    }

    public OnMenuTapEvent(boolean isClearFragment,String url, boolean sso, String view, String exScreen) {
        this.url = url;
        this.sso = sso;
        this.view = view;
        this.exScreen = exScreen;
        this.isClearFragment= isClearFragment;
    }


    /**
     * タブホ判別用
     * @param url
     * @param sso
     * @param view
     * @param exScreen
     * @param tabuho: true => タブホ、 false => タブホ以外　
     */
    public OnMenuTapEvent(String url, boolean sso, String view, String exScreen, boolean tabuho) {
        this.url = url;
        this.sso = sso;
        this.view = view;
        this.exScreen = exScreen;
        this.tabuho = tabuho;
    }
}
