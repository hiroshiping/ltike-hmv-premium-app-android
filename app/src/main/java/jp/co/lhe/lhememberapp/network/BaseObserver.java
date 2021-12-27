package jp.co.lhe.lhememberapp.network;

import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import jp.co.lhe.lhememberapp.network.models.BaseResponseBean;

public abstract class  BaseObserver<T extends BaseResponseBean> implements Observer<T> {

    @Override
    public void onNext(T response) {

    }

    @Override
    public void onError(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException) {
            onSocketTimeout();
        } else {
            onNetError(throwable);
        }
    }

    @Override
    public void onComplete() {

    }

    protected void onSocketTimeout() {}
    protected void onNetError(Throwable throwable) {}
}
