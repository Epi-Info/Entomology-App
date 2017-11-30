package gov.cdc.epiinfo_ento.analysis;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;

import java.util.LinkedList;

import gov.cdc.epiinfo_ento.EpiDbHelper;
import gov.cdc.epiinfo_ento.FormMetadata;
import gov.cdc.epiinfo_ento.R;

public class ContainerTypesFragment extends Fragment {

    private PieChart chart;
    private OnFragmentInteractionListener mListener;

    public ContainerTypesFragment() {
        // Required empty public constructor
    }

    public static ContainerTypesFragment newInstance(String param1, String param2) {
        ContainerTypesFragment fragment = new ContainerTypesFragment();
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
        View v = inflater.inflate(R.layout.fragment_container_types, container, false);
        chart = v.findViewById(R.id.plot);

        SetupGadget();

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.dash_container_types));
        }

        return v;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String uri);
    }

    private void SetupGadget() {

        chart.getBackgroundPaint().setColor(Color.TRANSPARENT);
        chart.getBorderPaint().setColor(Color.TRANSPARENT);


        chart.clear();
        try {

            FormMetadata metadata = new FormMetadata("EpiInfoEntomology/Questionnaires/_Collection.xml", getActivity());
            EpiDbHelper dbHelper = new EpiDbHelper(getActivity(), metadata, "_collection");
            dbHelper.open();
            LinkedList<String> allTypes = metadata.GetFieldByName("ContainerTypes").getListValues();//.getList();
            Cursor c = dbHelper.getContainerTypes();
            int[] colors = {0xFF00AAF8, 0xFF006CF8, 0xFF635EFF, 0xFFBD0CF9, 0xFFF73BD7, 0xFFF30000, 0xFFFF7700, 0xFFFFCB00, 0xFFFFEE00, 0xFFB0F007, 0xFF23CC06, 0xFF1CF1CE};

            if (c.moveToFirst()) {
                int counter = 0;
                do {
                    String val = allTypes.get(Integer.parseInt(c.getString(c.getColumnIndexOrThrow("ContainerTypes"))) % 100).split("/")[0];//.split("||")[0];

                    if (val == null || val.toLowerCase().equals("inf") || val.equals("")) {
                        val = "Missing";
                    }

                    int count = c.getInt(c.getColumnIndexOrThrow("Total"));
                    chart.addSeries(new Segment(val + " (" + count + ")", count), new SegmentFormatter(colors[counter % 12], colors[counter % 12] - 0x33000000));
                    counter++;
                } while (c.moveToNext());
                try {
                    chart.getRenderer(PieRenderer.class).setDonutSize(0, PieRenderer.DonutMode.PERCENT);
                    chart.redraw();
                } catch (Exception ex) {

                }
            } else {
                chart.addSeries(new Segment(getString(R.string.insuff_data), 1), new SegmentFormatter(colors[1], colors[1] - 0x33000000));
                chart.getRenderer(PieRenderer.class).setDonutSize(0, PieRenderer.DonutMode.PERCENT);
                chart.redraw();
            }
        }
        catch (Exception ex)
        {

        }

    }

}
