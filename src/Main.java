public class Main {
	public static void main(String [] args) {
		TwitterUtility.getInstance();
		Followers followers = new Followers();
		
		followers.getTwoSeparationFriends();
	}
}
