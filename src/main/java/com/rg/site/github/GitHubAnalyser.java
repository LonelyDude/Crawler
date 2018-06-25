package com.rg.site.github;

import com.rg.exception.IOConnectionException;
import com.rg.profile.Profile;
import com.rg.site.SiteAnalyser;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.net.URL;

public class GitHubAnalyser implements SiteAnalyser{

    private GitHub gitHub;

    public GitHubAnalyser(){
        try {
            gitHub = GitHub.connect();
        } catch (IOException e) {
            throw new IOConnectionException(e);
        }
    }

    @Override
    public Profile analyse(URL url) {



        return null;
    }
}
