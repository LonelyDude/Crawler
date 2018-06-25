package com.rg;

import com.rg.analyser.SiteAnalyser;
import com.rg.exception.CrawlerInitializationException;
import com.rg.exception.IOConnectionException;
import com.rg.profile.Profile;

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

        analysers = new HashMap<>();

        try {
            Properties hostProperties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG);
            hostProperties.load(inputStream);

            for (Object key : hostProperties.keySet()){
                String host = (String) key;
                String className = hostProperties.getProperty(host);

                Class clazz = Class.forName(className);
                SiteAnalyser analyser = (SiteAnalyser) clazz.newInstance();
                analysers.put(host, analyser);
            }
        }
        catch (IOException e) {
            throw new IOConnectionException(e);
        } catch (IllegalAccessException e) {
            throw new CrawlerInitializationException(e);
        } catch (InstantiationException e) {
            throw new CrawlerInitializationException(e);
        } catch (ClassNotFoundException e) {
            throw new CrawlerInitializationException(e);
        }
    }

    public void crawl(File file){
        List<URL> urls = readFile(file);
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

    public static List<URL> readFile(File file){
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
