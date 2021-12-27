package jp.co.lhe.lhememberapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import jp.co.lhe.lhememberapp.ui.events.OnTitleChangeEvent;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    public ProgressBar mProgressBar;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public String getTitle() {
        return "";
    }

    public boolean changeTitle(){
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (changeTitle() && !TextUtils.isEmpty(getTitle())) {

            EventBusHolder.EVENT_BUS.post(new OnTitleChangeEvent(getTitle()));
        }
    }
}
