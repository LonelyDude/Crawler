package com.rg;

import com.rg.analyser.SiteAnalyser;
import com.rg.exception.IOConnectionException;
import com.rg.profile.Profile;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class Crawler {

    private static final String MAX_PARALLEL_REQUESTS_ENV = "crawler.maxParallelRequests";
    private static final String REQUEST_DELAY_MS_ENV = "crawler.requestDelayMs";
    private static final String CONFIG = "hosts.properties";
    private static final String HOSTS_PROPERTY = "hosts";

    private int maxParallelRequests = 1;
    private int requestDelayMs = 1000;

    private final ScheduledExecutorService threadPool;

    private Properties properties;

    private Map<URL, SiteAnalyser> analysers;

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

        analysers = new HashMap<>();

        try {
            properties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG);
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IOConnectionException(e);
        }

        loadAnalysers(properties);
    }

    private void loadAnalysers(Properties properties){
        String hostsProperty = properties.getProperty(HOSTS_PROPERTY);

        List<String> hosts = new ArrayList<>();

        String str = "";
        while (str != null){
            int index = str.indexOf(";");
            if(index == -1){
                hosts.add(str);
                break;
            }

            hosts.add(str.substring(0, index));
            str = str.substring(index + 1, str.length());
        }

        Pattern classPattern = Pattern.compile(".*\\.class");
        Pattern argsPattern = Pattern.compile(".*\\.arg \\d");

        for(String host : hosts){
            List<String> args = new ArrayList<>();

            for (Object key : properties.keySet()){
                String keyString = (String) key;
                if (keyString.contains(host)){

                }
            }
        }

    }

    public void crawl(File file){
        urls = readFile(file);
        crawl(urls);
    }

    public void crawl(List<URL> urls){
        for (URL url : urls){
            threadPool.scheduleWithFixedDelay(()-> {

                Profile profile = analysers.get(url.getHost()).analyse(url);

                System.out.println(profile);

            }, 0, requestDelayMs, TimeUnit.MILLISECONDS);
        }
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
