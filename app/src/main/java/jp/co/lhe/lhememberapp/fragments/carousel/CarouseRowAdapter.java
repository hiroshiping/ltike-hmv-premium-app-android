package jp.co.lhe.lhememberapp.fragments.carousel;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.network.models.TopicsColumnModel;
import jp.co.lhe.lhememberapp.ui.events.OnCalledViewCloseEvent;
import jp.co.lhe.lhememberapp.ui.events.OnMenuTapEvent;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.utils.ScreenUtil;

public class CarouseRowAdapter extends RecyclerView.Adapter<CarouseRowAdapter.CarouselRowViewHolder> {

    private static final String SPACE_STRING = "空";
    private static final int TEXT_SP_SIZE = 10;
    private static final int DESCRIPTION_TEXT_SIZE = ScreenUtil.sp2px(LMApplication.getApplication(), TEXT_SP_SIZE);

    private static final int MAX_COLUMN = 10;
    private List<TopicsColumnModel> mTopicsColumnModels;
    private int mMaxRatherHeight = 0;

    private int mLayoutWidth = 0;
    private int mLayoutHeight = 0;


    public CarouseRowAdapter() {
        mTopicsColumnModels = new ArrayList<>();
    }

    public void updateRowData(List<TopicsColumnModel> topicsColumnModels, Paint paint, int layoutWidth, int layoutHeight) {
        mTopicsColumnModels.clear();
        if (topicsColumnModels != null) {
            mTopicsColumnModels = topicsColumnModels;
        }
        mLayoutWidth = layoutWidth;
        mLayoutHeight = layoutHeight;

        mMaxRatherHeight = getMaxDescriptionHeight(paint, topicsColumnModels, layoutWidth);

        notifyDataSetChanged();

    }

    private int getMaxDescriptionHeight(Paint paint, List<TopicsColumnModel> topicsColumnModels, int layoutWidth) {
        if(topicsColumnModels == null){
            return 0;
        }

        paint.setTextSize(DESCRIPTION_TEXT_SIZE);


        Rect rect = new Rect();
        paint.getTextBounds(SPACE_STRING, 0, SPACE_STRING.length(), rect);
        int UNIT_HEIGHT = rect.height();

        int itemCount = getItemCount();
        int maxRatherHeightNumber = 0;
        for (int i = 0; i < itemCount; i++){
            TopicsColumnModel topicsColumnModel = topicsColumnModels.get(i);
            if(topicsColumnModel != null && topicsColumnModel.description != null ){

                int size = topicsColumnModel.description.size();
                int tempHeight = 0;
                for(int j = 0; j < size; j++){
                    String description = topicsColumnModel.description.get(j);
                    float descriptionWidth = paint.measureText(description);
                    tempHeight++;
                    if(descriptionWidth > layoutWidth){
                        //two lines
                        tempHeight++;
                    }
                }

                if(tempHeight > maxRatherHeightNumber){
                    maxRatherHeightNumber = tempHeight;
                }

            }
        }



        return (int) (maxRatherHeightNumber * UNIT_HEIGHT * 1.5f);
    }

    @NonNull
    @Override
    public CarouselRowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int index) {

        CarouselColumnView carouseRowView = new CarouselColumnView(viewGroup.getContext());
        return new CarouselRowViewHolder(carouseRowView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselRowViewHolder carouselRowViewHolder, int index) {
        TopicsColumnModel columnModel = mTopicsColumnModels.get(index);
        carouselRowViewHolder.carouseRowView.updateRowData(columnModel, mMaxRatherHeight, mLayoutWidth, mLayoutHeight);

        carouselRowViewHolder.carouseRowView.setOnImageClickListener(v -> {
            //HomeActivityへ発火
            EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(columnModel.href, columnModel.sso, columnModel.view, OnCalledViewCloseEvent.SCREEN_SERVICE_WEBVIEW));
        });
    }

    @Override
    public int getItemCount() {
        if (mTopicsColumnModels == null) {
            return 0;
        } else {
            int size = mTopicsColumnModels.size();
            return size > MAX_COLUMN? MAX_COLUMN: size;
        }
    }

    public static class CarouselRowViewHolder extends RecyclerView.ViewHolder {

        public CarouselColumnView carouseRowView;
        public CarouselRowViewHolder(@NonNull CarouselColumnView carouseRowView) {
            super(carouseRowView);
            this.carouseRowView = carouseRowView;
        }
    }
}
