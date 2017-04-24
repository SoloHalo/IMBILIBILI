package com.cql.imbilibili.data;

import com.cql.imbilibili.model.BiliBiliResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by home on 2017/1/24.
 * 检查结果的合法性
 */


public class BilibiliResponseHandler {
    public static <T extends BiliBiliResponse<R>,R> BilibiliResultFunction<T,R> handlerResult(){
        return new BilibiliResultFunction<>();
    }

    private static class BilibiliResultFunction<T extends BiliBiliResponse<R>, R> implements Function<T, ObservableSource<R>> {

        @Override
        public ObservableSource<R> apply(T t) throws Exception {
            if (t.isSuccess()) {//确保拿到数据
                return Observable.just(t.getResult());
            } else {
                return Observable.error(new ApiException(t.getCode(), t.getMessage()));
            }
        }
    }
}
