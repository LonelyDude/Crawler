package com.rg;

import com.rg.analyser.SiteAnalyser;
import com.rg.exception.CrawlerInitializationException;
import com.rg.exception.IOConnectionException;
import com.rg.profile.Profile;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

        String str = hostsProperty;
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
        Pattern argsPattern = Pattern.compile(".*\\.arg\\d");

        for(String host : hosts){
            String className = null;
            Map<Integer, String> args = new TreeMap<>();

            for (Object key : properties.keySet()){
                String keyString = (String) key;
                if (keyString.contains(host)){
                    if(classPattern.matcher(keyString).matches()){
                        className = properties.getProperty(keyString);
                    }
                    if(argsPattern.matcher(keyString).matches()){
                        int index = Integer.valueOf(keyString.substring(keyString.length()-1, keyString.length()));
                        args.put(index, properties.getProperty(keyString));
                    }
                }
            }

            if (className == null){
                continue;
            }

            try {
                Class clazz = Class.forName(className);
                for(Constructor constructor : clazz.getDeclaredConstructors()){
                    if(constructor.getParameterCount() == args.size()){
                        SiteAnalyser analyser = (SiteAnalyser) constructor.newInstance(args.values().toArray());
                        analysers.put(host, analyser);
                        break;
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new CrawlerInitializationException(e);
            } catch (IllegalAccessException e) {
                throw new CrawlerInitializationException(e);
            } catch (InstantiationException e) {
                throw new CrawlerInitializationException(e);
            } catch (InvocationTargetException e) {
                throw new CrawlerInitializationException(e);
            }
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
        threadPool.shutdown();;
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
