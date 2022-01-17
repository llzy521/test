package com.lux.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lux.constant.WeatherUrls;
import com.lux.http.RetryInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author leolu
 * @title: RemoteDataService
 * @projectName test
 * @description: IRemoteDataService implementation
 * @date 2022/1/142:02 PM
 */
public class RemoteDataService implements IRemoteDataService {

    private final OkHttpClient httpClient;

    public RemoteDataService() {
        // init httpClient
        this.httpClient = new OkHttpClient.Builder()
            .addInterceptor(new RetryInterceptor())
            .build();
    }

    @Override
    public Optional<HashMap<String, String>> getProvinceCode() {
        Request request = new Request.Builder()
            .url(WeatherUrls.PROVINCE_CODE_URL)
            .get()
            .build();
        try {
            Response response = httpClient.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null) {
                InputStream inputStream = body.byteStream();
                HashMap<String, String> data = JSONObject.parseObject(inputStream, new TypeReference<HashMap<String, String>>() {
                }.getType());
//                System.out.println(data);
                return Optional.ofNullable(data);
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public Optional<HashMap<String, String>> getCityCodeByProvince(String province) {
        Request request = new Request.Builder()
            .url(String.format(WeatherUrls.CITY_CODE_URL, province))
            .get()
            .build();
        try {
            Response response = httpClient.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null) {
                InputStream inputStream = body.byteStream();
                HashMap<String, String> data = JSONObject.parseObject(inputStream, new TypeReference<HashMap<String, String>>() {
                }.getType());
//                System.out.println(data);
                return Optional.ofNullable(data);
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public Optional<HashMap<String, String>> getCountyCodeByCity(String province, String city){
        Request request = new Request.Builder()
            .url(String.format(WeatherUrls.COUNTY_CODE_URL, province,city))
            .get()
            .build();
        try {
            Response response = httpClient.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null) {
                InputStream inputStream = body.byteStream();
                HashMap<String, String> data = JSONObject.parseObject(inputStream, new TypeReference<HashMap<String, String>>() {
                }.getType());
//                System.out.println(data);
                return Optional.of(data);
            }
        } catch (Exception e) {
//            throw new BusinessException("invoke remote api error", e);
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getWeatherOfCounty(String province, String city, String county) {
        Request request = new Request.Builder()
            .url(String.format(WeatherUrls.WEATHER_OF_COUNTY_URL, province,city,county))
            .get()
            .build();
        try {
            Response response = httpClient.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null) {
                InputStream inputStream = body.byteStream();

                HashMap<String, HashMap<String,String>> data = JSONObject.parseObject(inputStream, new TypeReference<HashMap<String, HashMap<String,String>>>() {
                }.getType());
                // get location temperature
//                System.out.println(data);
                return Optional.of(  new BigDecimal(data.get("weatherinfo").get("temp"))
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue());
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }
}
