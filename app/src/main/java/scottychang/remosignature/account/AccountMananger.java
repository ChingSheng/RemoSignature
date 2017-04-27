package scottychang.remosignature.account;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Scarlet on 2017/4/27.
 */

public class AccountMananger {

    private final String TAG = AccountMananger.class.getSimpleName();
    private final static String PREFERENCE_NAME = "ACCOUNT_MANAGER";
    private static final String EMAIL_KEY = "EMAIL_KEY";

    private static AccountMananger sInstance;

    public static AccountMananger newInstance(Context context) {
        sInstance = new AccountMananger(context);
        return sInstance;
    }

    public static AccountMananger getInstance() {
        return sInstance;
    }

    Context mContext;

    private AccountMananger(Context context) {
        mContext = context;
    }

    public String getAccountEmail() {
        SharedPreferences sp = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(EMAIL_KEY, null);
    }

    public void setAccountEmail(String email) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(EMAIL_KEY, email);
        editor.commit();
    }

    public void resetAccountEmail() {
        setAccountEmail(null);
    }

}
