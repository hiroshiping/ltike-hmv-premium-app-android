package jp.co.lhe.lhememberapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.ui.events.OnSettingMenuCloseTapEvent;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.funnelpush.sdk.FunnelPush;

/**
 * プッシュ設定Fragment
 */
public class PushSettingFragment extends BaseFragment {

    private OnFragmentInteractionListener mListener;

    public PushSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PushSettingFragment.
     */
    public static PushSettingFragment newInstance() {
        PushSettingFragment fragment = new PushSettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_push_setting, container, false);
        ButterKnife.bind(this,view);

        //Push配信を許可
        Switch allowSwitch = (Switch) view.findViewById(R.id.fragment_push_allow_switch);
        allowSwitch.setChecked(((LMApplication)getActivity().getApplicationContext()).getLmaSharedPreference().getNotificationFlg());
        allowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((LMApplication)getActivity().getApplicationContext()).getLmaSharedPreference().setNotificationFlg(isChecked);

                if (isChecked) {
                    FunnelPush.enablePushNotification(getActivity().getApplicationContext(), true);
                } else {
                    FunnelPush.enablePushNotification(getActivity().getApplicationContext(), false);
                }
            }
        });
        //サウンドを再生
        Switch soundSwitch = (Switch) view.findViewById(R.id.fragment_push_sound_switch);
        soundSwitch.setChecked(((LMApplication)getActivity().getApplicationContext()).getLmaSharedPreference().getNotificationSoundFlg());
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((LMApplication)getActivity().getApplicationContext()).getLmaSharedPreference().setNotificationSoundFlg(isChecked);
            }
        });
        //バイブレーション
        Switch vibrationSwitch = (Switch) view.findViewById(R.id.fragment_push_vibration_switch);
        vibrationSwitch.setChecked(((LMApplication)getActivity().getApplicationContext()).getLmaSharedPreference().getNotificationVibrationFlg());
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((LMApplication)getActivity().getApplicationContext()).getLmaSharedPreference().setNotificationVibrationFlg(isChecked);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }

    @Override
    public String getTitle() {
        return getString(R.string.push_setting_title);
    }

    @OnClick(R.id.iv_back)
    public void onBackClick(){
        EventBusHolder.EVENT_BUS.post(new OnSettingMenuCloseTapEvent(""));
    }
}
