package jp.co.lhe.lhememberapp.fragments.carousel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.network.models.TopicsContentsModel;
import jp.co.lhe.lhememberapp.network.models.TopicsRowModel;

public class CarouselView extends FrameLayout {

    private static final int MAX_ROW_NUMBER = 3;

    private ImageView mTitleIcon;
    private TextView mTitleText;
    private LinearLayout mContentContainer;


    public CarouselView(Context context) {
        this(context, null);
    }

    public CarouselView(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.component_topics_carousel, this);

        // タイトル部
        mTitleIcon = findViewById(R.id.carousel_title_image);
        mTitleText = findViewById(R.id.carousel_title_text);

        // グリッド部
        mContentContainer = findViewById(R.id.carousel_list_layout);
    }

    public void updateCarouselData(@NonNull TopicsContentsModel contentsModel, int width){
        Glide.with(this).load(contentsModel.thumb_url).into(mTitleIcon);
        mTitleText.setText(contentsModel.title);


        for (int row = 0; row < contentsModel.rows.size(); row++) {

            // 4行目を表示させない
            if (row >= MAX_ROW_NUMBER) {
                break;
            }

            TopicsRowModel rowModel = contentsModel.rows.get(row);

            CarouseRowView carouseRowView = new CarouseRowView(getContext());
            mContentContainer.addView(carouseRowView);

            carouseRowView.updateRowData(rowModel, width);
        }

    }
}
