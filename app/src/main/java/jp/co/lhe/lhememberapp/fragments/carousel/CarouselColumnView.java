package jp.co.lhe.lhememberapp.fragments.carousel;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.network.models.TopicsColumnModel;

public class CarouselColumnView extends FrameLayout {

    private ImageView mGoodsPicture;
    private LinearLayout mGoodsDescriptionContainer;
    private OnImageClickListener mOnImageClickListener;

    public CarouselColumnView(Context context) {
        this(context, null);
    }

    public CarouselColumnView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselColumnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.service_top_carousel_column_view, this);
        mGoodsPicture = findViewById(R.id.carousel_column_image);
        mGoodsDescriptionContainer = findViewById(R.id.carousel_column_text_layout);

        mGoodsPicture.setOnClickListener(v -> {
            if(mOnImageClickListener != null){
                mOnImageClickListener.onImageClicked(v);
            }
        });
    }

    public void updateRowData(@NonNull TopicsColumnModel columnModel, int maxDescriptionHeight, int layoutWidth, int layoutHeight) {
        Glide.with(this).load(columnModel.src).into(mGoodsPicture);

        ViewGroup.LayoutParams imageParams = mGoodsPicture.getLayoutParams();
        imageParams.height = layoutHeight;
        imageParams.width = layoutWidth;
        mGoodsPicture.setLayoutParams(imageParams);

        mGoodsDescriptionContainer.removeAllViews();
        ViewGroup.LayoutParams containerLayoutParams = mGoodsDescriptionContainer.getLayoutParams();
        containerLayoutParams.height = maxDescriptionHeight;
        containerLayoutParams.width = layoutWidth;
        mGoodsDescriptionContainer.setLayoutParams(containerLayoutParams);


        // description設定
        for (int i = 0; i < columnModel.description.size(); i++) {

            TextView columnText = new TextView(mGoodsDescriptionContainer.getContext());
            columnText.setText(columnModel.description.get(i));
            columnText.setGravity(Gravity.CENTER);
            columnText.setHorizontallyScrolling(true);
            columnText.setMaxLines(2);

            columnText.setSingleLine(false);
            columnText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            columnText.setEllipsize(TextUtils.TruncateAt.END);
            int descriptionColor;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                descriptionColor = getResources().getColor(R.color.carouselDescription, null);
            }else{
                descriptionColor = getResources().getColor(R.color.carouselDescription);
            }
            columnText.setTextColor(descriptionColor);
            columnText.setPadding(0, 0, 0, 0);

            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textLayoutParams.gravity = Gravity.CENTER;
            textLayoutParams.topMargin = 0;
            textLayoutParams.bottomMargin = 0;
            columnText.setLayoutParams(textLayoutParams);


            mGoodsDescriptionContainer.addView(columnText);
        }

    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mOnImageClickListener = listener;
    }

    /**
     * picture click listener
     */
    public interface OnImageClickListener{

        void onImageClicked(View view);
    }
}
