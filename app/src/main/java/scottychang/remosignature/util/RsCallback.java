package scottychang.remosignature.util;

import android.support.annotation.NonNull;

/**
 * Created by Scarlet on 2017/4/27.
 */

public interface RsCallback<T> {
    void onSuccess(T object);
    void onFailure(@NonNull Exception e);
}
