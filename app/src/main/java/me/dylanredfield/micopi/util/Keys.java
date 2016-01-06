package me.dylanredfield.micopi.util;

public class Keys {

    // Parse
    public static String APPLICATION_ID = "KsEdWZqTnFqPb4SpBCworrdAolP2zVOUU9THbHFz";
    public static String CLIENT_KEY = "rJch65NAPZaHDoF0wvvKLTGRd7oviACkcrYAzIjl";

    // General
    public static String OBJECT_ID_STR = "objectId";
    public static String CREATED_AT_DATE = "createdAt";
    public static String UPDATED_AT_DATE = "updatedAt";
    public static String AUTH_DATA = "authData";
    public static String ACL = "ACL";
    public static String PREF_STR = "SHARED_PREFS";
    public static String IS_FIRST_TIME_STR = "IS_FIRST_TIME";

    // User
    public static String KEY_USER = "_User";
    public static String USERNAME_STR = "username";
    public static String PASSWORD_STR = "password";
    public static String EMAIL_VERIFIED_BOOL = "emailVerified";
    public static String EMAIL_STR = "email";
    public static String FRIENDS_ARR = "friends";
    public static String GAMES_WON_NUM = "gamesWon";
    public static String NUMBER_OF_COMPILES = "numberOfCompiles";
    public static String IS_ANON_BOOL = "isAnonymous";
    public static String ROUNDS_WON_NUM = "roundsWon";

    // Game
    public static String KEY_GAME = "Game";
    public static String LANGUAGE_POINT = "Language";
    public static String GAME_POINT = "Game";
    public static String CHALLENGE_DESCRIPTION_STR = "challengeDescription";
    public static String PLAYERS_ARR = "players";
    public static String HAS_STARTED_BOOL = "hasStarted";
    public static String ROUNDS_ARR = "rounds";
    public static String IS_PUBLIC_BOOL = "isPublic";
    public static String IS_OVER_BOOL = "isOver";
    public static String INVITED_PLAYERS_ARR = "invitedPlayers";
    public static String DESIRED_NUM_PLAYERS = "desiredNumberOfPlayers";
    public static String NUM_PLAYERS_NUM = "numPlayers";
    public static String GAME_DIFFICULTY_POINT = "GameDifficulty";
    public static String IS_INVITE_BOOL = "isInvite";
    public static String INVITE_STARTER_POINT = "inviteStarter";

    // GameDifficulty
    public static String KEY_GAME_DIFFICULTY = "GameDifficulty";
    public static String DIFFICULTY_STRING = "difficultyString";
    public static String EASY_OBJECT_ID = "QYOXPi8grU";


    // GameRound
    public static String KEY_GAME_ROUND = "GameRound";
    public static String GAME_MAKER_POINT = "gameMaker";
    public static String PLAYERS_DONE_ARR = "playersDone";
    public static String PLAYERS_NOT_DONE_ARR = "playersNotDone";
    public static String ROUND_NUM = "round";
    public static String END_DATE_DATE = "endDate";
    public static String WINNERS_ARR = "winners";
    public static String IS_READY_FOR_LEADER_BOOL = "isReadyForLeader";
    public static String CHALLENGE_POINT = "Challenge";
    public static String LEADER_END_DATE_DATE = "leaderEndDate";
    public static String LEADER_POINT = "leader";
    public static String PLAYERS_STARTED = "playersStarted";

    // Language
    public static String KEY_LANGUAGE = "Language";
    public static String NAME_STR = "name";
    public static String PYTHON_ID_STR = "IofD71loU8";
    public static String SWIFT_ID_STR = "5EdFnZu5pO";
    public static String OJB_C_ID_STR = "7kNdgNs2QP";
    public static String JAVA_ID_STR = "dwCXNgpzpC";

    // Submission
    public static String KEY_SUBMISSION = "Submission";
    public static String GAME_ROUND_POINT = "GameRound";
    public static String PLAYER_POINT = "player";
    public static String POWER_UPS_USED_NUM = "powerUpsUsed";
    public static String SUBMISSION_FILE = "submissionFile";
    public static String SUBMISSION_STR = "submissionString";
    public static String CAN_EDIT_BOOL = "canEdit";

    // FriendRequest
    public static String KEY_FRIEND_REQUEST = "FriendRequest";
    public static String FROM_USER_POINT = "fromUser";
    public static String TO_USER_POINT = "toUser";

    // Cloud
    public static String SEARCH_FOR_LOBBY_CLOUD = "searchForLobby";
    public static String SEARCH_FOR_LOBBY_LANGUAGE_ID_STR = "searchForLobby";
    public static String SEARCH_FOR_LOBBY_USER_POINT = "user";

    // Challenge
    public static String KEY_CHALLENGE = "Challenge";
    public static String DESCRIPTION_STR = "description";
    //public static String NAME_STR = "name";

    // Other
    public static int USERNAME_LENGTH = 16;
    public static int EMAIL_LENGTH = 254;
    public static int FRAGMENT_REQUEST_CODE = 0;
    public static String EXTRA_FRIENDS_LIST = "me.dylanredfield.micopi.FRIENDS_LIST";
    public static String EXTRA_GAME_OBJ_ID = "me.dylanredfield.micopi.OBJ_ID_EXTRA";
    public static String EXTRA_GAME = "me.dylanredfield.micopi.GAME_EXTRA";
    public static int REFRESH_LIST_RESULT_CODE = 2;
    public static int GAME_LIST_REQUEST_CODE = 0;
    public static int CREATED_GAME_RESULT_CODE = 0;

    // GameTypes
    public static String OVER = "over";
    public static String PRIVATE_STARTED_YOUR_TURN_PLAYER = "private_started_your_turn_player";
    public static String PRIVATE_STARTED_THEIR_TURN_PLAYER = "private_started_their_turn_player";
    public static String PUBLIC_STARTED_YOUR_TURN_PLAYER = "public_started_your_turn_player";
    public static String PUBLIC_STARTED_THEIR_TURN_PLAYER = "public_started_their_turn_player";
    public static String PRIVATE_STARTED_YOUR_TURN_LEADER = "private_started_your_turn_leader";
    public static String PRIVATE_STARTED_THEIR_TURN_LEADER = "private_started_their_turn_leader";
    public static String PUBLIC_STARTED_YOUR_TURN_LEADER = "public_started_your_turn_leader";
    public static String PUBLIC_STARTED_THEIR_TURN_LEADER = "public_started_their_turn_leader";
    public static String PRIVATE_LOBBY_STARTER = "private_lobby_start";
    public static String PRIVATE_LOBBY_PLAYER = "private_lobby_player";
    public static String PUBLIC_LOBBY = "public_lobby";
}
