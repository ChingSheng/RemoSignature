package scottychang.remosignature.signature;

import android.util.Log;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

/**
 * Created by Scarlet on 2017/4/11.
 */

public class CanvasPresenter extends MvpBasePresenter<CanvasView> {

    private static final String TAG = CanvasPresenter.class.getSimpleName();

    public void doA(){
        Log.d(TAG, "DoA");
    }

}
