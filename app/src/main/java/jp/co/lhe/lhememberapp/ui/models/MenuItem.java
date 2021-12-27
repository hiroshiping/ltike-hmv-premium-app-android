package jp.co.lhe.lhememberapp.ui.models;

import jp.co.lhe.lhememberapp.network.models.LayoutModel;

/**
 * Created by lhedev on 2018/03/05.
 */

public class MenuItem {
    public static final int NO_TAB_INDEX = -1;
    public MenuItem(int icon, LayoutModel model) {
        this.icon = icon;
        this.model = model;
    }

    public MenuItem(int icon, LayoutModel model, int tabResId) {
        this.icon = icon;
        this.model = model;
        this.tabResId = tabResId;
    }

    private int icon;
    private LayoutModel model;
    private int tabResId = NO_TAB_INDEX;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public LayoutModel getModel() {
        return model;
    }

    public void setModel(LayoutModel model) {
        this.model = model;
    }

    public int getTabResId() {
        return tabResId;
    }

    public void setTabResId(int tabResId) {
        this.tabResId = tabResId;
    }
}
