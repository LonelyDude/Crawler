package com.rg.utils;

import com.rg.analyser.SiteAnalyser;
import com.rg.exception.ReadingException;
import com.rg.exception.IOConnectionException;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ReaderUtils {

    public static Properties readProperties(String name){
        Properties properties = null;
        try {
            properties = new Properties();
            InputStream inputStream = ReaderUtils.class.getClassLoader().getResourceAsStream(name);
            properties.load(inputStream);
        }
        catch (IOException e) {
            throw new IOConnectionException(e);
        }
        return properties;
    }

    public static Map<String, SiteAnalyser> loadFromProperties(Properties hostProperties){
        Map<String, SiteAnalyser> analysers = new HashMap<>();
        try {
            for (Object key : hostProperties.keySet()){
                String host = (String) key;
                String className = hostProperties.getProperty(host);

                Class clazz = Class.forName(className);
                SiteAnalyser analyser = (SiteAnalyser) clazz.newInstance();
                analysers.put(host, analyser);
            }
        }
        catch (IllegalAccessException e) {
            throw new ReadingException(e);
        } catch (InstantiationException e) {
            throw new ReadingException(e);
        } catch (ClassNotFoundException e) {
            throw new ReadingException(e);
        }
        return analysers;
    }

    public static List<URL> readURLFromFile(File file){
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

    public static List<URL> readURLFromFile(String name){
        List<URL> urls = new ArrayList<>();
        try {
            FileReader reader = new FileReader(new File(name));
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
