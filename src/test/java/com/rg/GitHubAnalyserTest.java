package com.rg;

import com.rg.analyser.github.GitHubAnalyser;
import com.rg.exception.IOConnectionException;
import com.rg.profile.github.GitHubProfile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GitHubAnalyserTest {

    private static final String TXT = "repos.txt";

    private GitHubAnalyser analyser;
    private List<URL> urls;

    @Before
    public void init() throws URISyntaxException {
        analyser = new GitHubAnalyser();
        urls = new ArrayList<>();

        URL url = getClass().getClassLoader().getResource(TXT);
        urls = Crawler.readFile(new File(url.toURI()));
    }

    @Test
    public void analyseTest(){
        for (URL url : urls){
            GitHubProfile profile = (GitHubProfile) analyser.analyse(url);
            if(profile == null){
                Assert.fail();
            }
            System.out.println(profile);
        }
    }

}
