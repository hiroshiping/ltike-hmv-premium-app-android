package jp.co.lhe.lhememberapp.ui.views;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

public abstract class BlueClickableSpan extends ClickableSpan {

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(Color.BLUE);
    }
}
