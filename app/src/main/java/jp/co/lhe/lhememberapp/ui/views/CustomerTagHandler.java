package jp.co.lhe.lhememberapp.ui.views;

import android.content.Context;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StrikethroughSpan;
import android.util.TypedValue;
import android.view.TextureView;

import com.orhanobut.logger.Logger;

import org.xml.sax.Attributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import jp.co.lhe.lhememberapp.LMApplication;

/**
 * Created by Siy on 2016/11/23.
 */

public class CustomerTagHandler implements HtmlParser.TagHandler {
    /**
     * html 标签的开始下标
     */
    private Stack<Integer> startIndex;

    /**
     * html的标签的属性值 value，如:<size value='16'></size>
     * 注：value的值不能带有单位,默认就是sp
     */
    private Stack<String> propertyValue;

    @Override
    public boolean handleTag(boolean opening, String tag, Editable output, Attributes attributes) {
        if (opening) {
            handlerStartTAG(tag, output, attributes);
        } else {
            handlerEndTAG(tag, output, attributes);
        }
        return handlerBYDefault(tag);
    }

    private void handlerStartTAG(String tag, Editable output, Attributes attributes) {
        if (tag.equalsIgnoreCase("del")) {
            handlerStartDEL(output);
        } else if (tag.equalsIgnoreCase("span")) {
            handlerStartSPAN(output, attributes);
        }
    }


    private void handlerEndTAG(String tag, Editable output, Attributes attributes) {
        if (tag.equalsIgnoreCase("del")) {
            handlerEndDEL(output);
        } else if (tag.equalsIgnoreCase("span")) {
            handlerEndSPAN(output);
        }
    }

    private void handlerStartFONT(Editable output, Attributes attributes) {
        if (startIndex == null) {
            startIndex = new Stack<>();
        }
        startIndex.push(output.length());

        if (propertyValue == null) {
            propertyValue = new Stack<>();
        }

        propertyValue.push(HtmlParser.getValue(attributes, "size"));
    }

    private void handlerStartSPAN(Editable output, Attributes attributes) {
        if (startIndex == null) {
            startIndex = new Stack<>();
        }
        startIndex.push(output.length());

        if (propertyValue == null) {
            propertyValue = new Stack<>();
        }

        propertyValue.push(HtmlParser.getValue(attributes, "style"));
    }


    private void handlerEndFONT(Editable output) {
        if (!isEmpty(propertyValue)) {
            try {
                int value = Integer.parseInt(propertyValue.pop());
                output.setSpan(new AbsoluteSizeSpan(sp2px(LMApplication.getApplication(), value)), startIndex.pop(), output.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handlerEndSPAN(Editable output) {
        if (!isEmpty(propertyValue)) {
            try {
                String str = propertyValue.pop();

                if (!TextUtils.isEmpty(str)) {
                    str = str.trim();
                    String[] strings = str.split(";");
                    if (strings != null && strings.length > 0) {
                        Map<String, String> map = new HashMap<>();
                        for (String s : strings) {
                            if (s.contains(":")) {
                                int divider = s.indexOf(":");
                                map.put(s.substring(0, divider), s.substring(divider + 1));
                            }
                        }
                        if (map.containsKey("font-size")) {
                            String v = map.get("font-size");
                            if (!TextUtils.isEmpty(v)) {
                                float value = Float.parseFloat(v);
                                output.setSpan(new AbsoluteSizeSpan(sp2px(LMApplication.getApplication(), value)), startIndex.pop(), output.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void handlerStartDEL(Editable output) {
        if (startIndex == null) {
            startIndex = new Stack<>();
        }
        startIndex.push(output.length());
    }

    private void handlerEndDEL(Editable output) {
        output.setSpan(new StrikethroughSpan(), startIndex.pop(), output.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * 返回true表示不交给系统后续处理
     * false表示交给系统后续处理
     *
     * @param tag
     * @return
     */
    private boolean handlerBYDefault(String tag) {
        if (tag.equalsIgnoreCase("del")) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }


    public static int sp2px(Context context, float spValue) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics()) + 0.5f);
    }

}
