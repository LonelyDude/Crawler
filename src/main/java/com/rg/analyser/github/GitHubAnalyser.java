package com.rg.analyser.github;

import com.rg.exception.IOConnectionException;
import com.rg.profile.github.GitHubProfile;
import com.rg.analyser.SiteAnalyser;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.net.URL;

public class GitHubAnalyser implements SiteAnalyser{

    private GitHub gitHub;

    public GitHubAnalyser(String login, String password){
        try {
            gitHub = GitHub.connectUsingPassword(login, password);
        } catch (IOException e) {
            throw new IOConnectionException(e);
        }
    }

    @Override
    public GitHubProfile analyse(URL url) {       //format: https://github.com/login
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
