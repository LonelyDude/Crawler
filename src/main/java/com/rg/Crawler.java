package com.rg;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class Crawler {

    private static final String MAX_PARALLEL_REQUESTS_ENV = "crawler.maxParallelRequests";
    private static final String REQUEST_DELAY_MS_ENV = "crawler.requestDelayMs";

    private int maxParallelRequests = 1;
    private int requestDelayMs = 1000;

    private ThreadPoolExecutor threadPool;

    public Crawler(){
        String maxParallelRequestsEnv = System.getenv(MAX_PARALLEL_REQUESTS_ENV);
        if(maxParallelRequestsEnv != null){
            maxParallelRequests = Integer.valueOf(maxParallelRequestsEnv);
        }

        String requestDelayMsEnv = System.getenv(REQUEST_DELAY_MS_ENV);
        if(requestDelayMsEnv != null){
            requestDelayMs = Integer.valueOf(requestDelayMsEnv);
        }
    }

    public void crawl(File file){

    }

    public void crawl(List<URL> urls){

    }

}
