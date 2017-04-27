package scottychang.remosignature.signature;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import scottychang.remosignature.R;
import scottychang.remosignature.account.AccountMananger;
import scottychang.remosignature.svg.SVGfileHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CanvasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CanvasFragment extends MvpFragment<CanvasView, CanvasPresenter> implements CanvasView {

    private static final String TAG = CanvasFragment.class.getSimpleName();

    Unbinder unbinder;
    @BindView(R.id.signatureview)
    SignaturePad signatureview;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private OnFragmentInteractionListener mListener;

    public CanvasFragment() {

    }

    @Override
    public CanvasPresenter createPresenter() {
        return new CanvasPresenter();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CanvasFragment.
     */
    public static CanvasFragment newInstance() {
        CanvasFragment fragment = new CanvasFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_canvas, container, false);
        unbinder = ButterKnife.bind(this, view);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureview.clear();
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String svgString = signatureview.getSignatureSvg();
                        SVGfileHelper helper = new SVGfileHelper();
                        helper.setFile(svgString);
                        sendMail(helper.getFile());
                    }
                }).start();

                return true;
            }
        });

        return view;
    }

    private void sendMail(File file) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        String[] to = new String[1];
        to[0] = AccountMananger.getInstance().getAccountEmail();

        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "RemoSignature svg file");
        intent.putExtra(Intent.EXTRA_TEXT, "Text");

        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
        intent.setType("application/text");

        startActivity(Intent.createChooser(intent, "Choose one"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                                               + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setData(Bitmap b) {
        getPresenter().doA();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
