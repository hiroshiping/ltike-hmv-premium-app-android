package jp.co.lhe.lhememberapp.fragments;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.network.models.AppHelpModel;
import jp.co.lhe.lhememberapp.ui.events.OnCalledViewCloseEvent;
import jp.co.lhe.lhememberapp.ui.events.OnLicensePageEvent;
import jp.co.lhe.lhememberapp.ui.events.OnMenuTapEvent;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.utils.LogUtil;

/**
 * ご利用案内Fragment
 */
public class TermsFragment extends BaseFragment {
    private OnFragmentInteractionListener mListener;

    public TermsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TermsFragment.
     */
    public static TermsFragment newInstance() {
        TermsFragment fragment = new TermsFragment();
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
        View view = inflater.inflate(R.layout.fragment_terms, container, false);

        RelativeLayout howToBlock =
            (RelativeLayout) view.findViewById(R.id.fragment_guidance_how_to_block);
        RelativeLayout faqBlock =
            (RelativeLayout) view.findViewById(R.id.fragment_guidance_faq_block);
        RelativeLayout termsBlock =
            (RelativeLayout) view.findViewById(R.id.fragment_guidance_terms_block);
        RelativeLayout policyBlock =
                (RelativeLayout) view.findViewById(R.id.fragment_policy_terms_block);
        RelativeLayout licenseBlock =
                (RelativeLayout) view.findViewById(R.id.fragment_license_terms_block);

        final AppHelpModel model = ((LMApplication)getActivity().getApplicationContext()).getLmaSharedPreference().getAppHelpJson();

        howToBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.help.href, true, OnCalledViewCloseEvent.SCREEN_TERMS));
            }
        });
        faqBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.faq.href, true, OnCalledViewCloseEvent.SCREEN_TERMS));
            }
        });
        termsBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HomeActivityへ発火
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.terms.href, true, OnCalledViewCloseEvent.SCREEN_TERMS));
            }
        });
        policyBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBusHolder.EVENT_BUS.post(new OnMenuTapEvent(model.policy.href, true, OnCalledViewCloseEvent.SCREEN_TERMS));
            }
        });
        licenseBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBusHolder.EVENT_BUS.post(new OnLicensePageEvent());
            }
        });

        PackageManager pm = getActivity().getPackageManager();
        int versionCode = 0;
        String versionName = "";
        try{
            PackageInfo packageInfo = pm.getPackageInfo(getActivity().getPackageName(), 0);
            versionName = packageInfo.versionName;
            LogUtil.logDebug("versionCode = " + versionCode + ", " + versionName);

        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }


        TextView appVerText = (TextView) view.findViewById(R.id.fragment_terms_app_version);
        appVerText.setText("バージョン情報：" + versionName);

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
        return getString(R.string.guidance_title);
    }
}
