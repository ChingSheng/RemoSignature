package scottychang.remosignature.svg;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Scarlet on 2017/4/20.
 */

public class SVGFileHelper {

    private static final String TAG = SVGFileHelper.class.getSimpleName();
    private File mTempFile;
    private Context mContext;
    private String mInput;
    private String url;

    public SVGFileHelper(Context context) {
        mContext = context;
    }

    public void setServerUrl(String url) {
        this.url = url;
    }

    public void setFile(String input) {

        File path = Environment.getExternalStorageDirectory();
        if (!path.exists()) {
            path.mkdirs();
        }
        mInput = input;
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

    public void sendByHttp() {

        if (url == null) {
            Log.e(TAG, "sendByHttp error: url is null");
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        final String requestBody = mInput;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null) {
                    Log.e(TAG, "onErrorResponse: unknown error");
                }

                if (error.getCause() != null) {
                    Toast.makeText(mContext, "Error - " + error.getCause().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    Log.e(TAG, "" + error.getCause().getLocalizedMessage());
                } else {
                    Toast.makeText(mContext, "Error - http status code: " + error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "status code " + error.networkResponse.statusCode);
                }
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    // can get more details such as response.headers
                    responseString = String.valueOf(response.statusCode);
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);
    }

}
