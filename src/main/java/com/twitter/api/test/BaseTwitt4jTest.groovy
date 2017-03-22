package com.twitter.api.test

import com.twitter.api.util.PropertiesContext
import spock.lang.Shared
import spock.lang.Specification
import twitter4j.Status
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken


/**
 * Created by yojjitsu on 22.03.2017.
 */
class BaseTwitt4jTest extends Specification {
    @Shared twitter = TwitterFactory.getSingleton();
    @Shared context = PropertiesContext.getInstance();
    @Shared date = new Date(context.getProperty("test.date"));

     def setupSpec(){
        twitter = TwitterFactory.getSingleton();
        AccessToken accessToken = new AccessToken(context.getProperty("oauth.accessToken"), context.getProperty("oauth.accessTokenSecret"));
        twitter.setOAuthConsumer(context.getProperty("oauth.consumerKey"), context.getProperty("oauth.consumerSecret"));
        twitter.setOAuthAccessToken(accessToken);
    }

    def "Verify basic fields"() {
        given:
        List<Status> statuses = twitter.getHomeTimeline();
        expect: 'Dates fields are correct'
        statuses.each {
            it.createdAt.after(date);
            it.retweetCount == 0;
            it.text.contains("back")
        }
    }

    def "Verify status removed"() {
        setup:
        Status status = twitter.updateStatus(getStatusName())
        when:
        status = twitter.destroyStatus(status.getId())
        and:
        twitter.showStatus(status.getId())
        then:
        TwitterException e = thrown()
        expect:
        //default 404 not-found code which is expected when tweet deleted is 144.
        e.getErrorCode() == 144
    }

    def "Verify Status Update" () {
        given:
        List<Status> statuses = twitter.getHomeTimeline();
        when:
        def statusText = getStatusName()
        Status status = twitter.updateStatus(statusText);
        then:
        List<Status> newStatuses = twitter.getHomeTimeline();
        expect:
        newStatuses.size() > statuses.size()
        //first tweet updated.
        newStatuses.get(0).text != statuses.get(0).text
        newStatuses.get(0).text == statusText
        cleanup:
        twitter.destroyStatus(status.getId())
    }

    def "Duplication of tweet causes error" () {
        setup:
        def statusText = getStatusName()
        Status status = twitter.updateStatus(statusText)
        when:
        twitter.updateStatus(statusText)
        then:
        TwitterException e = thrown()
        expect:
        e.getErrorMessage() == "Status is a duplicate."
        e.getMessage().startsWith("403:")
        cleanup:
        twitter.destroyStatus(status.getId())
    }

    private String getStatusName() {
        "back to " + (new Random().nextInt(100) + 1)
    }

}