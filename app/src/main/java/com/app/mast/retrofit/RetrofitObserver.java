package com.app.mast.retrofit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by pawansingh on 01/04/18.
 */

public abstract class RetrofitObserver<T> implements Observer<T> {

    protected abstract void onSuccess(T object);

    protected abstract void onFailure(Throwable e);

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T object) {
        onSuccess(object);
    }

    @Override
    public void onError(Throwable e) {
        onFailure(e);
    }

    @Override
    public void onComplete() {

    }
}
