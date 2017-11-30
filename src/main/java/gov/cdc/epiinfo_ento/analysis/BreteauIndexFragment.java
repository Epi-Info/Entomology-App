package gov.cdc.epiinfo_ento.analysis;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gov.cdc.epiinfo_ento.EpiDbHelper;
import gov.cdc.epiinfo_ento.FormMetadata;
import gov.cdc.epiinfo_ento.R;

public class BreteauIndexFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private LinearLayout content;

    public BreteauIndexFragment() {

    }

    public static BreteauIndexFragment newInstance(String param1, String param2) {
        BreteauIndexFragment fragment = new BreteauIndexFragment();
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
        View v = inflater.inflate(R.layout.fragment_breteau_index, container, false);
        content = v.findViewById(R.id.rate_content);

        SetupGadget();

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.dash_breteau_index));
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
        void onFragmentInteraction(String uri);
    }

    private String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%.1f",d);
    }

    private int GetCircle(double index)
    {
        if (index <= 5)
            return R.drawable.circle_green;
        else if (index > 50)
            return R.drawable.circle_red;
        else
            return R.drawable.circle_yellow;
    }

    private void SetupGadget() {

        EpiDbHelper dbHelper = new EpiDbHelper(getActivity(), new FormMetadata("EpiInfoEntomology/Questionnaires/_Collection.xml", getActivity()), "_collection");
        dbHelper.open();
        Cursor c = dbHelper.getBreteauIndex();

        if (c.moveToFirst()) {
            do {

                double index = 100.0 * c.getDouble(c.getColumnIndexOrThrow("PositiveContainers")) / c.getDouble(c.getColumnIndexOrThrow("TotalHouses"));

                ImageView cir = new ImageView(getContext());
                cir.setImageResource(GetCircle(index));
                LinearLayout.LayoutParams circleLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                int circleDp = dpToPx(20);
                circleLayout.setMargins(circleDp, circleDp, circleDp, circleDp);
                cir.setLayoutParams(circleLayout);

                TextView rateText = new TextView(getContext());
                rateText.setGravity(Gravity.CENTER);
                RelativeLayout.LayoutParams rateLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                int rateDp = dpToPx(50);
                rateLayout.setMargins(0, 0, 0, rateDp);
                rateText.setLayoutParams(rateLayout);
                rateText.setTextSize(100);
                rateText.setText(fmt(index));

                TextView locText = new TextView(getContext());
                locText.setGravity(Gravity.CENTER);
                RelativeLayout.LayoutParams locLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                int locDp = dpToPx(70);
                locLayout.setMargins(0, locDp, 0, 0);
                locText.setLayoutParams(locLayout);
                locText.setTextSize(25);
                locText.setText(c.getString(c.getColumnIndexOrThrow("Region")));

                RelativeLayout layout = new RelativeLayout(getContext());
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(350)));
                layout.addView(cir);
                layout.addView(rateText);
                layout.addView(locText);

                content.addView(layout);
            } while (c.moveToNext());
        }
        else
        {
            ImageView cir = new ImageView(getContext());
            cir.setImageResource(R.drawable.circle);
            LinearLayout.LayoutParams circleLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            int circleDp = dpToPx(20);
            circleLayout.setMargins(circleDp, circleDp, circleDp, circleDp);
            cir.setLayoutParams(circleLayout);

            TextView rateText = new TextView(getContext());
            rateText.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams rateLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            int rateDp = dpToPx(50);
            rateLayout.setMargins(0, 0, 0, rateDp);
            rateText.setLayoutParams(rateLayout);
            rateText.setTextSize(100);
            rateText.setText("N/A");

            TextView locText = new TextView(getContext());
            locText.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams locLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            int locDp = dpToPx(70);
            locLayout.setMargins(0, locDp, 0, 0);
            locText.setLayoutParams(locLayout);
            locText.setTextSize(25);
            locText.setText(getString(R.string.insuff_data));

            RelativeLayout layout = new RelativeLayout(getContext());
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(350)));
            layout.addView(cir);
            layout.addView(rateText);
            layout.addView(locText);

            content.addView(layout);
        }

    }

    public int dpToPx(int dp) {
        float density = getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

}
