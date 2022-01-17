package com.lux;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.lux.exception.BusinessException;
import com.lux.service.IWeatherInfoService;
import com.lux.service.RemoteDataService;
import com.lux.service.WeatherInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author leolu
 * @title: WeatherInfoTest
 * @projectName test
 * @description: TODO
 * @date 2022/1/144:24 PM
 */
public class WeatherInfoTest {


    static IWeatherInfoService iWeatherInfoService;

    @BeforeAll
    public static void init() {
        iWeatherInfoService = new WeatherInfoService();
    }

    @Test
    public void testWeatherData() {
        Integer integer = iWeatherInfoService.getTemperature("10119", "04", "05")
            .get();
        System.out.println(integer);
    }

    @Test
    public void testWrongParams(){
        // null params
        Assertions.assertEquals(
            Assertions.assertThrows(BusinessException.class,() -> iWeatherInfoService.getTemperature(null, "04", "05")).getMessage(),
            "params province,city,county cannot be null"
        );
        // invalid province code
        Assertions.assertEquals(
            Assertions.assertThrows(BusinessException.class,() -> iWeatherInfoService.getTemperature("123123", "04", "05")).getMessage(),
            "invalid remote data or province code: 123123"
        );
        //
        Assertions.assertEquals(
            Assertions.assertThrows(BusinessException.class,() -> iWeatherInfoService.getTemperature("10119", "024", "05")).getMessage()
            ,"invalid remote data or city code: 024"
        );
        // invalid city code
        Assertions.assertEquals(
            Assertions.assertThrows(BusinessException.class,() -> iWeatherInfoService.getTemperature("10119", "04", "052"))
                .getMessage(),"invalid remote data or county code: 052"
        );
    }

    @Test
    public void testTPS() {
        final int taskNum  = 200;
        ListeningExecutorService es = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(taskNum));
        List<Callable<Optional<Integer>>> taskList = new ArrayList<Callable<Optional<Integer>>>();
        for (int i = 0; i < taskNum; i++) {
            final int taskId = i;
            taskList.add(() -> {
                Optional<Integer> temperature = iWeatherInfoService.getTemperature("10119", "04", "05");
                return temperature;
            });
        }
        try {
            LocalDateTime start = LocalDateTime.now();
            System.out.printf("test start time: %s \n", start);
            List<Future<Optional<Integer>>> futures = es.invokeAll(taskList);

            futures.forEach(optionalFuture -> {
                try {
                    optionalFuture.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
            LocalDateTime end = LocalDateTime.now();
            System.out.printf("test end time: %s \n", end);
            System.out.printf("The test takes %s seconds \n",Duration.between(start,end).getSeconds());
            System.out.printf("The actual value should be closer to 20 seconds \n",Duration.between(start,end).getSeconds());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
