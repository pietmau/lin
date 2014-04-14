package com.lisnx.util;


public class Constants {
	
	//public static final boolean DEBUG = true;
	public static final String DEBUG_TAG = "LISNX";
	
	public static String ip = "www.lisnx.com/mobile";    // Production
    //public static String ip = "qa.lisnx.com:8080";
    //public static String ip="74.117.153.85:8080";		//QA
    //public static String ip="74.117.153.159"; 
	//public static String ip="dev.lisnx.com:9990";             //Local
	//public static String ip = "http://192.168.15.101:9997";
	
	//REST Api urls
		public static final String LOGIN_TOKEN_URL = "http://"+ ip + "/api/getLoginToken";
		public static final String LOGIN_URL = "http://"+ ip + "/api/login";
		public static final String LISNX_DETAIL_URL = "http://"+ ip + "/api/getLisnDetails";
		public static final String PAST_LISNS_URL = "http://"+ ip + "/api/getPastLisns";
		public static final String USER_DETAILS_URL = "http://"+ ip + "/api/getUserDetails";
		public static final String USER_DETAILS_FOR_LISN_URL = "http://"+ ip + "/api/getUserDetailsForLisn";
		public static final String ABOUT_US_URL = "http://"+ ip + "/api/aboutUs";
		public static final String LISNS_AROUND_ME_URL = "http://"+ ip + "/api/getLisnsAroundMe";
		public static final String CREATE_LISN_URL = "http://"+ ip + "/api/createLisn";
		public static final String LOGOUT_URL = "http://"+ ip + "/api/logout";
		public static final String REGISTRATION_URL = "http://"+ ip + "/api/getUserRegisterDetails";
		public static final String JOIN_LISN_URL = "http://"+ ip + "/api/joinLisn";
		public static final String USER_PROFILE_URL = "http://"+ ip + "/api/getLoginUserDetails";
		public static final String UPDATE_PROFILE_URL = "http://"+ ip + "/api/updateUser";
		public static final String ADD_FACEBOOK_URL = "http://"+ ip + "/api/addFacebookAccount";
		public static final String LOGIN_WITH_FACEBOOK = "http://"+ ip + "/api/loginWithFacebook";
		public static final String ADD_LINKEDIN_URL = "http://"+ ip + "/api/addLinkedInAccount";
		public static final String SET_PROFILE_PICTURE_URL = "http://"+ ip + "/api/setProfilePicture";
		public static final String SET_PROFILE_PICTURE_URL2 = "http://"+ ip + "/api/setProfilePicture2";
		public static final String CHANGE_PASSWORD_URL = "http://"+ ip + "/api/changePassword";
		public static final String SEND_FRIEND_REQUEST_URL = "http://"+ ip + "/api/sendConnectionRequest";
		public static final String ACCEPT_FRIEND_REQUEST_URL = "http://"+ ip + "/api/acceptConnectionRequest";
		public static final String INVITE_FRIENDS_URL = "http://"+ ip + "/api/inviteFriends";
		public static final String IGNORE_FRIEND_REQUEST_URL = "http://"+ ip + "/api/ignoreConnectionRequest";
		public static final String GET_PEOPLE_NEARBY_URL = "http://"+ ip + "/api/getPeopleNearByNotification";
		public static final String GET_FRIENDS_URL = "http://"+ ip + "/api/getFriendsV2";
		public static final String GET_PEOPLE_NEARBY_PROFILE_URL = "http://"+ ip + "/api/getProfileOfPeopleNearBy";
		public static final String SEND_LOCATION_TO_SERVER_URL = "http://"+ ip + "/api/saveLatitudeAndLongitude";
		public static final String NOTIFICATION_COUNT_URL = "http://"+ ip + "/api/notification";
		public static final String GET_NOTIFICATION_URL = "http://"+ ip + "/api/getConnectionToBeAcceptNotification";
		public static final String GET_PROFILE_PICTURE_URL = "http://"+ ip + "/api/getProfilePicture";
		public static final String GET_OTHER_PROFILE_PICTURE_URL = "http://"+ ip + "/api/getOtherUserProfilePicture";
		public static final String SEND_NOTIFICATION_REQUEST= "http://"+ ip + "/api/sendNotificationRequest";
		public static final String SET_PROFILE_PICTURE_WITH_USERNAME_URL = "http://"+ ip + "/api/saveProfileImageFromUserName";
		public static final String GET_BUCKET_DETAILS_URL = "http://"+ ip + "/api/getBucketDetails";
		public static final String GET_MESSAGE_DETAILS_FOR_LISN_URL = "http://"+ ip + "/api/getLisnMessages";
		public static final String GET_MESSAGE_POST_DETAILS_FOR_LISN_URL = "http://"+ ip + "/api/postLisnMessage";
		public static final String GET_COMMON_LISNS_URL = "http://"+ ip +"/api/getCommonLisns";
		public static final String GET_COMMON_FRIENDS_URL = "http://"+ ip +"/api/getCommonFriends";
		public static final String GET_UNREAD_MESSAGE_COUNT_URL = "http://"+ ip +"/api/getMessageSummary";
		public static final String GET_PRIVATE_MESSAGE_URL = "http://"+ ip +"/api/getPrivateMessage";
		public static final String SEND_PRIVATE_MESSAGE_URL = "http://"+ ip +"/api/sendPrivateMessage";
		public static final String SEND_EXTERNAL_CONNECTION_REQUEST_URL = "http://"+ ip +"/api/sendExternalConnectionRequest";
		public static final String FORGOT_PASSWORD_URL = "http://"+ ip +"/api/forgotPassword";
		public static final String SET_LAST_VIEWED_MESSAGE_ID_URL = "http://"+ ip +"/api/setLastViewedMessageId";
		public static final String FRIEND_REQUEST_IS_ACCEPTED_URL = "http://"+ ip +"/api/notifyAcceptedFriendRequest";
		public static final String DISABLE_SOCIAL_NETWORKS_URL = "http://"+ ip +"/api/disableSocialNetworkSetting";
		
	//Post parameters name
	public static final String USERNAME_PARAM = "username";
	public static final String USERNAME_PARAM_2 = "userName";
	public static final String PASSWORD_PARAM = "password";
	public static final String LOGIN_TOKEN_PARAM = "token";
	public static final String LISN_ID_PARAM = "id";
	public static final String ACCESS_TOKEN_PARAM = "token";
	public static final String MESSAGE_RECEIVER_PARAM = "receiver";
	public static final String USER_ID_PARAM = "id";
	public static final String LATITUDE_PARAM = "latitude";
	public static final String LONGITUDE_PARAM = "longitude";
	public static final String CREATE_LISN_PARAM = "name";
	public static final String FULL_NAME_PARAM = "fullName";
	public static final String DATE_OF_BIRTH_PARAM = "dateOfBirth";
	public static final String CONFIRM_PASSWORD_PARAM = "password2";
	public static final String LISN_DESCRIPTION_PARAM = "description";
	public static final String END_DATE_TIME_PARAM = "endDate";
	public static final String POST_ON_FACEBOOK_PARAM = "postOnFacebook";
	public static final String VENUE_PARAM = "venue";
	public static final String TIME_STAMP = "timeStamp";
	public static final String FACEBOOK_ID = "fid";
	public static final String LINKEDIN_ID = "lid";
	public static final String LINKEDIN_PROFILE_URL = "linkedinProfileURL";
	public static final String ANDROID_PARAM = "android";
	public static final String PROFILE_PICTURE_PARAM = "profileImage";
	public static final String PROFILE_TYPE_PARAM = "profileShareType";
	public static final String LISN_ID_PARAM_2 = "lisnId";
	public static final String TARGET_USER_ID_PARAM = "targetUserId";
	public static final String OTHER_USER_ID_PARAM = "otherUserId";
	public static final String MESSAGE_CONTENT_PARAM = "content";
	public static final String SOCIAL_NETWORK_PARAM = "socialNetwork";
	public static final String SHARED_PREFERENCES_PARAM = "userToken";
	public static final String PREFS_FILE_NAME_PARAM = "UserPrefsFile"; 
	public static final String SEND_ALL_PARAM = "sendAll";
	public static final String LAST_MESSAGE_ID_PARAM = "lastMessageId";
	public static final String PASSWORD_RESET_EMAIL_PARAM = "email";
	public static final String FRIEND_ID_PARAM = "friendId";
	public static final String DISABLE_PARAM = "disable";
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	public static final String RSVP_IN = "In";
	public static final String PARSE_INSTALLATION_ID = "parseInstallationId";
	
	//Error Messages
		public static final String LOCATION_ERROR = "Please enable location: Settings -> Location & Security -> Enable Use wireless networks";
		public static final String DATABASE_ERROR = "No network connection found. Please try again."; 
		public static final String SERVER_DOWN_ERROR = "Something went wrong with the database. Please try again.";
		public static final String JSON_EXCEPTION_ERROR = "Something went wrong while parsing the server response. Please try again.";
		//public static final String NO_CONNECTION_ERROR = "Oops, an error occurred. Please try again later . Error code - 103.";
		public static final String WRONG_USERNAME_PASSWORD_ERROR = "Please enter a valid email and a valid password";
		public static final String USER_EMAIL_FIELD_EMPTY_ERROR = "Please enter a valid email";
		public static final String PASSWORD_FIELD_EMPTY_ERROR = "Please enter a valid password";
		public static final String APPLICATION_ERROR = "Oops, Some error occurred. Please try again.";
		public static final String BACK_BUTTON_PRESSED_ERROR = "Please use logout button"; 
		public static final String USER_NAME_FIELD_EMPTY_ERROR = "Please enter a name";
		public static final String USER_DATE_OF_BIRTH_FIELD_EMPTY_ERROR = "Please enter a valid date of birth";
		public static final String CONFIRM_PASSWORD_FIELD_EMPTY_ERROR = "Please enter the matching password"; 
		public static final String PASSWORD_AND_CONFIRM_PASSWORD_NOT_MATCHED_ERROR = "Please enter the matching password";
		public static final String SUCCESSFUL_REGISTER_MESSAGE = "Congratulations! Your registration is now complete. Welcome to LISNx!";
		public static final String LISN_DESCRIPTION_FIELD_EMPTY_ERROR = "Please enter a description for the LISN";
		public static final String END_DATE_TIME_FIELD_EMPTY_ERROR = "Please select end date & time";
		public static final String SUCCESSFUL_LISN_CREATION = "LISN created successfully! Users nearby can join and interact";
		public static final String LISN_JOINED_ALREADY_ERROR = "You already have joined this LISN";
		public static final String LISN_JOINED_SUCCESSFUL = "You have joined LISN successfully";
		public static final String SUCCESSFUL_UPDATE_MESSAGE = "Profile updated successfully";
		public static final String SUCCESSFUL_CHANGE_PASSWORD_MESSAGE = "Password changed successfully";
		public static final String PROBLEM_GETTING_IMAGE_ERROR = "Error occurred while loading image";
		public static final String SUCCESSFUL_REQUEST_SENT_MESSAGE = "Your request has been sent!";
		public static final String SUCCESSFUL_REQUEST_ACCEPT_MESSAGE = "You are now connected!";
		public static final String SUCCESSFUL_REQUEST_IGNORED_MESSAGE = "Request ignored";
		public static final String NO_PROFILE_TYPE_CHOSEN_ERROR = "Please choose profile options to share";
		public static final String NO_FRIENDS_ERROR = "You have no connections";
		public static final String NO_PEOPLE_NEARBY_ERROR = "No users nearby. Please check back later.";
		public static final String NO_NOTIFICATION_ERROR = "You have no new notifications";
		public static final String NO_LISNS_ERROR = "No users nearby. Meeting people? Create a LISN and connect instantly.";
		public static final String FACEBOOK_ALREADY_CONNECTED_ERROR = "Your Facebook account is connected";
		public static final String LINKEDIN_ALREADY_CONNECTED_ERROR = "Your LinkedIn account is connected";
		public static final String NO_RESPONSE_FROM_SERVICE_PROVIDER_ERROR = "Unable to perform the operation at this time. Please try again later.";
		public static final String MESSAGE_FIELD_EMPTY_ERROR = "Please enter a message";
		public static final String CONNECTED_TO_FACEBOOK_ERROR = "Your Facebook is connected.";
		public static final String CONNECTED_TO_LINKEDIN_ERROR = "Your LinkedIn is connected.";
		public static final String NO_INTERNET_CONNECTION_ERROR = "Please check your internet connection.";
		public static final String TEMPORARY_PASSWORD_SENT_ERROR = "A temporary password has been emailed to you. Please login with the temporary password.";
		public static final String DISCONNECTED_TO_FACEBOOK_ERROR = "Your Facebook login has been disabled.";
		public static final String DISCONNECTED_TO_LINKEDIN_ERROR = "Your LinkedIn login has been disabled.";
		public static final String LOGIN_FAILED_TO_FACEBOOK_ERROR = "Login failed.";
		public static final String APP_CRASH_MSG = "An unknown error occured. Sorry for the inconvenience.";
	
	//Tab view constants
	public static final String TAB_TITLE_CREATE = "Create";
	public static final String TAB_TITLE_NOW = "Now";
	public static final String TAB_TITLE_PAST = "Past";
	
	//Grid constants
	public static final int GRID_ICON_COUNT = 4;
	
	//Version Constant
	//TODO: fetch version from manifest file.
	public static final String VERSION_COUNT="Version : 1.0";
	
	//Grid Item position
	public static final int LISN = 0;
	public static final int PROFILE = 1;
	//public static final int SETTINGS = 2;
	public static final int CONNECTIONS = 2;
	public static final int ABOUT_US = 3;
	
	//List Item position
		public static final int LISN_NAME = 0;
		public static final int LISN_START_DATE = 1;
		public static final int LISN_END_DATE = 2;
		public static final int LISN_DESCRIPTION= 3;
		public static final int LISN_LISNERS = 4;
		
	//Image Resolution Constants
		public static final int LOW_WIDTH = 32*2;
		public static final int LOW_HEIGHT = 32*2;
		public static final int MEDIUM_WIDTH = 50*2;
		public static final int MEDIUM_HEIGHT = 50*2;
		public static final int HIGH_WIDTH = 64*2;
		public static final int HIGH_HEIGHT = 64*2;
		public static final int LOW_WIDTH_SMALL = 30*2;
		public static final int LOW_HEIGHT_SMALL = 30*2;
		public static final int MEDIUM_WIDTH_SMALL = 40*2;
		public static final int MEDIUM_HEIGHT_SMALL = 40*2;
		public static final int HIGH_WIDTH_SMALL = 50*2;
		public static final int HIGH_HEIGHT_SMALL = 50*2;
		public static final int LOW_WIDTH_LANDSCAPE = 40*2;
		public static final int LOW_HEIGHT_LANDSCAPE = 40*2;
		public static final int MEDIUM_WIDTH_LANDSCAPE = 50*2;
		public static final int MEDIUM_HEIGHT_LANDSCAPE = 50*2;
		public static final int HIGH_WIDTH_LANDSCAPE = 60*2;
		public static final int HIGH_HEIGHT_LANDSCAPE = 60*2;
		public static final int NOTIFICATION_TEXT_SIZE = 12*2;
		
	//Calling Screen Constants
		public static final String CALLING_SCREEN_CREATE = "Create";
		public static final String CALLING_SCREEN_JOIN = "Join";
		public static final String CALLING_SCREEN_CONNECT = "Connect";
		public static final String CALLING_SCREEN_LISN_DETAIL = "LisnDetail";
		public static final String CALLING_SCREEN_PEOPLE_NEARBY = "PeopleNearBy";
		public static final String CALLING_SCREEN_FRIENDS = "Friends";
		public static final String CALLING_SCREEN_CHOOSE_PROFILE = "ChooseProfile";
		public static final String CALLING_SCREEN_NOTIFICATIONS = "Notifications";
		public static final String CALLING_SCREEN_CHANGE_PASSWORD = "ChangePassword";
		public static final String CALLING_SCREEN_CREATE_LISN = "CreateLisn";
		public static final String CALLING_SCREEN_MENU = "Menu";
		public static final String CALLING_SCREEN_NOW_LISN = "NowLisn";
		public static final String CALLING_SCREEN_OTHER_PROFILE = "OtherProfile";
		public static final String CALLING_SCREEN_PAST_LISN = "PastLisn";
		public static final String CALLING_SCREEN_PROFILE = "Profile";
		public static final String CALLING_SCREEN_SETTINGS = "Settings";
		public static final String CALLING_SCREEN_LOGIN = "Login";
		public static final String CALLING_SCREEN_MESSAGE_NOTIFICATION = "MessageNotification";
		public static final String CALLING_SCREEN_COMMON_LISN = "CommonLisn";
		public static final String CALLING_SCREEN_COMMON_FRIEND = "CommonFriend";
		public static final String CALLING_SCREEN_ABOUT_US = "AboutUs";
		public static final String CALLING_SCREEN_MESSAGE_BOARD = "MessageBoard";
		
	//Connection Activities
		public static final String ACCEPT_FRIEND_REQUEST = "AcceptRequest";
		public static final String SEND_FRIEND_REQUEST = "SendRequest";
		
	//Profile Share Types
		public static final String PROFILE_SHARE_ALL = "ALL";
		public static final String PROFILE_SHARE_CASUAL = "CASUAL";
		public static final String PROFILE_SHARE_EMAIL = "EMAIL";
		public static final String PROFILE_SHARE_PROFESSIONAL = "PROFESSIONAL";
		
	//Connection Status
		public static final String CONNECTION_STATUS_CONNECTED = "Connected";
		public static final String CONNECTION_STATUS_NOT_CONNECTED = "Not_Connected";
		public static final String CONNECTION_STATUS_PENDING = "Pending";
		public static final String CONNECTION_STATUS_ACCEPT = "To_Be_Accept";
		public static final String CONNECTION_STATUS_IGNORED = "Ignored";
		
	//Strings to differentiate message types.
		public static final String MESSAGE_TYPE_LISNS = "lisnMessage";
		public static final String MESSAGE_TYPE_CHAT = "chatMessage";
		public static final String MESSAGE_TYPE_FRIEND_REQUEST = "friendRequest";
		public static final String MESSAGE_TYPE_ACCEPTED_REQUEST = "acceptedRequest";
	
	//Toast visible duration
		public static final int TOAST_VISIBLE_SHORT = 1;
		public static final int TOAST_VISIBLE_LONG = 2;
		public static final int TOAST_VISIBLE_EXTRA_LONG = 3;
		public static final int REFRESH_ITERATION_INTERVAL = 10000;
		public static final int UPDATE_LOCATION_ITERATION_INTERVAL = 5000; //VISH (30000)
	
		public static final int MAX_ADDRESS_TO_RETURN = 1;
		public static final String ERROR_NO_ADDRESS_FOUND = "Trying to get your current location.";
	
		public static final String LISNERS = "LISNers";
		public static final String START_DATE = "Start date:";
		public static final String END_DATE = "End date:";
		public static final String LISNS = "LISNs";
		public static final String FB = "FB";
		public static final String LINKEDIN = "LINKEDIN";
	
		public static final String APP_NAME = "lisnx";
		public static final String FACEBOOK_KEY = "164116363623152"; 
		public static final String LINKEDIN_KEY = "ndd55k2u1zem";
		public static final String LINKEDIN_SECRET = "AVnDRwmNzHC6k0jG";
		public static final String LOGIN_FAILED = "Login failed!";
		public static final String STATUS_POSTED_SUCCESSFULLY = "Status Posted Successfully!!";
		public static final String FEW_SECONDS_AGO = "few seconds ago...";
		
		//Constants for Cache Capacity.
		public static final int HARD_CACHE_CAPACITY = 10;
	    public static final int DELAY_BEFORE_PURGE = 10 * 1000;
	    
	    //Constants for Parse push notification
	    public static final String APP_ID = "8rOOQ5Q2a54cTdhwcohTgenTeDsVojZToRHktguz";
	    public static final String CLIENT_KEY = "mmOWCtG2GSiVcnAadrbi2JTVvW4YM0Woeysn8adc";

		public static final String EMAIL = "email";

		public static final String CHANNEL = "channel";
		public static final String INVITEE_LIST = "inviteeList";
		public static final String INVITATION_MESSAGE = "invitationMessage";


		public enum FACEBOOK_PARAMS {
			ACCESS_TOKEN, TOKEN_EXPIRATION_DATE, PERMISSIONS, USER_LOCATION
		}
		
		
		//VISH
		public static final int CONNECTION_TIMEOUT = 60; // secs
		public static final int SOCKET_TIMEOUT = 60; // secs
		
}