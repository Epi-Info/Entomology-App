package gov.cdc.epiinfo_ento.analysis;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import gov.cdc.epiinfo_ento.R;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ContainerTypesFragment.OnFragmentInteractionListener,
        ContainerIndexFragment.OnFragmentInteractionListener,
        HouseIndexFragment.OnFragmentInteractionListener,
        BreteauIndexFragment.OnFragmentInteractionListener,
        PupaeIndexFragment.OnFragmentInteractionListener,
        OvitrapIndexFragment.OnFragmentInteractionListener,
        GravidIndexFragment.OnFragmentInteractionListener,
        GravidDensityFragment.OnFragmentInteractionListener,
        OvitrapDensityFragment.OnFragmentInteractionListener,
        PupaePersonFragment.OnFragmentInteractionListener,
        PupaeContainerFragment.OnFragmentInteractionListener,
        CollectionMapFragment.OnFragmentInteractionListener,
        TrappingMapFragment.OnFragmentInteractionListener,
        InfestationMapFragment.OnFragmentInteractionListener {

    private int currentGadget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialogFragment dialog = new FilterDialogFragment();
                new FilterDialogFragment().show(getSupportFragmentManager(),"filterDialog");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_container_types);
        currentGadget = R.id.nav_container_types;
        //NOTE:  Open fragment1 initially.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, new ContainerTypesFragment());
        ft.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem mnuFreq = menu.add(0, 0, 0, R.string.menu_exit);
        mnuFreq.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        mnuFreq.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == 0)
        {
            super.onBackPressed();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void Refresh()
    {
        Fragment fragment = null;

        if (currentGadget == R.id.nav_container_types) {
            fragment = new ContainerTypesFragment();
        } else if (currentGadget == R.id.nav_container_index) {
            fragment = new ContainerIndexFragment();
        } else if (currentGadget == R.id.nav_house_index) {
            fragment = new HouseIndexFragment();
        } else if (currentGadget == R.id.nav_breteau_index) {
            fragment = new BreteauIndexFragment();
        } else if (currentGadget == R.id.nav_pupae_index) {
            fragment = new PupaeIndexFragment();
        } else if (currentGadget == R.id.nav_ovitrap_index) {
            fragment = new OvitrapIndexFragment();
        } else if (currentGadget == R.id.nav_gravid_index) {
            fragment = new GravidIndexFragment();
        } else if (currentGadget == R.id.nav_gravid_density) {
            fragment = new GravidDensityFragment();
        } else if (currentGadget == R.id.nav_ovitrap_density) {
            fragment = new OvitrapDensityFragment();
        } else if (currentGadget == R.id.nav_pupae_per_person) {
            fragment = new PupaePersonFragment();
        } else if (currentGadget == R.id.nav_pupae_per_container) {
            fragment = new PupaeContainerFragment();
        } else if (currentGadget == R.id.nav_collection_map) {
            fragment = new CollectionMapFragment();
        } else if (currentGadget == R.id.nav_trapping_map) {
            fragment = new TrappingMapFragment();
        } else if (currentGadget == R.id.nav_infestation_map) {
            fragment = new InfestationMapFragment();
        }

        //NOTE: Fragment changing code
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//NOTE: creating fragment object
        Fragment fragment = null;

        if (id == R.id.nav_container_types) {
            fragment = new ContainerTypesFragment();
        } else if (id == R.id.nav_container_index) {
            fragment = new ContainerIndexFragment();
        } else if (id == R.id.nav_house_index) {
            fragment = new HouseIndexFragment();
        } else if (id == R.id.nav_breteau_index) {
            fragment = new BreteauIndexFragment();
        } else if (id == R.id.nav_pupae_index) {
            fragment = new PupaeIndexFragment();
        } else if (id == R.id.nav_ovitrap_index) {
            fragment = new OvitrapIndexFragment();
        } else if (id == R.id.nav_gravid_index) {
            fragment = new GravidIndexFragment();
        } else if (id == R.id.nav_gravid_density) {
            fragment = new GravidDensityFragment();
        } else if (id == R.id.nav_ovitrap_density) {
            fragment = new OvitrapDensityFragment();
        } else if (id == R.id.nav_pupae_per_person) {
            fragment = new PupaePersonFragment();
        } else if (id == R.id.nav_pupae_per_container) {
            fragment = new PupaeContainerFragment();
        } else if (id == R.id.nav_collection_map) {
            fragment = new CollectionMapFragment();
        } else if (id == R.id.nav_trapping_map) {
            fragment = new TrappingMapFragment();
        } else if (id == R.id.nav_infestation_map) {
            fragment = new InfestationMapFragment();
        }

        currentGadget = id;

        //NOTE: Fragment changing code
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
        }

        //NOTE:  Closing the drawer after selecting
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); //Ya you can also globalize this variable :P
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String text) {

        getSupportActionBar().setTitle(text);

    }

}