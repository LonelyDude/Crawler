package com.rg.utils;

import com.rg.analyser.SiteAnalyser;
import com.rg.exception.LoadingException;
import com.rg.exception.IOConnectionException;
import org.apache.http.HttpEntity;
import sun.misc.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ReaderUtils {

    public static Properties readProperties(String name){
        Properties properties = new Properties();
        try(InputStream inputStream = ReaderUtils.class.getClassLoader().getResourceAsStream(name)) {
            properties.load(inputStream);
        }
        catch (IOException e) {
            throw new IOConnectionException(e);
        }
        return properties;
    }

    public static String loadContent(HttpEntity entity, String encoding){
        StringBuilder builder = new StringBuilder();

        try(Reader reader = new InputStreamReader(entity.getContent(), encoding)){
            BufferedReader bufferedReader = new BufferedReader(reader);

            String tmp;

            while ((tmp = bufferedReader.readLine()) != null){
                builder.append(tmp);
            }

        } catch (IOException e) {
            throw new IOConnectionException(e);
        }
        return builder.toString();
    }

    public static Map<String, SiteAnalyser> loadAnalyserFromProperties(Properties hostProperties){
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
            throw new LoadingException(e);
        } catch (InstantiationException e) {
            throw new LoadingException(e);
        } catch (ClassNotFoundException e) {
            throw new LoadingException(e);
        }
        return analysers;
    }

    public static List<URL> readURLFromFile(File file){
        List<URL> urls = new ArrayList<>();
        try(FileReader reader = new FileReader(file)) {

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
        try(FileReader reader = new FileReader(new File(name))) {

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
