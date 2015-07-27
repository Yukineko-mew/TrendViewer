import java.util.HashSet;

import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class Follows {

	public Follows() throws TwitterException {
		long cursor = -1;
		int count = 0;
		HashSet<String> set = new HashSet<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("screenName,name,followersCount,friendsCount,description\n");
		while (cursor != 0) {

			// 15回リクエスト送ったら怒られるから、14で区切っとく。本来はThread#sleepするのが望ましい
			if (count >= 14) {
				break;
			}
			
			PagableResponseList<User> friendlist = TwitterUtility.getTwitter().getFriendsList(TwitterUtility.getTwitter().getScreenName(), cursor);
			int sizeoffrendlist = friendlist.size();

			for (int i = 0; i < sizeoffrendlist; i++) {
				User user = friendlist.get(i);
//				sb.append(user.getName()).append(" (@")
//				  .append(user.getScreenName()).append(")")
//				  .append("\n");
				set.add(user.getScreenName());
/*
				sb.append(user.getScreenName()).append(",")
						.append(user.getName()).append(",")
						.append(user.getFollowersCount()).append(",")
						.append(user.getFriendsCount()).append(",")
						.append(user.getDescription()).append("\n");
*/
//				System.out.println(sb.toString());
			}
			
			cursor = friendlist.getNextCursor();
			System.out.println("====> New cursor value" + cursor);
			count++;
		}
		System.out.println(set.size());
	}
}
