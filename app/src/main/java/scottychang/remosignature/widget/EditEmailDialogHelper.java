package scottychang.remosignature.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import scottychang.remosignature.account.AccountMananger;
import scottychang.remosignature.util.RsCallback;

/**
 * Created by Scarlet on 2017/4/27.
 */

public class EditEmailDialogHelper {

    Context mContext;
    RsCallback<Void> mOnButtonClickCallback;
    public EditEmailDialogHelper(Context context, RsCallback<Void> callback) {
        mContext = context;
        mOnButtonClickCallback = callback;
    }

    public void show() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Set account Email");

        final EditText input = new EditText(mContext);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        String text = AccountMananger.getInstance().getAccountEmail();
        String defaultInput = text;

        input.setText(defaultInput);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String email = input.getText().toString();

                        if (email == null || email.length() == 0) {
                            AccountMananger.getInstance().resetAccountEmail();
                            Toast.makeText(mContext, "Clean Email setting", Toast.LENGTH_SHORT).show();
                        } else {
                            AccountMananger.getInstance().setAccountEmail(email);
                        }
                        dialog.dismiss();
                        mOnButtonClickCallback.onSuccess(null);
                    }
                });

        alertDialog.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();


    }

}
