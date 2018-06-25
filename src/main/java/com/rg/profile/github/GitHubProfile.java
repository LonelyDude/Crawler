package com.rg.profile.github;

import com.rg.exception.IOConnectionException;
import com.rg.profile.Profile;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

import java.io.IOException;
import java.util.*;

public class GitHubProfile implements Profile{

    private String login;

    private String name;

    private String company;

    private String location;

    private String language;

    private Map<String, GHRepository> repositories;

    private String popularRepository;

    private int starsInPopularRepository;

    public GitHubProfile(GHUser user){
        try {
            login = user.getLogin();
            name = user.getName();
            company = user.getCompany();
            location = user.getLocation();
            repositories = user.getRepositories();

            Map<String, Integer> languagesMap = new HashMap<>(); // language-number
            int max = 0;
            for (GHRepository repository : repositories.values()){
                for(String lang : repository.listLanguages().keySet()){
                    if(languagesMap.get(lang) == null){
                        languagesMap.put(lang, 0);
                    } else {
                        languagesMap.put(lang, languagesMap.get(lang) + 1);
                    }
                }

                if(repository.getSubscribersCount() >= max){  //get popular repository and number of stars
                    max = repository.getSubscribersCount();
                    popularRepository = repository.getName();
                    starsInPopularRepository = repository.getStargazersCount();
                }
            }

            max = 0;
            for (String lang : languagesMap.keySet()){ // get the most popular language
                int numb = languagesMap.get(lang);
                if(numb >= max){
                    max = numb;
                    language = lang;
                }
            }

        } catch (IOException e) {
            throw new IOConnectionException(e);
        }

    }


    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public String getPopularRepository() {
        return popularRepository;
    }

    public int getStarsInPopularRepository() {
        return starsInPopularRepository;
    }

    @Override
    public String toString() {
        return "login: " + login + "\nname: " + name + "\ncompany: " + company + "\nlocation: " + location + "\nlanguage: " + language + "\nstars: " + starsInPopularRepository;
    }
}
