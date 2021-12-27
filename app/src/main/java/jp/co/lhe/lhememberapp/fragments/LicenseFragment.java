package jp.co.lhe.lhememberapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.OnClick;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.ui.events.OnLicensePageEvent;
import jp.co.lhe.lhememberapp.ui.events.OnSettingMenuCloseTapEvent;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;

public class LicenseFragment extends BaseFragment {

    private static final String LICENSE_TITLE = "ソフトウェアライセンス";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_license, container, false);
        ImageView imageView=view.findViewById(R.id.iv_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBusHolder.EVENT_BUS.post(new OnLicensePageEvent(true));
            }
        });
        return view;
    }

    @Override
    public String getTitle() {
        return LICENSE_TITLE;
    }

    @Override
    public boolean changeTitle() {
        return false;
    }
}
