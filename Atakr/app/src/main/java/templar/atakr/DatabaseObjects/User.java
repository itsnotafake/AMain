package templar.atakr.databaseobjects;

/**
 * Created by Devin on 3/8/2017.
 */

public class User {

    private String userID;
    private String displayName;

    public User(){
    }

    public User(String uid, String dn){
        userID = uid;
        displayName = dn;
    }

    public String getUserID(){return userID;}
    public void setUserID(String uid){userID = uid;}

    public String getDisplayName(){return displayName;}
    public void setDisplayName(String dn){displayName = dn;}
}
