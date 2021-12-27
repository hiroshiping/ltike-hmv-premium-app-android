package jp.co.lhe.lhememberapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import jp.co.lhe.lhememberapp.activities.HomeActivity;
import jp.co.lhe.lhememberapp.network.models.ToastInfo;
import jp.co.lhe.lhememberapp.ui.events.OnQuestionTapEvent;
import jp.co.lhe.lhememberapp.ui.events.OnSettingMenuCloseTapEvent;
import jp.co.lhe.lhememberapp.ui.models.QuestionItemModel;
import jp.co.lhe.lhememberapp.ui.utils.EventBusHolder;
import jp.co.lhe.lhememberapp.ui.views.UserInfoGridAdapter;
import jp.co.lhe.lhememberapp.utils.FirebaseUtil;
import jp.funnelpush.sdk.FunnelPush;
import jp.funnelpush.sdk.callback.OnUserAttributesSendListener;
import jp.funnelpush.sdk.model.builder.FunnelPushBuilders;
import jp.funnelpush.sdk.response.UserAttributes;

/**
 * アンケート画面Fragment
 */
public class QuestionFragment extends BaseFragment {
    private List<UserInfoGridAdapter> mPrefectureAdapterList;
    private UserInfoGridAdapter mFavListAdapter;

    private static final String ARG_PARAM1 = "exScreen";
    private static final String ARG_PARAM2 = "message";
    private static final String ARG_PARAM3 = "url";
    private static final String ARG_PARAM4 = "a01";
    private static final String ARG_PARAM5 = "a02";

    private String mExScreen;
    private String mMessage;
    private String mUrl;

    private boolean mDoingRegister = false;

    private String[] mSavedPrefectures;
    private String[] mSavedServices;


    private ScrollView mScrollView;
    private OnFragmentInteractionListener mListener;

    @BindView(R.id.iv_back)
    ImageView mBackIv;

    public QuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment QuestionFragment.
     */
    public static QuestionFragment newInstance(String param1, String param2, String param3, String[] s01, String[] s02) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putStringArray(ARG_PARAM4, s01);
        args.putStringArray(ARG_PARAM5, s02);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mExScreen = getArguments().getString(ARG_PARAM1);
            mMessage = getArguments().getString(ARG_PARAM2);
            mUrl = getArguments().getString(ARG_PARAM3);
            mSavedPrefectures = getArguments().getStringArray(ARG_PARAM4);
            mSavedServices = getArguments().getStringArray(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() !=null && isAdded()) {
            mBackIv.setVisibility(getActivity() instanceof HomeActivity ? View.VISIBLE : View.GONE);
        }
        mDoingRegister = true;
        //Funnel Pushに登録済みのアンケートを取得
        setComponents(view);

        mScrollView = (ScrollView) view.findViewById(R.id.fragment_question_scroll_view);
        mScrollView.fullScroll(View.FOCUS_UP);


        return view;
    }

    private void setComponents(View view) {
        Button laterBtn = (Button) view.findViewById(R.id.activity_sign_up_later_btn);
        Button signUpBtn = (Button) view.findViewById(R.id.activity_sign_up_next_btn);

        if (getString(R.string.user_info_ex_screen_settings).equals(mExScreen)) {
            //設定画面から起動された場合
            TextView note = (TextView) view.findViewById(R.id.activity_user_info_note);
            note.setVisibility(View.GONE);

            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!mDoingRegister) {
                        return;
                    }
                    mDoingRegister = false;
                    //アンケート回答内容を登録
                    saveQuestionResult();


                }
            });
            laterBtn.setVisibility(View.GONE);
        } else {
            //ログイン画面から起動した場合
            RelativeLayout actionBarView = (RelativeLayout) view.findViewById(R.id.app_action_bar);
            actionBarView.setVisibility(View.VISIBLE);
            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!mDoingRegister) {
                        return;
                    }
                    mDoingRegister = false;

                    //アンケート回答内容を登録
                    saveQuestionResult();
                }
            });

            laterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mDoingRegister) {
                        return;
                    }
                    mDoingRegister = false;
                    //後で登録選択済み
                    ((LMApplication) getActivity().getApplicationContext()).getLmaSharedPreference().setQuestionDone(true);

                    Intent intent = new Intent(v.getContext(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }
        //情報取得したい都道府県
        setPrefComponents(view);

        //利用したいサービス
        mFavListAdapter = new UserInfoGridAdapter(getActivity(), false);
        mFavListAdapter.setAdapter(setFavoritesList());
        LinearLayout favCheckBoxBlock = (LinearLayout) view.findViewById(R.id.component_favorite_check_box_block);
        GridView favGridView = (GridView) favCheckBoxBlock.findViewById(R.id.component_favorite_check_box_grid_view);
        favGridView.setAdapter(mFavListAdapter);

    }

    /**
     * 情報取得したい都道府県をセットします。
     */
    private void setPrefComponents(View view) {
        List<List<QuestionItemModel>> prefectures = setPrefectureList();

        int prefAreaCount = prefectures.size();
        for (int i = 0; i < prefAreaCount; i++) {
            LinearLayout prefView = null;
            GridView checkBoxGridView;
            TextView prefNameTextView;
            if (i == 0) {
                //北海道
                prefView = (LinearLayout) view.findViewById(R.id.user_info_prefecture_hokkaido);
                prefNameTextView = (TextView) prefView.findViewById(R.id.component_prefecture_area_name);
                prefNameTextView.setText(getString(R.string.pref_area_name_hokkaido));
            } else if (i == 1) {
                //東北
                prefView = (LinearLayout) view.findViewById(R.id.user_info_prefecture_tohoku);
                prefNameTextView = (TextView) prefView.findViewById(R.id.component_prefecture_area_name);
                prefNameTextView.setText(getString(R.string.pref_area_name_tohoku));
            } else if (i == 2) {
                //関東・甲信越
                prefView = (LinearLayout) view.findViewById(R.id.user_info_prefecture_kanto_koshinetsu);
                prefNameTextView = (TextView) prefView.findViewById(R.id.component_prefecture_area_name);
                prefNameTextView.setText(getString(R.string.pref_area_name_kanto_koshinetsu));
            } else if (i == 3) {
                //中部・東海
                prefView = (LinearLayout) view.findViewById(R.id.user_info_prefecture_chubu_tokai);
                prefNameTextView = (TextView) prefView.findViewById(R.id.component_prefecture_area_name);
                prefNameTextView.setText(getString(R.string.pref_area_name_chubu_tokai));
            } else if (i == 4) {
                //近畿・北陸
                prefView = (LinearLayout) view.findViewById(R.id.user_info_prefecture_kinki_hokuriku);
                prefNameTextView = (TextView) prefView.findViewById(R.id.component_prefecture_area_name);
                prefNameTextView.setText(getString(R.string.pref_area_name_kinki_hokuriku));
            } else if (i == 5) {
                //中国・四国
                prefView = (LinearLayout) view.findViewById(R.id.user_info_prefecture_chugoku_shikoku);
                prefNameTextView = (TextView) prefView.findViewById(R.id.component_prefecture_area_name);
                prefNameTextView.setText(getString(R.string.pref_area_name_chugoku_shikoku));
            } else if (i == 6) {
                //九州・沖縄
                prefView = (LinearLayout) view.findViewById(R.id.user_info_prefecture_kyusyu_okinawa);
                prefNameTextView = (TextView) prefView.findViewById(R.id.component_prefecture_area_name);
                prefNameTextView.setText(getString(R.string.pref_area_name_kyusyu_okinawa));
            }

            if (prefView != null) {
                final LinearLayout checkboxBlock = (LinearLayout) prefView.findViewById(R.id.component_prefecture_check_box_block);
                checkBoxGridView =
                        (GridView) checkboxBlock.findViewById(R.id.component_prefecture_check_box_grid_view);
                UserInfoGridAdapter adapter = new UserInfoGridAdapter(getActivity(), true);
                adapter.setAdapter(prefectures.get(i));
                checkBoxGridView.setAdapter(adapter);
                if (mPrefectureAdapterList == null) {
                    mPrefectureAdapterList = new ArrayList<>();
                }
                mPrefectureAdapterList.add(adapter);

                RelativeLayout openBtn = (RelativeLayout) prefView.findViewById(R.id.component_prefecture_open_block);
                openBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkboxBlock.getVisibility() == View.VISIBLE) {
                            setCloseAnimation((ImageView) v.findViewById(R.id.component_prefecture_open_btn));
                            checkboxBlock.setVisibility(View.GONE);
                            v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.appBg));
                        } else {
                            v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.prefectureHeader));
                            setOpenAnimation((ImageView) v.findViewById(R.id.component_prefecture_open_btn));
                            checkboxBlock.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
    }

    /**
     * アンケートの回答内容を登録します。
     */
    private void saveQuestionResult() {
        List<QuestionItemModel> selectedPrefectureDataList = new ArrayList<QuestionItemModel>();
        int adapterCountPref = mPrefectureAdapterList.size();
        for (int i = 0; i < adapterCountPref; i++) {
            List<QuestionItemModel> prefectureList = mPrefectureAdapterList.get(i).getAdapterItemList();
            //選択された都道府県をリストにまとめる
            int prefCount = prefectureList.size();
            for (int j = 0; j < prefCount; j++) {
                if (prefectureList.get(j).selected) {
                    //選択されていたらfunnel pushのパラメーター用リストに追加
                    selectedPrefectureDataList.add(prefectureList.get(j));
                }
            }
        }

        //利用したいサービス
        List<QuestionItemModel> selectedServiceDataList = new ArrayList<QuestionItemModel>();
        //mFavListAdapter
        int adapterCountService = mFavListAdapter.getAdapterItemList().size();
        for (int i = 0; i < adapterCountService; i++) {
            if (mFavListAdapter.getAdapterItemList().get(i).selected) {
                selectedServiceDataList.add(mFavListAdapter.getAdapterItemList().get(i));
            }
        }

        final int[] paramPrefectures = new int[selectedPrefectureDataList.size()];
        final int[] paramServices = new int[selectedServiceDataList.size()];

        for (int j = 0; j < selectedPrefectureDataList.size(); j++) {
            paramPrefectures[j] = Integer.parseInt(selectedPrefectureDataList.get(j).id);
        }
        for (int j = 0; j < selectedServiceDataList.size(); j++) {
            paramServices[j] = Integer.parseInt(selectedServiceDataList.get(j).id);
        }

        //Builderの生成
        FunnelPushBuilders.UserAttributesBuilder builder =
                FunnelPushBuilders.newUserAttributesBuilderInstance(getActivity().getApplicationContext());

        //都道府県
        builder.setAttributes(UserAttributes.ATTR_A, 1, paramPrefectures);
        //利用したいサービス
        builder.setAttributes(UserAttributes.ATTR_A, 2, paramServices);


        //Rate属性の設定（numberには1～50を指定）
        //builderの送信（非同期なので完了通知を受け取る場合はListenerを設定する。nullも可）
        FunnelPush.sendUserAttributes(getActivity().getApplicationContext(), builder, new OnUserAttributesSendListener() {

            @Override
            public void onSuccessSendUserAttribute(UserAttributes userAttributes) {

                Log.d(getClass().getSimpleName(), "[ユーザー属性登録]ユーザーID:" + userAttributes.getUserAttribute().getUserId());

                if (getString(R.string.user_info_ex_screen_settings).equals(mExScreen)) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("確認")
                            .setMessage("登録しました。")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //HomeActivityへ発火
                                    EventBusHolder.EVENT_BUS.post(new OnQuestionTapEvent(OnQuestionTapEvent.CLOSE));
                                }
                            })
                            .show();

                } else {
                    //アンケート登録済み
                    ((LMApplication) getActivity().getApplicationContext()).getLmaSharedPreference().setQuestionDone(true);

                    Intent intent = new Intent(getActivity(), HomeActivity.class);

                    //Push通知から起動した場合
                    if (mUrl != null && !mUrl.isEmpty()) {
                        intent.putExtra(FunnelPush.FUNNEL_PUSH_MESSAGE_URL, mUrl);
                    }

                    startActivity(intent);
                    getActivity().finish();
                }

            }

            @Override
            public void onFailSendUserAttribute(String s, int i) {
                //Firebase イベントをレポートする
                String errContent = "ユーザー属性設定失敗、"+ s+ "、 コード "+ i;
                String nextAction = FirebaseUtil.getActionDialog(getString(R.string.net_error_title),
                        getString(R.string.net_error_msg));
                FirebaseUtil.logEventFunnelPushOption(getActivity(), QuestionFragment.class.getName(),  errContent, nextAction);
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.net_error_title))
                        .setMessage(R.string.net_error_msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.dialog_retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //HomeActivityへ発火
                                EventBusHolder.EVENT_BUS.post(new OnQuestionTapEvent(OnQuestionTapEvent.ERR));
                                saveQuestionResult();
                            }
                        })
                        .setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        })
                        .show();

            }
        });

    }


    /**
     * アイコンを反転させます。（open）
     *
     * @param icon
     */
    private void setOpenAnimation(ImageView icon) {
        RotateAnimation rotate =
                new RotateAnimation(getResources().getInteger(R.integer.list_open_from),
                        getResources().getInteger(R.integer.list_open_to),
                        icon.getWidth() / 2, icon.getHeight() / 2);
        rotate.setDuration(300);
        //アニメーション後の状態を維持する
        rotate.setFillAfter(true);
        //制御を有効化
        rotate.setFillEnabled(true);
        icon.startAnimation(rotate);
    }

    /**
     * アイコンを反転させます。（close）
     *
     * @param icon
     */
    private void setCloseAnimation(ImageView icon) {
        RotateAnimation rotate =
                new RotateAnimation(getResources().getInteger(R.integer.list_close_from),
                        getResources().getInteger(R.integer.list_close_to), icon.getWidth() / 2,
                        icon.getHeight() / 2);
        rotate.setDuration(300);
        icon.startAnimation(rotate);
    }

    private List<QuestionItemModel> setFavoritesList() {
        List<QuestionItemModel> favorites = new ArrayList<>();
        String[] savedServices = mSavedServices;

        QuestionItemModel fav1 = new QuestionItemModel();
        fav1.id = getString(R.string.fav_service_id_win_rate_up);
        fav1.label = getString(R.string.user_info_fav_service_win_rate_up);
        fav1.selected = isSavedService(savedServices, fav1.id);
        favorites.add(fav1);
        QuestionItemModel fav2 = new QuestionItemModel();
        fav2.id = getString(R.string.fav_service_id_leisure_ticket);
        fav2.label = getString(R.string.user_info_fav_service_leisure_ticket);
        fav2.selected = isSavedService(savedServices, fav2.id);
        favorites.add(fav2);
        QuestionItemModel fav3 = new QuestionItemModel();
        fav3.id = getString(R.string.fav_service_id_movie_ticket);
        fav3.label = getString(R.string.user_info_fav_service_movie_ticket);
        fav3.selected = isSavedService(savedServices, fav3.id);
        favorites.add(fav3);
//        QuestionItemModel fav4 = new QuestionItemModel();
//        fav4.id = getString(R.string.fav_service_id_ticket_guard);
//        fav4.label = getString(R.string.user_info_fav_service_ticket_guard);
//        fav4.selected = isSavedService(savedServices, fav4.id);
//        favorites.add(fav4);
        QuestionItemModel fav5 = new QuestionItemModel();
        fav5.id = getString(R.string.fav_service_id_coupon);
        fav5.label = getString(R.string.user_info_fav_service_coupon);
        fav5.selected = isSavedService(savedServices, fav5.id);
        favorites.add(fav5);
        QuestionItemModel fav6 = new QuestionItemModel();
        fav6.id = getString(R.string.fav_service_id_present);
        fav6.label = getString(R.string.user_info_fav_service_present);
        fav6.selected = isSavedService(savedServices, fav6.id);
        favorites.add(fav6);
        QuestionItemModel fav8 = new QuestionItemModel();
        fav8.id = getString(R.string.fav_service_id_free_magazine);
        fav8.label = getString(R.string.user_info_fav_service_free_magazine);
        fav8.selected = isSavedService(savedServices, fav8.id);
        favorites.add(fav8);
        QuestionItemModel fav9 = new QuestionItemModel();
        fav9.id = getString(R.string.fav_service_id_video);
        fav9.label = getString(R.string.user_info_fav_service_video);
        fav9.selected = isSavedService(savedServices, fav9.id);
        favorites.add(fav9);
        return favorites;
    }


    /**
     * @return エリア別のと都道府県リスト
     */
    private List<List<QuestionItemModel>> setPrefectureList() {

        List<List<QuestionItemModel>> prefectures = new ArrayList<>();
        String[] savedPrefectures = mSavedPrefectures;

        //北海道:北海道
        List<QuestionItemModel> prefHokkaido = new ArrayList<>();
        QuestionItemModel hokkaido = new QuestionItemModel();
        hokkaido.id = getString(R.string.pref_id_hokkaido);
        hokkaido.label = getString(R.string.pref_name_hokkaido);
        hokkaido.selected = isSavedPref(savedPrefectures, hokkaido.id);
        prefHokkaido.add(hokkaido);
        prefectures.add(prefHokkaido);

        //東北:青森、岩手、宮城、秋田、山形、福島
        List<QuestionItemModel> prefTohoku = new ArrayList<>();
        QuestionItemModel aomori = new QuestionItemModel();
        aomori.id = getString(R.string.pref_id_aomori);
        aomori.label = getString(R.string.pref_name_aomori);
        aomori.selected = isSavedPref(savedPrefectures, aomori.id);
        prefTohoku.add(aomori);
        QuestionItemModel iwate = new QuestionItemModel();
        iwate.id = getString(R.string.pref_id_iwate);
        iwate.label = getString(R.string.pref_name_iwate);
        iwate.selected = isSavedPref(savedPrefectures, iwate.id);
        prefTohoku.add(iwate);
        QuestionItemModel miyagi = new QuestionItemModel();
        miyagi.id = getString(R.string.pref_id_miyagi);
        miyagi.label = getString(R.string.pref_name_miyagi);
        miyagi.selected = isSavedPref(savedPrefectures, miyagi.id);
        prefTohoku.add(miyagi);
        QuestionItemModel akita = new QuestionItemModel();
        akita.id = getString(R.string.pref_id_akita);
        akita.label = getString(R.string.pref_name_akita);
        akita.selected = isSavedPref(savedPrefectures, akita.id);
        prefTohoku.add(akita);
        QuestionItemModel yamagata = new QuestionItemModel();
        yamagata.id = getString(R.string.pref_id_yamagata);
        yamagata.label = getString(R.string.pref_name_yamagata);
        yamagata.selected = isSavedPref(savedPrefectures, yamagata.id);
        prefTohoku.add(yamagata);
        QuestionItemModel fukushima = new QuestionItemModel();
        fukushima.id = getString(R.string.pref_id_fukushima);
        fukushima.label = getString(R.string.pref_name_fukushima);
        fukushima.selected = isSavedPref(savedPrefectures, fukushima.id);
        prefTohoku.add(fukushima);
        prefectures.add(prefTohoku);

        //関東・甲信越:茨城、栃木、群馬、埼玉、千葉、東京、神奈川、新潟、山梨、長野
        List<QuestionItemModel> prefKanto = new ArrayList<>();
        QuestionItemModel ibaraki = new QuestionItemModel();
        ibaraki.id = getString(R.string.pref_id_ibaraki);
        ibaraki.label = getString(R.string.pref_name_ibaraki);
        ibaraki.selected = isSavedPref(savedPrefectures, ibaraki.id);
        prefKanto.add(ibaraki);
        QuestionItemModel tochigi = new QuestionItemModel();
        tochigi.id = getString(R.string.pref_id_tochigi);
        tochigi.label = getString(R.string.pref_name_tochigi);
        tochigi.selected = isSavedPref(savedPrefectures, tochigi.id);
        prefKanto.add(tochigi);
        QuestionItemModel gunma = new QuestionItemModel();
        gunma.id = getString(R.string.pref_id_gunma);
        gunma.label = getString(R.string.pref_name_gunma);
        gunma.selected = isSavedPref(savedPrefectures, gunma.id);
        prefKanto.add(gunma);
        QuestionItemModel saitama = new QuestionItemModel();
        saitama.id = getString(R.string.pref_id_saitama);
        saitama.label = getString(R.string.pref_name_saitama);
        saitama.selected = isSavedPref(savedPrefectures, saitama.id);
        prefKanto.add(saitama);
        QuestionItemModel chiba = new QuestionItemModel();
        chiba.id = getString(R.string.pref_id_chiba);
        chiba.label = getString(R.string.pref_name_chiba);
        chiba.selected = isSavedPref(savedPrefectures, chiba.id);
        prefKanto.add(chiba);
        QuestionItemModel tokyo = new QuestionItemModel();
        tokyo.id = getString(R.string.pref_id_tokyo);
        tokyo.label = getString(R.string.pref_name_tokyo);
        tokyo.selected = isSavedPref(savedPrefectures, tokyo.id);
        prefKanto.add(tokyo);
        QuestionItemModel kanagawa = new QuestionItemModel();
        kanagawa.id = getString(R.string.pref_id_kanagawa);
        kanagawa.label = getString(R.string.pref_name_kanagawa);
        kanagawa.selected = isSavedPref(savedPrefectures, kanagawa.id);
        prefKanto.add(kanagawa);
        QuestionItemModel niigata = new QuestionItemModel();
        niigata.id = getString(R.string.pref_id_niigata);
        niigata.label = getString(R.string.pref_name_niigata);
        niigata.selected = isSavedPref(savedPrefectures, niigata.id);
        prefKanto.add(niigata);
        QuestionItemModel yamanashi = new QuestionItemModel();
        yamanashi.id = getString(R.string.pref_id_yamanashi);
        yamanashi.label = getString(R.string.pref_name_yamanashi);
        yamanashi.selected = isSavedPref(savedPrefectures, yamanashi.id);
        prefKanto.add(yamanashi);
        QuestionItemModel nagano = new QuestionItemModel();
        nagano.id = getString(R.string.pref_id_nagano);
        nagano.label = getString(R.string.pref_name_nagano);
        nagano.selected = isSavedPref(savedPrefectures, nagano.id);
        prefKanto.add(nagano);
        prefectures.add(prefKanto);

        //中部・東海:岐阜、静岡、愛知、三重
        List<QuestionItemModel> prefChubu = new ArrayList<>();
        QuestionItemModel gifu = new QuestionItemModel();
        gifu.id = getString(R.string.pref_id_gihu);
        gifu.label = getString(R.string.pref_name_gihu);
        gifu.selected = isSavedPref(savedPrefectures, gifu.id);
        prefChubu.add(gifu);
        QuestionItemModel shizuoka = new QuestionItemModel();
        shizuoka.id = getString(R.string.pref_id_shizuoka);
        shizuoka.label = getString(R.string.pref_name_shizuoka);
        shizuoka.selected = isSavedPref(savedPrefectures, shizuoka.id);
        prefChubu.add(shizuoka);
        QuestionItemModel aichi = new QuestionItemModel();
        aichi.id = getString(R.string.pref_id_aichi);
        aichi.label = getString(R.string.pref_name_aichi);
        aichi.selected = isSavedPref(savedPrefectures, aichi.id);
        prefChubu.add(aichi);
        QuestionItemModel mie = new QuestionItemModel();
        mie.id = getString(R.string.pref_id_mie);
        mie.label = getString(R.string.pref_name_mie);
        mie.selected = isSavedPref(savedPrefectures, mie.id);
        prefChubu.add(mie);
        prefectures.add(prefChubu);

        //近畿・北陸:富山、石川、福井、滋賀、京都、大阪、兵庫、奈良、和歌山
        List<QuestionItemModel> prefKinki = new ArrayList<>();
        QuestionItemModel toyama = new QuestionItemModel();
        toyama.id = getString(R.string.pref_id_toyama);
        toyama.label = getString(R.string.pref_name_toyama);
        toyama.selected = isSavedPref(savedPrefectures, toyama.id);
        prefKinki.add(toyama);
        QuestionItemModel ishikawa = new QuestionItemModel();
        ishikawa.id = getString(R.string.pref_id_ishikawa);
        ishikawa.label = getString(R.string.pref_name_ishikawa);
        ishikawa.selected = isSavedPref(savedPrefectures, ishikawa.id);
        prefKinki.add(ishikawa);
        QuestionItemModel fukui = new QuestionItemModel();
        fukui.id = getString(R.string.pref_id_fukui);
        fukui.label = getString(R.string.pref_name_fukui);
        fukui.selected = isSavedPref(savedPrefectures, fukui.id);
        prefKinki.add(fukui);
        QuestionItemModel shiga = new QuestionItemModel();
        shiga.id = getString(R.string.pref_id_shiga);
        shiga.label = getString(R.string.pref_name_shiga);
        shiga.selected = isSavedPref(savedPrefectures, shiga.id);
        prefKinki.add(shiga);
        QuestionItemModel kyoto = new QuestionItemModel();
        kyoto.id = getString(R.string.pref_id_kyoto);
        kyoto.label = getString(R.string.pref_name_kyoto);
        kyoto.selected = isSavedPref(savedPrefectures, kyoto.id);
        prefKinki.add(kyoto);
        QuestionItemModel osaka = new QuestionItemModel();
        osaka.id = getString(R.string.pref_id_osaka);
        osaka.label = getString(R.string.pref_name_osaka);
        osaka.selected = isSavedPref(savedPrefectures, osaka.id);
        prefKinki.add(osaka);
        QuestionItemModel hyogo = new QuestionItemModel();
        hyogo.id = getString(R.string.pref_id_hyogo);
        hyogo.label = getString(R.string.pref_name_hyogo);
        hyogo.selected = isSavedPref(savedPrefectures, hyogo.id);
        prefKinki.add(hyogo);
        QuestionItemModel nara = new QuestionItemModel();
        nara.id = getString(R.string.pref_id_nara);
        nara.label = getString(R.string.pref_name_nara);
        nara.selected = isSavedPref(savedPrefectures, nara.id);
        prefKinki.add(nara);
        QuestionItemModel wakayama = new QuestionItemModel();
        wakayama.id = getString(R.string.pref_id_wakayama);
        wakayama.label = getString(R.string.pref_name_wakayama);
        wakayama.selected = isSavedPref(savedPrefectures, wakayama.id);
        prefKinki.add(wakayama);
        prefectures.add(prefKinki);

        //中国・四国:鳥取、島根、岡山、広島、山口、徳島、香川、愛媛、高知
        List<QuestionItemModel> prefChugoku = new ArrayList<>();
        QuestionItemModel tottori = new QuestionItemModel();
        tottori.id = getString(R.string.pref_id_tottori);
        tottori.label = getString(R.string.pref_name_tottori);
        tottori.selected = isSavedPref(savedPrefectures, tottori.id);
        prefChugoku.add(tottori);
        QuestionItemModel shimane = new QuestionItemModel();
        shimane.id = getString(R.string.pref_id_shimane);
        shimane.label = getString(R.string.pref_name_shimane);
        shimane.selected = isSavedPref(savedPrefectures, shimane.id);
        prefChugoku.add(shimane);
        QuestionItemModel okayama = new QuestionItemModel();
        okayama.id = getString(R.string.pref_id_okayama);
        okayama.label = getString(R.string.pref_name_okayama);
        okayama.selected = isSavedPref(savedPrefectures, okayama.id);
        prefChugoku.add(okayama);
        QuestionItemModel hiroshima = new QuestionItemModel();
        hiroshima.id = getString(R.string.pref_id_hiroshima);
        hiroshima.label = getString(R.string.pref_name_hiroshima);
        hiroshima.selected = isSavedPref(savedPrefectures, hiroshima.id);
        prefChugoku.add(hiroshima);
        QuestionItemModel yamaguchi = new QuestionItemModel();
        yamaguchi.id = getString(R.string.pref_id_yamaguchi);
        yamaguchi.label = getString(R.string.pref_name_yamaguchi);
        yamaguchi.selected = isSavedPref(savedPrefectures, yamaguchi.id);
        prefChugoku.add(yamaguchi);
        QuestionItemModel tokushima = new QuestionItemModel();
        tokushima.id = getString(R.string.pref_id_tokushima);
        tokushima.label = getString(R.string.pref_name_tokushima);
        tokushima.selected = isSavedPref(savedPrefectures, tokushima.id);
        prefChugoku.add(tokushima);
        QuestionItemModel kagawa = new QuestionItemModel();
        kagawa.id = getString(R.string.pref_id_kagawa);
        kagawa.label = getString(R.string.pref_name_kagawa);
        kagawa.selected = isSavedPref(savedPrefectures, kagawa.id);
        prefChugoku.add(kagawa);
        QuestionItemModel ehime = new QuestionItemModel();
        ehime.id = getString(R.string.pref_id_ehime);
        ehime.label = getString(R.string.pref_name_ehime);
        ehime.selected = isSavedPref(savedPrefectures, ehime.id);
        prefChugoku.add(ehime);
        QuestionItemModel kochi = new QuestionItemModel();
        kochi.id = getString(R.string.pref_id_kochi);
        kochi.label = getString(R.string.pref_name_kochi);
        kochi.selected = isSavedPref(savedPrefectures, kochi.id);
        prefChugoku.add(kochi);
        prefectures.add(prefChugoku);

        //九州・沖縄:福岡、佐賀、長崎、熊本、大分、宮崎、鹿児島、沖縄
        List<QuestionItemModel> prefKyusyu = new ArrayList<>();
        QuestionItemModel fukuoka = new QuestionItemModel();
        fukuoka.id = getString(R.string.pref_id_fukuoka);
        fukuoka.label = getString(R.string.pref_name_fukuoka);
        fukuoka.selected = isSavedPref(savedPrefectures, fukuoka.id);
        prefKyusyu.add(fukuoka);
        QuestionItemModel saga = new QuestionItemModel();
        saga.id = getString(R.string.pref_id_saga);
        saga.label = getString(R.string.pref_name_saga);
        saga.selected = isSavedPref(savedPrefectures, saga.id);
        prefKyusyu.add(saga);
        QuestionItemModel nagasaki = new QuestionItemModel();
        nagasaki.id = getString(R.string.pref_id_nagasaki);
        nagasaki.label = getString(R.string.pref_name_nagasaki);
        nagasaki.selected = isSavedPref(savedPrefectures, nagasaki.id);
        prefKyusyu.add(nagasaki);
        QuestionItemModel kumamoto = new QuestionItemModel();
        kumamoto.id = getString(R.string.pref_id_kumamoto);
        kumamoto.label = getString(R.string.pref_name_kumamoto);
        kumamoto.selected = isSavedPref(savedPrefectures, kumamoto.id);
        prefKyusyu.add(kumamoto);
        QuestionItemModel oita = new QuestionItemModel();
        oita.id = getString(R.string.pref_id_oita);
        oita.label = getString(R.string.pref_name_oita);
        oita.selected = isSavedPref(savedPrefectures, oita.id);
        prefKyusyu.add(oita);
        QuestionItemModel miyazaki = new QuestionItemModel();
        miyazaki.id = getString(R.string.pref_id_miyazaki);
        miyazaki.label = getString(R.string.pref_name_miyazaki);
        miyazaki.selected = isSavedPref(savedPrefectures, miyazaki.id);
        prefKyusyu.add(miyazaki);
        QuestionItemModel kagoshima = new QuestionItemModel();
        kagoshima.id = getString(R.string.pref_id_kagoshima);
        kagoshima.label = getString(R.string.pref_name_kagoshima);
        kagoshima.selected = isSavedPref(savedPrefectures, kagoshima.id);
        prefKyusyu.add(kagoshima);
        QuestionItemModel okinawa = new QuestionItemModel();
        okinawa.id = getString(R.string.pref_id_okinawa);
        okinawa.label = getString(R.string.pref_name_okinawa);
        okinawa.selected = isSavedPref(savedPrefectures, okinawa.id);
        prefKyusyu.add(okinawa);
        prefectures.add(prefKyusyu);

        return prefectures;

    }

    private boolean isSavedPref(String[] savedPrefectures, String prefId) {
        if (savedPrefectures == null) {
            return false;
        }
        for (String pref : savedPrefectures) {
            if (pref.equals(prefId)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSavedService(String[] savedServices, String serviceId) {
        if (savedServices == null) {
            return false;
        }
        for (String service : savedServices) {
            if (service.equals(serviceId)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
        if (getString(R.string.user_info_ex_screen_settings).equals(mExScreen)) {
            return getString(R.string.setting_title_user_info);
        }
        return super.getTitle();
    }

    @OnClick(R.id.iv_back)
    public void onBackClick() {
        EventBusHolder.EVENT_BUS.post(new OnSettingMenuCloseTapEvent(""));
    }

}
