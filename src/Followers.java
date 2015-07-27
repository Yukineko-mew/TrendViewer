import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import twitter4j.RateLimitStatus;
import twitter4j.User;
import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.TwitterException;

public class Followers {
	private static final int ACQUIRABLE_MAX_ID_PER_REQUEST = 5000;
	List<Long> friendsIDs;
	File rootDir;
	
	public Followers() {
			// もし認証ユーザーのディレクトリがなかったら、作成する
		friendsIDs = new ArrayList<Long>();
		
		try {
			rootDir = new File(String.valueOf(TwitterUtility.getTwitter().getId()));
		} catch (IllegalStateException | TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!rootDir.exists()) { rootDir.mkdir(); }
	}
	
	public void getTwoSeparationFriends() {
		try {
			// 認証ユーザーのフォローリストを取得
			getFriends(TwitterUtility.getTwitter().getId(), true);
		
			// フォローリストのユーザーそれぞれのフォローリストを取得
			for(Long userID : friendsIDs) {
				getFriends(userID, false);
			}
		} catch (IllegalStateException | TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getFriends(final long targetUserID, final boolean isRootUser) {
		List<Long> friendsIDs = new ArrayList<Long>();
		long cursor = -1L;
		IDs ids = null;
		long page = 1L;
		
			do {
	            try {
					ids = TwitterUtility.getTwitter().getFriendsIDs(targetUserID, cursor, 5000);
		            // フォロワーが多いユーザだと無反応で寂しい＆不安なので状況表示
		            System.out.println(String.format("%dページ目取得(%d <= %d)",
		            		page,
		            		ACQUIRABLE_MAX_ID_PER_REQUEST * (page - 1),
		                    ACQUIRABLE_MAX_ID_PER_REQUEST * page++));
				} catch (TwitterException e) {
	                // Rate Limit に引っかかった場合の処理
	                // (メモ) status code も併せてチェックすべきか？！

	                RateLimitStatus rateLimit = e.getRateLimitStatus();
	                int secondsUntilReset = rateLimit.getSecondsUntilReset();
	                System.err.println("please wait for " + secondsUntilReset + " seconds");
	                System.err.println("Reset Time : " +  rateLimit.getResetTimeInSeconds());
	                
	                //(注) 120秒間，水増し・・getSecondsUntilReset() の返す値が負の
	                //場合があり，それに対応するため
	                // long waitTime = (long)(secondsUntilReset + 120) * 1000;
	                long waitTime = (long)(300 * 1000); // 300秒待ち
	                try {
	                    Thread.sleep(waitTime);
	                } catch(Exception e1){
	                    e1.printStackTrace();
	                }

	                continue;
				}

	            // 取得したIDをストックする
	            for (long id : ids.getIDs()) {
	                friendsIDs.add(id);
	            }

	            // 次のページへのカーソル取得
	            cursor = ids.getNextCursor();
	        } while (ids.hasNext());
			
			showIDs(friendsIDs, targetUserID);
		
		if(isRootUser) { this.friendsIDs = new ArrayList<Long>(friendsIDs); }
	}
	
	public void showIDs(final List<Long> idList, final long targetUserID) {
		System.out.println("- - - -");
		System.out.println("全 " + idList.size() + " 件のフォロー情報を取得開始します");
		
		File file = new File(rootDir.getName() + "/" + String.valueOf(targetUserID));
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			for(int c=0; c<idList.size(); c+=100) {
				int end = 100;
					
				System.out.println(c+" 件取得済み、残りは " + (idList.size()-c) + " 件です。");

				if(idList.size()-c < 100) { end = idList.size()-c; }

				long [] idlistArray = toArr(idList.subList(c, c+end));
				System.out.println();
				ResponseList<User> responseUsers = TwitterUtility.getTwitter().lookupUsers(idlistArray);
				System.out.println("idListArray = [" + idlistArray.length + "], responseUsers = [" + responseUsers.size() + "]");
				for(int i=0; i<responseUsers.size(); i++) {
					String s = "[" + (c+i) + "]" + responseUsers.get(i).getName() + " : " + String.valueOf(responseUsers.get(i).getId());
//					System.out.println(s);
					pw.println(s);
				}
			}
			pw.close();
		} catch (IOException | TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static long[] toArr(List<Long> list){
        // List<Integer> -> int[]
        int l = list.size();
        long[] arr = new long[l];
        Iterator<Long> iter = list.iterator();
        for (int i=0;i<l;i++) arr[i] = iter.next();
        return arr;
    } 
}