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
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import scottychang.remosignature.account.PrefMananger;
import scottychang.remosignature.signature.CanvasFragment;
import scottychang.remosignature.svg.SVGFileHelper;
import scottychang.remosignature.util.RsCallback;
import scottychang.remosignature.widget.EditServerSiteDialogHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CanvasFragment.OnFragmentInteractionListener {

    @BindView(R.id.content_fragment)
    FrameLayout contentFragment;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    TextView serverText;

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
    // PrefMananger
    //================================================================================

    private void initAccountManager(){
        PrefMananger.newInstance(this);
    }

    //================================================================================
    // CanvasFragment
    //================================================================================

    CanvasFragment mCanvasFragment;

    private void initCanvasFragment() {
        mCanvasFragment = new CanvasFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_fragment, mCanvasFragment);
        transaction.commit();
    }

    //================================================================================
    // NavigationView
    //================================================================================

    private void initNavigationView() {

        navigationView.setNavigationItemSelectedListener(this);

        serverText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.server_info);
        serverText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditServerDialog();
            }
        });
        updateServerText();
    }

    private void updateServerText() {
        String defaultServer = PrefMananger.getInstance().getServerSite();
        String textServer = defaultServer != null ? defaultServer : getString(R.string.set_server_site);
        serverText.setText(textServer);
    }

    private void showEditServerDialog() {
        new EditServerSiteDialogHelper(this, new RsCallback<Void>() {
            @Override
            public void onSuccess(Void object) {
                updateServerText();
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String serverUrl= PrefMananger.getInstance().getServerSite();
                    if (serverUrl != null && serverUrl.contains("http://")) {
                        String svgString = mCanvasFragment.getCurrentCanvasSVG();
                        SVGFileHelper helper = new SVGFileHelper(MainActivity.this);
                        helper.setServerUrl(serverUrl);
                        helper.setFile(svgString);
                        helper.sendByHttp();
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.invalid_server_url), Toast.LENGTH_SHORT).show();
                    }
                }
            }).start();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
