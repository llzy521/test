package com.lux;

import com.alibaba.fastjson.JSONObject;
import com.lux.constant.WeatherUrls;
import com.lux.http.RetryInterceptor;
import com.lux.service.IRemoteDataService;
import com.lux.service.RemoteDataService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author leolu
 * @title: RemoteDataTest
 * @projectName test
 * @description: TODO
 * @date 2022/1/14 3:02 PM
 */
public class RemoteDataTest {

    static IRemoteDataService iRemoteDataService;

    @BeforeAll
    public static void init(){
        iRemoteDataService = new RemoteDataService();
    }

    @Test
    public void testProvinceData(){
        iRemoteDataService.getProvinceCode()
            .ifPresent(stringStringHashMap -> {
                Assertions.assertFalse(stringStringHashMap.isEmpty(),"remote province data is not null");
            });
    }

    @Test
    public void testCityData(){
        String province = "10119";
        iRemoteDataService
            .getCityCodeByProvince(province)
            .ifPresent(stringStringHashMap -> {
                Assertions.assertFalse(stringStringHashMap.isEmpty(),"returns correct city data");
            });
    }

    @Test
    public void testCountyData(){
        String province = "10119";
        String city = "04";
        iRemoteDataService
            .getCountyCodeByCity(province,city)
            .ifPresent(stringStringHashMap -> {
                Assertions.assertFalse(stringStringHashMap.isEmpty(),"returns correct county data");
            });
    }

    @Test
    public void testWeatherData(){
        String province = "10119";
        String city = "04";
        String county ="01";
        iRemoteDataService.getWeatherOfCounty(province,city,county)
            .ifPresent(System.out::println);
    }

    @Test
    public void testWrongUrl(){
        String province = "101192";
        String city = "043";
        String county ="01";
        Optional<Integer> weatherOfCounty = iRemoteDataService.getWeatherOfCounty(province, city, county);
        Assertions.assertFalse(weatherOfCounty.isPresent(),"Bad arguments lead to empty results");
    }

    @Test
    public void testRetry() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new RetryInterceptor())
            .build();

            Response response = client.newCall(new Request.Builder()
                    .url("http://giasfasdf.com")
                    .get()
                    .build())
                .execute();
            Assertions.assertFalse(response.isSuccessful(),"response cannot be success");
    }

}
