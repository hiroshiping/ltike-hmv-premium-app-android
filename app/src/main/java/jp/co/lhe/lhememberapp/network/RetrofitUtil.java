package jp.co.lhe.lhememberapp.network;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.co.lhe.lhememberapp.R;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {
    private ApiCallerService mAPI;

    private RetrofitUtil() {
        OkHttpClient client = LMApplication.getApplication().getOkhttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LMApplication.getApplication().getString(R.string.lweb_login_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        mAPI = retrofit.create(ApiCallerService.class);
    }

    private static RetrofitUtil mInstance;

    public static RetrofitUtil getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitUtil.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitUtil();
                }
            }
        }
        return mInstance;
    }

    public ApiCallerService getAPI() {
        return mAPI;
    }
}
