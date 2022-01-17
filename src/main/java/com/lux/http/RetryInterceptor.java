package com.lux.http;

import com.lux.exception.BusinessException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author leolu
 * @title: RetryInterceptor
 * @projectName test
 * @description: interceptor for retry
 * @date 2022/1/164:43 PM
 */
public class RetryInterceptor implements Interceptor {

    private final int maxRetry = 3;

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        int retryNum = 0;
        Response response = chain.proceed(request);
        while (!response.isSuccessful() && retryNum < maxRetry) {
            retryNum++;
            System.out.println("retryNum=" + retryNum);
            response = chain.proceed(request);
        }
        return response;
    }
}
