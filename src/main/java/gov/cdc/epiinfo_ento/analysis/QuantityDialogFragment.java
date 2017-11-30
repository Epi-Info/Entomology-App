package gov.cdc.epiinfo_ento.analysis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import gov.cdc.epiinfo_ento.R;
import gov.cdc.epiinfo_ento.RecordList;

public class QuantityDialogFragment extends DialogFragment {

    private static EditText currentTextView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.quantity_dialog, null);

        Spinner spinner = v.findViewById(R.id.cbxQuantity);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.quantity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int q = ((Spinner) v.findViewById(R.id.cbxQuantity)).getSelectedItemPosition()+1;
                        ((RecordList)getActivity()).MakeCopies(q);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        QuantityDialogFragment.this.getDialog().cancel();
                    }
                });
        Dialog dialog = builder.create();

        return dialog;
    }

}
