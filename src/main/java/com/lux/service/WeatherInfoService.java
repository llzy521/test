package com.lux.service;

import com.google.common.util.concurrent.RateLimiter;
import com.lux.exception.BusinessException;
import com.sun.istack.internal.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Semaphore;

/**
 * implementation of interface IWeatherInfoService
 * @author leolu
 * @title: WeatherInfoService
 * @projectName test
 * @description: TODO
 * @date 2022/1/144:12 PM
 */
public class WeatherInfoService implements IWeatherInfoService {

    private final IRemoteDataService iRemoteDataService;
    // test
    // private final RateLimiter rateLimiter = RateLimiter.create(10);
    //
    private final RateLimiter rateLimiter = RateLimiter.create(100);
//    private final Semaphore semaphore = new Semaphore(100);

    public WeatherInfoService() {
        iRemoteDataService = new RemoteDataService();
    }

    @Override
    public Optional<Integer> getTemperature(@NotNull String province, @NotNull String city, @NotNull String county) {
//        Objects.requireNonNull(province,"param province,city,county cannot be null");
//        Objects.requireNonNull(city,"param city cannot be null");
//        Objects.requireNonNull(county,"param county cannot be null");
            if (province == null || city == null || county == null){
                throw new BusinessException("params province,city,county cannot be null");
            }
            rateLimiter.acquire();
//        try {
//            semaphore.acquire();
            // check valid province code
            iRemoteDataService.getProvinceCode()
                .flatMap(provincesMap -> provincesMap.keySet().stream().filter(province::equals).findFirst())
                .orElseThrow(() -> new BusinessException("invalid remote data or province code: " + province));

            // check valid city code
            iRemoteDataService.getCityCodeByProvince(province)
                .flatMap(cityMap -> cityMap.keySet().stream().filter(city::equals).findFirst())
                .orElseThrow(() -> new BusinessException("invalid remote data or city code: " + city));


            // check valid county code
            iRemoteDataService.getCountyCodeByCity(province, city)
                .flatMap(countyMap -> countyMap.keySet().stream().filter(county::equals).findFirst())
                .orElseThrow(() -> new BusinessException("invalid remote data or county code: " + county));
            // get temperature of location

            return Optional.of(iRemoteDataService.getWeatherOfCounty(province, city, county)
                .orElseThrow(() -> new BusinessException("failed to get temperature")));

//        } catch (InterruptedException e) {
//           throw  new BusinessException("failed to get temperature",e);
//        }finally {
//            semaphore.release();
//        }

    }
}
