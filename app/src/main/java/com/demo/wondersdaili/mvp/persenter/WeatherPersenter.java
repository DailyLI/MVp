package com.demo.wondersdaili.mvp.persenter;

import com.demo.wondersdaili.mvp.api.Api;
import com.demo.wondersdaili.mvp.api.WeatherBean;
import com.demo.wondersdaili.mvp.api.WeatherObserver;
import com.demo.wondersdaili.mvp.base.BaseView;
import com.demo.wondersdaili.mvp.base.BaseWeatherFragment;
import com.demo.wondersdaili.mvp.widget.EmptyLayout;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by daili on 2017/3/9.
 */

public class WeatherPersenter implements WeatherContract.Persenter {
    private Api mApi;
    private BaseWeatherFragment mView;

    public WeatherPersenter(Api api) {
        mApi = api;
    }

    @Override
    public void queryWeather(final int format, final String key, final String cityName, final boolean isRefreshing) {
        WeatherObserver weatherSubsribe = new WeatherObserver() {

            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                mView.showLoading(isRefreshing);
            }

            @Override
            public void onQuerySuccess(WeatherBean weatherBean) {
                mView.hideLoading();
                mView.finishRefresh();
                //获取数据成功显示数据
                mView.loadWeatherData(weatherBean.getResult());
            }

            @Override
            public void onQueryFail(WeatherBean weatherBean) {
                mView.hideLoading();
                if (weatherBean == null) {
                    mView.showNetError(new EmptyLayout.OnRetryListener() {
                        @Override
                        public void onRetry() {
                            queryWeather(format, key, cityName, false);
                        }
                    });
                }else {
                    mView.showNoData();
                }
                mView.loadErrorData(weatherBean);
            }
        };

        Observable<WeatherBean> observable = mApi.queryWeather(format, key, cityName);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(weatherSubsribe);
    }


    @Override
    public void register(BaseView baseView) {
        mView = (BaseWeatherFragment) baseView;
    }

    @Override
    public void unRegister() {
        mView = null;
    }
}
