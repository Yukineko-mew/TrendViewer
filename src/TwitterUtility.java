import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterUtility {
	private static TwitterUtility instance = null;
	
	private static Twitter twitter;
	private List<Status> timeline;
    
	private static final String consumerKey = "OUMcqsAjKcVAimLjzS4lcGtCL";
	private static final String consumerSecret = "iRmzoAPIgGasAKfJE4ASzOqc9ZuhP67rUWL5esMttCx1H65K0o";
	private static final String AccessTokenKey = "822719520-dF8Ws7kO6dq6yAG9FOea2uvUX2iVEuDJ5iuxcgnD";
	private static final String accessTokenSecret = "XiCkRCDP6KQWW4rN0i7Jg1TkD2lMBxEr3wfy8isccSMf0";
	
    /* コンストラクタ
     * 
     * 
     */
	private TwitterUtility() {
		twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		AccessToken accessToken = new AccessToken(AccessTokenKey, accessTokenSecret);
		twitter.setOAuthAccessToken(accessToken);
	}
	
    public List<Status> getMyTimeline() {
      	 return timeline;
    }
    
    
    public static TwitterUtility getInstance() {
    	if(instance == null) {
    		instance = new TwitterUtility();
    	}
    	return instance;
    }
    
    public static Twitter getTwitter() {
    	return twitter;
    }
}
