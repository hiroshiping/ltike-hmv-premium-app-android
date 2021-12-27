package jp.co.lhe.lhememberapp.network.models;

import com.google.gson.annotations.SerializedName;

public class LoginResult extends BaseResponseBean {
    public Response response;

    public class Response extends BaseResponseBean{
        @SerializedName("contract_course")
        public String contractCourse;

        @SerializedName("lcus_no")
        public String lcusNo;

        @SerializedName("pay_status")
        public String payStatus;

        @SerializedName("auth_result")
        public String authResult;
    }
}
