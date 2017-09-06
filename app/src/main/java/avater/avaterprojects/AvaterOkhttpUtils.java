package avater.avaterprojects;


import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 作者:Avater
 * 日期： 2017-09-03.
 * 说明：Okhttpls工具类
 */

public class AvaterOkhttpUtils {

    private OkHttpClient client;

    private static AvaterOkhttpUtils instance;

    private Handler mDelivery;

    private Gson mGson;

    private AvaterOkhttpUtils() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        //.addInterceptor();添加自己的拦截器
        client = builder.build();
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static AvaterOkhttpUtils getInstance() {
        if (instance == null) {
            synchronized (AvaterOkhttpUtils.class) {
                instance = new AvaterOkhttpUtils();
            }
        }
        return instance;
    }

    /*----------------------------------------------同步get请求-------------------------------------------------*/
    private Response _getAsync(String url) throws IOException {
        final Request request = new Request
                .Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();//同步方法.execute()
        return response;
    }

    private String _getAsString(String url) throws IOException {
        Response response = _getAsync(url);
        return response.body().string();
    }

    /*----------------------------------------------------异步Post请求---------------------------------------*/
    private void _postAsyn(String url, final AvaterResultCallBack callBack, Param... params) {
        Request request = buildPostRequest(url, params);
        AvaterResultCallback(callBack, request);
    }

    private Request buildPostRequest(String url, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        RequestBody fromBody;
        FormBody.Builder builder = new FormBody.Builder();
        for (Param p : params) {
            builder.add(p.key, p.value);
        }
        fromBody = builder.build();
        return new Request.Builder().url(url).post(fromBody).build();
    }

    private void _postAsyn(String url, final AvaterResultCallBack callBack, Map<String, String> params) {
        Param[] ps = mapToParams(params);
        Request request = buildPostRequest(url, ps);
        AvaterResultCallback(callBack, request);
    }

    public static void postAsyn(String url, final AvaterResultCallBack callBack, Param... params) {
        getInstance()._postAsyn(url, callBack, params);
    }

    private Param[] mapToParams(Map<String, String> params) {
        if (params == null) {
            return new Param[0];
        }
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    public static String getAsString(String url) throws IOException {
        return getInstance()._getAsString(url);
    }


    /*-----------------------------------------自定义回调接口------------------------------------------------*/

    private void AvaterResultCallback(final AvaterResultCallBack callback, final Request request) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailureStringMessageCallBack(request, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    final String string = response.body().string();
                    if (callback.type == String.class) {
                        sendSuccessResultCall(string, callback);
                    } else {
                        Object o = mGson.fromJson(string, callback.type);
                        sendSuccessResultCall(o, callback);
                    }
                } catch (IOException io) {
                    sendFailureStringMessageCallBack(response.request(), io, callback);
                } catch (com.google.gson.JsonParseException jpe) {
                    sendFailureStringMessageCallBack(response.request(), jpe, callback);
                }
            }
        });
    }

    private void sendSuccessResultCall(Object string, AvaterResultCallBack callback) {
        if (callback != null) {
            callback.onResponse(string);
        }
    }

    private void sendFailureStringMessageCallBack(Request request, Exception e, AvaterResultCallBack callback) {
        if (callback != null) {
            callback.onError(request, e);
        }
    }

    public static abstract class AvaterResultCallBack<T> {
        Type type;

        public AvaterResultCallBack() {
            type = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getSuperclass();
            if (subclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType pt = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(pt.getActualTypeArguments()[0]);
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response);
    }

    public static class Param {
        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

}
