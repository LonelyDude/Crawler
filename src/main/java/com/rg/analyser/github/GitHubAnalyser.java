package com.rg.analyser.github;

import com.rg.exception.IOConnectionException;
import com.rg.profile.github.GitHubProfile;
import com.rg.analyser.SiteAnalyser;
import com.rg.utils.ReaderUtils;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class GitHubAnalyser implements SiteAnalyser{

    private static final String CONFIG = "github.properties";

    private GitHub gitHub;

    public GitHubAnalyser(){
        try {
            Properties properties = ReaderUtils.readProperties(CONFIG);
            gitHub = GitHub.connectUsingPassword(properties.getProperty("login"), properties.getProperty("password"));
        } catch (IOException e) {
            throw new IOConnectionException(e);
        }

    }

    public GitHubAnalyser(String login, String password){
        try {
            gitHub = GitHub.connectUsingPassword(login, password);
        } catch (IOException e) {
            throw new IOConnectionException(e);
        }
    }

    @Override
    public GitHubProfile analyse(URL url) { //format: https://github.com/login
        String path = url.getPath();
        String login = path.substring(1);
        GitHubProfile profile;

        try {
            profile = new GitHubProfile(gitHub.getUser(login));
        } catch (IOException e) {
            throw new IOConnectionException(e);
        }

        return profile;
    }
}
