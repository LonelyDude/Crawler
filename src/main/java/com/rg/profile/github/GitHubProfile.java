package com.rg.profile.github;

import com.rg.profile.Profile;

import java.util.Map;

public class GitHubProfile implements Profile{

    private String login;

    private String name;

    private String company;

    private String location;

    private String language;

    private String popularRepository;

    private int starsInPopularRepository;

    public GitHubProfile(){

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

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setPopularRepository(String popularRepository) {
        this.popularRepository = popularRepository;
    }

    public void setStarsInPopularRepository(int starsInPopularRepository) {
        this.starsInPopularRepository = starsInPopularRepository;
    }

    @Override
    public String toString() {
        return "login: " + login + "\nname: " + name + "\ncompany: " + company + "\nlocation: " + location + "\nlanguage: " + language + "\npopular repo: " + popularRepository + "\nstars: " + starsInPopularRepository + "\n";
    }
}
