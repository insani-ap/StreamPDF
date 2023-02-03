package com.sample.binpdfemail.util;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

public class RetrofitConf {
    public static Retrofit initConf(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .build();
    }

    public interface PdfApiService {
        @GET("/getPDF")
        @Streaming
        Call<ResponseBody> doGetPdf();
    }
}
