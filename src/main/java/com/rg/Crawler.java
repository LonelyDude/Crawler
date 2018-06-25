package com.rg;

import com.rg.exception.IOConnectionException;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class Crawler {

    private static final String MAX_PARALLEL_REQUESTS_ENV = "crawler.maxParallelRequests";
    private static final String REQUEST_DELAY_MS_ENV = "crawler.requestDelayMs";
    private static final String CONFIG = "urls.txt";

    private int maxParallelRequests = 1;
    private int requestDelayMs = 1000;

    private final ScheduledExecutorService threadPool;

    private Set<URL> hosts;

    private List<URL> urls;

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

        hosts = new HashSet<>(readFile(new File(CONFIG)));
    }

    public void crawl(File file){
        urls = (List<URL>) readFile(file);
        crawl(urls);
    }

    public void crawl(List<URL> urls){
        for (URL url : urls){
            threadPool.scheduleWithFixedDelay(()-> {
                


            }, 0, requestDelayMs, TimeUnit.MILLISECONDS);
        }
    }

    private Collection readFile(File file){
        List<URL> urls = new ArrayList<>();
        try {
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String url;
            while ((url = bufferedReader.readLine()) != null){
                urls.add(new URL(url));
            }

        } catch (FileNotFoundException e) {
            throw new IOConnectionException(e);
        } catch (IOException e) {
            throw new IOConnectionException(e);
        }
        return urls;
    }

}
