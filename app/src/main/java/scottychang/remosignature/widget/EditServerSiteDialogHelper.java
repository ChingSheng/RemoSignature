package scottychang.remosignature.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import scottychang.remosignature.R;
import scottychang.remosignature.account.PrefMananger;
import scottychang.remosignature.util.RsCallback;

/**
 * Created by Scarlet on 2017/4/27.
 */

public class EditServerSiteDialogHelper {

    Context mContext;
    RsCallback<Void> mOnButtonClickCallback;
    public EditServerSiteDialogHelper(Context context, RsCallback<Void> callback) {
        mContext = context;
        mOnButtonClickCallback = callback;
    }

    public void show() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(mContext.getString(R.string.set_server_site));

        final EditText input = new EditText(mContext);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        String text = PrefMananger.getInstance().getServerSite();
        String defaultInput = text;

        input.setText(defaultInput);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(
                mContext.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String serverSite = input.getText().toString();

                        if (serverSite == null || serverSite.length() == 0) {
                            PrefMananger.getInstance().resetServerSite();
                            Toast.makeText(mContext, mContext.getString(R.string.clean_server_site), Toast.LENGTH_SHORT).show();
                        } else {
                            PrefMananger.getInstance().setServerSite(serverSite);
                        }
                        dialog.dismiss();
                        mOnButtonClickCallback.onSuccess(null);
                    }
                });

        alertDialog.setNegativeButton(
                mContext.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

}
