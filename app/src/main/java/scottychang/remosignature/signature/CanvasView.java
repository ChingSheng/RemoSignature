package scottychang.remosignature.signature;

import android.graphics.Bitmap;

import com.hannesdorfmann.mosby3.mvp.MvpView;

/**
 * Created by Scarlet on 2017/4/11.
 */

public interface CanvasView extends MvpView {

    void setData(Bitmap b);

}
