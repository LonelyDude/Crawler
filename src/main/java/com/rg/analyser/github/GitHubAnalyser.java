package com.rg.analyser.github;

import com.rg.exception.IOConnectionException;
import com.rg.exception.JsonParserException;
import com.rg.profile.github.GitHubProfile;
import com.rg.analyser.SiteAnalyser;
import com.rg.utils.ReaderUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GitHubAnalyser implements SiteAnalyser{

    private static final String USER_PATH = "/users";
    private static final String REPO_PATH = "/repos";
    private static final String IF_NONE_MATCH_HEADER = "If-None-Match";

    private static int STATUS_CODE = 304;

    private CloseableHttpClient httpClient;
    private JSONParser parser;

    private static final String USER_PAGE_ETAG = "userPageEtag";
    private static final String REPOS_PAGE_ETAG = "reposPageEtag";

    private Map<String, Map<String, String>> usersEtags;

    private static final String LOGIN = "login";
    private static final String NAME = "name";
    private static final String COMPANY = "company";
    private static final String LOCATION = "location";

    public GitHubAnalyser(){
        httpClient = HttpClients.createDefault();
        parser = new JSONParser();

        usersEtags = new HashMap<>();
    }

    @Override
    public GitHubProfile analyse(URL url) { //format: https://github.com/username
        GitHubProfile profile = new GitHubProfile();

        String userName = url.getPath().substring(1);
        Map<String, String> eTags = usersEtags.get(userName);
        if(eTags == null){
            eTags = new HashMap<>();
            usersEtags.put(userName, eTags);
        }

        analyseUserPage(profile, url, eTags);


//        analyseReposPage(profile, url, eTags);
        return profile;
    }

    private void analyseUserPage(GitHubProfile profile, URL url, Map<String, String> eTags){
        HttpGet userRequest = new HttpGet("https://api." + url.getHost() + USER_PATH + url.getPath());

        String userPageEtag = eTags.get(USER_PAGE_ETAG);
        if(userPageEtag != null){
            userRequest.setHeader(IF_NONE_MATCH_HEADER, userPageEtag);
        }

        try {
            HttpResponse response = httpClient.execute(userRequest);

            String charset = "UTF8";
            if (userPageEtag != null && (response.getStatusLine().getStatusCode() != STATUS_CODE)){
                userRequest.removeHeaders(IF_NONE_MATCH_HEADER);
                response = httpClient.execute(userRequest);

                String content = ReaderUtils.loadContent(response.getEntity(), charset);
                JSONObject user = (JSONObject) parser.parse(content);
                profile.setLogin((String) user.get(LOGIN));
                profile.setName((String) user.get(NAME));
                profile.setCompany((String) user.get(COMPANY));
                profile.setLocation((String) user.get(LOCATION));
            } else{
                //GET FROM CASH
            }

        } catch (IOException e) {
            throw new IOConnectionException(e);
        } catch (ParseException e) {
            throw new JsonParserException(e);
        }
    }

    private void analyseReposPage(GitHubProfile profile, URL url, Map<String, String> eTags){
        HttpGet userRequest = new HttpGet(url.getHost() + USER_PATH + url.getPath() + REPO_PATH);

        String repoUserPageEtag = eTags.get(REPOS_PAGE_ETAG);
        if(repoUserPageEtag != null){
            userRequest.setHeader(IF_NONE_MATCH_HEADER, repoUserPageEtag);
        }

        try {
            HttpResponse response = httpClient.execute(userRequest);
            if (repoUserPageEtag != null && (response.getStatusLine().getStatusCode() != STATUS_CODE)){
                for (Header header : response.getAllHeaders()){
                    if(header.getName().equals("Etag")){
                        eTags.put(REPOS_PAGE_ETAG, header.getValue());
                        break;
                    }
                }
            }

            String content = ReaderUtils.loadContent(response.getEntity(), null);
            JSONArray repos = (JSONArray) parser.parse(content);

        } catch (IOException e) {
            throw new IOConnectionException(e);
        } catch (ParseException e) {
            throw new JsonParserException(e);
        }
    }

}
