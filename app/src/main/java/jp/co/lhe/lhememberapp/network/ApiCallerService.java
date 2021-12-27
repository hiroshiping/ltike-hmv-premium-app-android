package jp.co.lhe.lhememberapp.network;

import io.reactivex.Observable;
import jp.co.lhe.lhememberapp.network.models.LayoutSettingModel;
import jp.co.lhe.lhememberapp.network.models.LoginResult;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiCallerService {

    // headerの設定
    @Headers({
        "Content-Type:application/x-www-form-urlencoded", "User-Agent:LheMemberApp"
    })

    /**
     * 有料会員アプリ向け認証API
     */
    @FormUrlEncoded
    @POST("pn/async/auth/")
    Observable<LoginResult> appLogin(@Field("email") String email, @Field("password") String password);

    /**
     * レイアウトJSON
     * @return
     */
    @GET
    Observable<LayoutSettingModel> layoutJson(@Url String url);

}
