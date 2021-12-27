package jp.co.lhe.lhememberapp.utils;

import jp.co.lhe.lhememberapp.LMApplication;
import jp.funnelpush.sdk.manager.SharedPreferenceManager;

public class PushUtil {
    private void PushUtil(){

    }
    private static PushUtil mInstance;

    public static PushUtil getInstance() {
        if (mInstance==null){
            synchronized (PushUtil.class){
                if (mInstance == null){
                    mInstance = new PushUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * true == has been init
     * false == not init
     * @return
     */
    public Boolean isInited(){
        SharedPreferenceManager manager = SharedPreferenceManager.getInstance(LMApplication.getApplication());
        String userId = manager.getString("KEY_USER_ID", null);
        return null!=userId;
    }
}
