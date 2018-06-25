package com.rg;

import com.rg.analyser.SiteAnalyser;
import com.rg.exception.IOConnectionException;
import com.rg.profile.Profile;
import jdk.internal.org.objectweb.asm.tree.analysis.Analyzer;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class Crawler {

    private static final String MAX_PARALLEL_REQUESTS_ENV = "crawler.maxParallelRequests";
    private static final String REQUEST_DELAY_MS_ENV = "crawler.requestDelayMs";
    private static final String CONFIG = "hosts.properties";

    private int maxParallelRequests = 1;
    private int requestDelayMs = 1000;

    private final ScheduledExecutorService threadPool;

    private Properties hosts;

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

        try {
            hosts = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG);
            hosts.load(inputStream);
        } catch (IOException e) {
            throw new IOConnectionException(e);
        }
    }

    public void crawl(File file){
        urls = (List<URL>) readFile(file);
        crawl(urls);
    }

    public void crawl(List<URL> urls){
        for (URL url : urls){
            threadPool.scheduleWithFixedDelay(()-> {
                String clazzName = hosts.getProperty(url.getHost());
                try {
                    Class clazz = Class.forName(clazzName);
                    SiteAnalyser analyser = (SiteAnalyser) clazz.newInstance();
                    Profile profile = analyser.analyse(url);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
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
