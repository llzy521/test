package com.lux.service;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.util.Optional;

/**
 * @author leolu
 * @title: IWeatherInfoService
 * @projectName test
 * @description: TODO
 * @date 2022/1/144:12 PM
 */
public interface IWeatherInfoService {

    Optional<Integer> getTemperature(@NotNull String province, @NotNull String city, @NotNull String county);
}
