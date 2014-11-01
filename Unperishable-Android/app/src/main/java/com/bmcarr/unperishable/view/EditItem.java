package com.bmcarr.unperishable.view;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bmcarr.unperishable.R;
import com.bmcarr.unperishable.data.DataAccess;
import com.bmcarr.unperishable.data.Item;
import com.bmcarr.unperishable.util.Config;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class EditItem extends Fragment {
    private static final String ITEM = "item";

    private OnFragmentInteractionListener mListener;

    // edititem components
    private EditText itemNameEditText;
    private EditText ownerEditText;
    private Spinner categorySpinner;
    private Spinner quantitySpinner;
    private DatePicker inputDatePicker;
    private DatePicker expirationDatePicker;

    private Item oldItem;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditItem.
     */
    public static EditItem newInstance(Item theItem) {

        EditItem fragment = new EditItem();
        Bundle args = new Bundle();
        args.putSerializable(ITEM, theItem);
        fragment.setArguments(args);

        return fragment;
    }
    public EditItem() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        oldItem = (Item) args.getSerializable(ITEM);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);


        // get EditTexts and update them with info in oldItem
        itemNameEditText = (EditText) view.findViewById(R.id.editItem_name_edit);
        itemNameEditText.setText(oldItem.getName());

        ownerEditText = (EditText) view.findViewById(R.id.editItem_owner);
        if(oldItem.getOwner() != null){
            ownerEditText.setText(oldItem.getOwner());
        }

        // get Spinners and set them to current info in olditem
        categorySpinner = (Spinner) view.findViewById(R.id.editItem_category_spinner);
        quantitySpinner = (Spinner) view.findViewById(R.id.editItem_quantity_spinner);

        setupSpinner(view,categorySpinner, R.array.categories_array, oldItem.getCategory().getId());
        setupSpinner(view, quantitySpinner, R.array.quantities_array, oldItem.getQuantity().getId());

        // get DatePickers and if there is info about these items change the pickers
        inputDatePicker = (DatePicker) view.findViewById(R.id.editItem_input_date);
        if (oldItem.getInputDate() != null) {
            updateDatePicker(oldItem.getInputDate(), inputDatePicker);
        }
        expirationDatePicker = (DatePicker) view.findViewById(R.id.editItem_expiration_date);
        if (oldItem.getExpirationDate() != null) {
            updateDatePicker(oldItem.getExpirationDate(), expirationDatePicker);
        }

        // button for updating
        Button updateButton = (Button) view.findViewById(R.id.editItem_finish_button);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String updatedItemName =  itemNameEditText.getText().toString();
                int categoryPosition = categorySpinner.getSelectedItemPosition();
                int quantityPosition = quantitySpinner.getSelectedItemPosition();

                // must do this, going from java.sql.Data to java.util.Data types
                GregorianCalendar inputCalendar = new GregorianCalendar(inputDatePicker.getYear(),
                        inputDatePicker.getMonth(), inputDatePicker.getDayOfMonth());
                Date inputDate = new Date(inputCalendar.getTimeInMillis());

                GregorianCalendar expirationCalendar = new GregorianCalendar(expirationDatePicker.getYear(),
                        expirationDatePicker.getMonth(), expirationDatePicker.getDayOfMonth());
                Date expirationDate = new Date(expirationCalendar.getTimeInMillis());

                String owner = ownerEditText.getText().toString();




                if (updatedItemName.equals("")){
                    Toast.makeText(v.getContext(), "Requires Item Name", Toast.LENGTH_LONG).show();
                }else {

                    Item updatedItem = new Item(updatedItemName , Config.Category.getCategory(categoryPosition),
                            Config.Quantity.getQuantity(quantityPosition)).withInputDate(inputDate);

                    if(!owner.equals("")){
                        updatedItem.withOwner(owner);

                    }
                    if(inputDate.compareTo(expirationDate) != 0){
                        updatedItem.withExpirationDate(expirationDate);

                    }
                    DataAccess dataAccess = ((MainActivity) getActivity()).getDataAccess();

                        // did name change?
                    if (oldItem.getName().compareTo(updatedItemName) != 0){
                        // yes name changed

                               //is changed name already in the database?
                            if (dataAccess.queryForItemOfName(updatedItemName) != null){
                                // yes, can't use that name, name already taken. EX renaming apple
                                // to apple1 when apple1 already is in DataBase
                                Toast.makeText(v.getContext(),"Item of this name already exists", Toast.LENGTH_LONG).show();
                            }else {
                                // no, delete oldItem add updatedItem (no name conflicts)
                                dataAccess.deleteItem(oldItem);
                                dataAccess.saveItem(updatedItem);
                            }

                    }else {
                        //no, name did not change
                        dataAccess.saveItem(updatedItem);
                    }

                    EditItem.this.getFragmentManager().beginTransaction().replace(R.id.main_panel,
                            InventoryFragment.getInstance(((MainActivity) getActivity()).getDataAccess().queryForAllItems())).commit();
                }
            }
        });

        // button for deleting items
        Button deleteButton = (Button) view.findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getDataAccess().deleteItem(oldItem);
                EditItem.this.getFragmentManager().beginTransaction().replace(R.id.main_panel,
                        InventoryFragment.getInstance(((MainActivity) getActivity()).getDataAccess().queryForAllItems())).commit();
            }
        });

        // button for backing out of this fragment
        Button cancelButton = (Button) view.findViewById(R.id.editItem_cancel_add_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditItem.this.getFragmentManager().beginTransaction().replace(R.id.main_panel,
                        InventoryFragment.getInstance(((MainActivity) getActivity()).getDataAccess().queryForAllItems())).commit();
            }
        });

        return view;
    }

    /**
     * Use to update the date on a DatePicker
     * @param setDate a java.util.Date object
     * @param picker  a DatePicker object
     */
    private void updateDatePicker(Date setDate, DatePicker picker){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(setDate.getTime());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        picker.updateDate(year, month, day);

    }

    /**
     * Set the dataDapter of a Spinner and have it select a preselected position
     * @param view the View the spinner is in
     * @param spinner the spinner itselt
     * @param stringArrayID ID of the string array for the spiiner in @/strings
     * @param currentSelected  the position of the spinner
     */
    private void setupSpinner(View view, Spinner spinner, int stringArrayID, int currentSelected) {
        List<String> list = new ArrayList<String>();

        for (String i : view.getResources().getStringArray(stringArrayID)){
            list.add(i);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinner.setSelection(currentSelected);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        public void onFragmentInteraction(Uri uri);
    }

    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }


}
