package com.lux.service;

import java.util.HashMap;
import java.util.Optional;

/**
 * Describes a set of interfaces for calling remote weather api methods
 * @author leolu
 */
public interface IRemoteDataService {

    Optional<HashMap<String, String>> getProvinceCode();

    Optional<HashMap<String, String>> getCityCodeByProvince(String province);

    Optional<HashMap<String, String>> getCountyCodeByCity(String province, String city);

    Optional<Integer> getWeatherOfCounty(String province,String city,String county);
}
