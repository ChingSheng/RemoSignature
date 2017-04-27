package scottychang.remosignature;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import scottychang.remosignature.account.AccountMananger;
import scottychang.remosignature.signature.CanvasFragment;
import scottychang.remosignature.util.RsCallback;
import scottychang.remosignature.widget.EditEmailDialogHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CanvasFragment.OnFragmentInteractionListener {

    @BindView(R.id.content_fragment)
    FrameLayout contentFragment;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initAccountManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        initNavigationView();
        initCanvasFragment();
    }

    //================================================================================
    // AccountMananger
    //================================================================================

    private void initAccountManager(){
        AccountMananger.newInstance(this);
    }

    //================================================================================
    // CanvasFragment
    //================================================================================

    private void initCanvasFragment() {
        CanvasFragment myf = new CanvasFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_fragment, myf);
        transaction.commit();
    }

    //================================================================================
    // NavigationView
    //================================================================================

    private static final String defaultUISetEmail = "Click and set Account Email";

    private void initNavigationView() {

        navigationView.setNavigationItemSelectedListener(this);

        emailText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email_info);
        emailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditMailDialog();
            }
        });
        updateEmailText();
    }

    private void updateEmailText() {
        String defaultEmail = AccountMananger.getInstance().getAccountEmail();
        String textEmail = defaultEmail != null ? defaultEmail : defaultUISetEmail;
        emailText.setText(textEmail);
    }

    private void showEditMailDialog() {
        new EditEmailDialogHelper(this, new RsCallback<Void>() {
            @Override
            public void onSuccess(Void object) {
                updateEmailText();
            }

            @Override
            public void onFailure(@NonNull Exception e) {
                // Do nothing/
            }
        }).show();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
