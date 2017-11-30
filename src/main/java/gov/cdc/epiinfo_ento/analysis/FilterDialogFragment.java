package gov.cdc.epiinfo_ento.analysis;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import gov.cdc.epiinfo_ento.R;

public class FilterDialogFragment extends DialogFragment {

    private static EditText currentTextView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.date_filter_dialog, null);
        builder.setView(v)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            EditText txtStartDate = v.findViewById(R.id.txtStartDate);
                            EditText txtEndDate = v.findViewById(R.id.txtEndDate);

                            if (txtStartDate.getText().toString().trim().length() > 0) {
                                Date startDate = DateFormat.getDateInstance().parse(txtStartDate.getText().toString());
                                editor.putLong("filterStartDate", startDate.getTime());
                            }
                            if (txtEndDate.getText().toString().trim().length() > 0) {
                                Date endDate = DateFormat.getDateInstance().parse(txtEndDate.getText().toString());
                                editor.putLong("filterEndDate", endDate.getTime());
                            }
                            editor.commit();
                        } catch (Exception ex) {

                        }
                        ((Dashboard)getActivity()).Refresh();
                    }
                })
                .setNegativeButton(R.string.clear, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor = sharedPref.edit();

                            editor.remove("filterStartDate");
                            editor.remove("filterEndDate");
                            editor.commit();
                        } catch (Exception ex) {

                        }
                        ((Dashboard)getActivity()).Refresh();

                        FilterDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(getString(R.string.dash_filter));
        Dialog dialog = builder.create();


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ImageButton btnStartDate = v.findViewById(R.id.btnStartDate);
        final EditText txtStartDate = v.findViewById(R.id.txtStartDate);
        if (sharedPref.getLong("filterStartDate", -1) > 0) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(sharedPref.getLong("filterStartDate", -1));
            txtStartDate.setText(DateFormat.getDateInstance().format(c.getTime()));
        }
        btnStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTextView = txtStartDate;
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "startDatePicker");
            }
        });
        ImageButton btnEndDate = v.findViewById(R.id.btnEndDate);
        final EditText txtEndDate = v.findViewById(R.id.txtEndDate);
        if (sharedPref.getLong("filterEndDate", -1) > 0) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(sharedPref.getLong("filterEndDate", -1));
            txtEndDate.setText(DateFormat.getDateInstance().format(c.getTime()));
        }
        btnEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentTextView = txtEndDate;
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "endDatePicker");
            }
        });

        return dialog;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            DateFormat dateFormat = DateFormat.getDateInstance();
            Calendar c = Calendar.getInstance();
            c.set(year,month,day);
            currentTextView.setText(dateFormat.format(c.getTime()));
        }
    }
}
