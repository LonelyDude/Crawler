package com.rg;

import com.rg.analyser.SiteAnalyser;
import com.rg.utils.ReaderUtils;
import com.rg.profile.Profile;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class Crawler {

    private static final String CONFIG = "hosts.properties";

    private static final String MAX_PARALLEL_REQUESTS_ENV = "crawler.maxParallelRequests";
    private static final String REQUEST_DELAY_MS_ENV = "crawler.requestDelayMs";

    private int maxParallelRequests = 1;
    private int requestDelayMs = 1000;

    private final ScheduledExecutorService threadPool;

    private Map<String, SiteAnalyser> analysers;

    public Crawler(){
        String maxParallelRequestsEnv = System.getenv(MAX_PARALLEL_REQUESTS_ENV);
        if(maxParallelRequestsEnv != null){
            maxParallelRequests = Integer.valueOf(maxParallelRequestsEnv);
        }

        String requestDelayMsEnv = System.getenv(REQUEST_DELAY_MS_ENV);
        if(requestDelayMsEnv != null){
            requestDelayMs = Integer.valueOf(requestDelayMsEnv);
        }

        threadPool = Executors.newScheduledThreadPool(maxParallelRequests);

        Properties properties = ReaderUtils.readProperties(CONFIG);
        analysers = ReaderUtils.loadAnalyserFromProperties(properties);
    }

    public void crawl(File file){
        List<URL> urls = ReaderUtils.readURLFromFile(file);
        crawl(urls);
    }

    public void crawl(List<URL> urls) {
        for (URL url : urls){
            threadPool.schedule(()-> {

                Profile profile = analysers.get(url.getHost()).analyse(url);

                System.out.println(profile);

            }, requestDelayMs, TimeUnit.MILLISECONDS);
        }

        threadPool.shutdown();
    }
}
