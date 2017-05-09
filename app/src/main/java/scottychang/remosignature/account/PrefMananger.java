package scottychang.remosignature.account;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Scarlet on 2017/4/27.
 */

public class PrefMananger {

    private final String TAG = PrefMananger.class.getSimpleName();
    private final static String PREFERENCE_NAME = "ACCOUNT_MANAGER";
    private static final String EMAIL_KEY = "EMAIL_KEY";
    private static final String SERVER_KEY = "SERVER_KEY";

    private static PrefMananger sInstance;

    public static PrefMananger newInstance(Context context) {
        sInstance = new PrefMananger(context);
        return sInstance;
    }

    public static PrefMananger getInstance() {
        return sInstance;
    }

    Context mContext;

    private PrefMananger(Context context) {
        mContext = context;
    }

    @Deprecated
    public String getAccountEmail() {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(EMAIL_KEY, null);
    }

    @Deprecated
    public void setAccountEmail(String email) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(EMAIL_KEY, email).commit();
    }

    public String getServerSite() {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(SERVER_KEY, null);
    }

    public void setServerSite(String serverSite) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(SERVER_KEY, serverSite).commit();
    }

    public void resetServerSite() {
        setServerSite(null);
    }

    @Deprecated
    public void resetAccountEmail() {
        setAccountEmail(null);
    }

}
