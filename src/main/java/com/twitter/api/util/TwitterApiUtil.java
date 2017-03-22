package com.twitter.api.util;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.List;

/**
 * Created by mkhimich on 22.03.2017.
 */
public class TwitterApiUtil {
    private static PropertiesContext context = PropertiesContext.getInstance();
    private static Twitter twitter;
    private static TwitterApiUtil instance;

    public static TwitterApiUtil getInstance() {
        return instance;
    }

    public static Twitter getTwitter() {
        return twitter;
    }

    private TwitterApiUtil() {
        init();
    }

    private void init() {
        twitter = TwitterFactory.getSingleton();
        AccessToken accessToken = new AccessToken(context.getProperty("oauth.accessToken"), context.getProperty("oauth.accessTokenSecret"));
        twitter.setOAuthConsumer(context.getProperty("oauth.consumerKey"), context.getProperty("oauth.consumerSecret"));
        twitter.setOAuthAccessToken(accessToken);
    }

    public static List<Status> getTimelineList() throws TwitterException {
        return twitter.getHomeTimeline();
    }

}
