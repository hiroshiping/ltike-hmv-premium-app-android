package jp.co.lhe.lhememberapp.fragments.carousel;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.network.models.TopicsRowModel;

public class CarouseRowView extends FrameLayout {

    private static final double IMAGE_SIZE_RATE = 1.3;

    private CarouseRowAdapter mCarouseRowAdapter;

    private RecyclerView mCarouselRowView;

    private Paint mRowPaint = new Paint();

    public CarouseRowView(Context context) {
        this(context, null);
    }

    public CarouseRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouseRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.service_top_carousel_row_view, this);
        mCarouselRowView = findViewById(R.id.carousel_card_row_view);
        mCarouselRowView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mCarouseRowAdapter = new CarouseRowAdapter();
        mCarouselRowView.setAdapter(mCarouseRowAdapter);
    }

    public void updateRowData(@NonNull TopicsRowModel rowModel, int width){
        //  縦・横幅の設定 (正方形)
            int layoutWidth  = (int)((double)width * 4.0 / 15.0 * IMAGE_SIZE_RATE);
            int layoutHeight = layoutWidth;

            //  長方形に変更　(横幅の変更)
            if (rowModel.imageType.equals(getResources().getString(R.string.service_top_carousel_image_type_1))) {
                layoutWidth = (int)((double)layoutWidth * 4 / 5 );
            }

        mCarouseRowAdapter.updateRowData(rowModel.columns, mRowPaint, layoutWidth, layoutHeight);
    }
}
