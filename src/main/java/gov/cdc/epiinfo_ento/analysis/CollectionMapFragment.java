package gov.cdc.epiinfo_ento.analysis;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import gov.cdc.epiinfo_ento.EpiDbHelper;
import gov.cdc.epiinfo_ento.FormMetadata;
import gov.cdc.epiinfo_ento.R;

import static gov.cdc.epiinfo_ento.R.id.ActMap;

public class CollectionMapFragment extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;
    private MapView mapView;
    private GoogleMap gMap;
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    public CollectionMapFragment() {

    }

    public static CollectionMapFragment newInstance(String param1, String param2) {
        CollectionMapFragment fragment = new CollectionMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_collection_map, container, false);

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.dash_collection_map));
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle)
    {
        mapView = view.findViewById(ActMap);
        mapView.onCreate(bundle);
        mapView.onResume();
        mapView.getMapAsync(this);
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
    public void onPause() {
        super.onPause();
        if (gMap != null) {
            mapView.onPause();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gMap != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (gMap != null) {
            mapView.onSaveInstanceState(outState);
        }
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        if (gMap != null) {
            mapView.onLowMemory();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (gMap != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        SetupGadget(googleMap);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String uri);
    }

    private void SetupGadget(GoogleMap googleMap) {
        this.gMap = googleMap;

        ClusterManager<MyItem> clusterManager = new ClusterManager<MyItem>(getActivity(),googleMap);
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        minLatitude = 81.0;
        maxLatitude = -81.0;
        minLongitude  = 181.0;
        maxLongitude  = -181.0;

        EpiDbHelper dbHelper = new EpiDbHelper(getActivity(), new FormMetadata("EpiInfoEntomology/Questionnaires/_Collection.xml", getActivity()), "_collection");
        dbHelper.open();
        Cursor c = dbHelper.getCollectionActivityMap();

        if (c.moveToFirst()) {
            do {
                try {
                    double latitude = c.getDouble(c.getColumnIndexOrThrow("Latitude"));
                    double longitude = c.getDouble(c.getColumnIndexOrThrow("Longitude"));

                    if (latitude < 5000) {
                        minLatitude = (minLatitude > latitude) ? latitude : minLatitude;
                        maxLatitude = (maxLatitude < latitude) ? latitude : maxLatitude;
                        minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
                        maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;

                        clusterManager.addItem(new MyItem(latitude, longitude));
                    }
                }
                catch (Exception ex)
                {

                }
            } while (c.moveToNext());

            clusterManager.cluster();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(new LatLng(minLatitude,minLongitude),new LatLng(maxLatitude,maxLongitude)), 22));
        }
    }

    public int dpToPx(int dp) {
        float density = getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public class MyItem implements ClusterItem {
        private LatLng mPosition;
        private String mTitle;
        private String mSnippet;

        public MyItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        public MyItem(double lat, double lng, String title, String snippet) {
            mPosition = new LatLng(lat, lng);
            mTitle = title;
            mSnippet = snippet;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public String getTitle() {
            return mTitle;
        }

        @Override
        public String getSnippet() {
            return mSnippet;
        }
    }

}
