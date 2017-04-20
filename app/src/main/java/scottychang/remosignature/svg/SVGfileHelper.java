package scottychang.remosignature.svg;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Scarlet on 2017/4/20.
 */

public class SVGfileHelper {

    private static final String TAG = SVGfileHelper.class.getSimpleName();

    File mTempFile;

    public void setFile(String input) {

        File path = Environment.getExternalStorageDirectory();
        if (!path.exists()) {
            path.mkdirs();
        }

        mTempFile = new File(path, "RemoSig.svg");

        try {
            FileOutputStream fOut = new FileOutputStream(mTempFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(input);

            myOutWriter.close();
            fOut.flush();
            fOut.close();

        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public File getFile() {
        return mTempFile;
    }

    public void deleteFile() {
        mTempFile.delete();
    }
}
