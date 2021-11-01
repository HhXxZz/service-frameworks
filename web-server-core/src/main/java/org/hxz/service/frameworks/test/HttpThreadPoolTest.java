package org.hxz.service.frameworks.test;

import org.hxz.service.frameworks.utils.HttpClientHelper;
import com.alibaba.nacos.common.utils.UuidUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hxz on 2021/10/25 17:17.
 */

public class HttpThreadPoolTest {



    public static void main(String[] args){
        try {

            ExecutorService executorService = Executors.newFixedThreadPool(4);
            String url = "http://127.0.0.1:8686/service/info";
            //String url = "http://localhost:8088/";
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                executorService.submit(()->{
                    Map<String, String> map = new HashMap<>();
                    int r = new Random().nextInt(1000);
                    map.put("id", String.valueOf(r));
                    map.put("data", UuidUtils.generateUuid());
                    String res = null;
                    try {
                        res = HttpClientHelper.get(url, map);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(res);
                });
            }
            executorService.shutdown();
            while(true){
                if(executorService.isTerminated()){
                    System.out.println("所有的子线程都结束了！");
                    break;
                }
                Thread.sleep(100);
            }
            long end = System.currentTimeMillis();
            System.out.println(end-start);

            // Redis QPS
            // 单台 8thread  5872
            // 两台 8thread  4705
            // 三台 8thread  4471
            // 单台 4thread  6000
            // 两台 4thread  4971
            // 单台 20thread  6490

            // mysql QPS
            // 单台 4thread  6000
            // 两台 4thread  4900


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
