package avater.avaterprojects;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 作者:Avater
 * 日期： 2017-09-06.
 * 说明：
 */

public class MyOkHttpClient {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public OkHttpClient mOkHttpClient;
    private Gson mGson;

    /**
     * 初始化和超时 设置
     */
    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new ParmesIntercept())
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();
        mGson = new Gson();
    }

    private MyOkHttpClient() {
        init();
    }

    private static MyOkHttpClient instance;

    public static MyOkHttpClient getInstance() {
        if (instance == null) {
            synchronized (MyOkHttpClient.class) {
                instance = new MyOkHttpClient();
            }
        }
        return instance;
    }

    public void postAsynAvater(String url, AvaterResultCallBack callback, HashMap<String, String> dataMap, String tag) {
//        RequestBody requestBody = RequestBody.create(JSON, params);
        RequestBody formBody;
        FormBody.Builder builder = new FormBody.Builder();

        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .tag(tag)
                .addHeader("access_token", "")
                .addHeader("accept", "application/json")
                .post(formBody)
                .build();
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(callback);
        setAvaterResultCallBack(callback, request);
    }

    private void setAvaterResultCallBack(final AvaterResultCallBack callBack, final Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailureMessageCallBack(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String string = response.body().string();
                    if (callBack.type == String.class) {
                        sendSuccessResultCallBack(string, callBack);
                    } else {
                        Object obj = mGson.fromJson(string, callBack.type);
                        sendSuccessResultCallBack(obj, callBack);
                    }
                } catch (IOException io) {
                    sendFailureMessageCallBack(request, io, callBack);
                } catch (com.google.gson.JsonParseException jpe) {
                    sendFailureMessageCallBack(request, jpe, callBack);

                }
            }
        });
    }

    private void sendSuccessResultCallBack(Object string, AvaterResultCallBack callBack) {
        callBack.onResponse(string);
    }

    private void sendFailureMessageCallBack(Request request, Exception e, AvaterResultCallBack callBack) {
        if (callBack == null) {
            return;
        }
        callBack.onError(request, e);
    }

    /**
     * 指定返回类型的函数
     * 2017/09/03
     *
     * @param <T>
     */
    public static abstract class AvaterResultCallBack<T> {
        Type type;

        public AvaterResultCallBack() {
            type = getSuportClassTyoeParamter(getClass());
        }

        static Type getSuportClassTyoeParamter(Class<?> subclass) {
            Type t = subclass.getGenericSuperclass();
            if (t instanceof Class) {
                throw new RuntimeException("Miss tyoe parameter.");
            }
            ParameterizedType pt = (ParameterizedType) t;
            return $Gson$Types.canonicalize(pt.getActualTypeArguments()[0]);
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response);
    }
}
