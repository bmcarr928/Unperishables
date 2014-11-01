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
    // TODO: Rename parameter arguments, choose names that match
         // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
         private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ITEM = "item";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // edititem components
    private EditText itemNameEditText;
    private EditText ownerEditText;
    private Spinner categorySpinner;
    private Spinner quantitySpinner;
    private DatePicker inputDatePicker;
    private DatePicker expirationDatePicker;

    private Item theItem;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditItem.
     */
    // TODO: Rename and change types and number of parameters
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
        theItem = (Item) args.getSerializable(ITEM);
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);

        itemNameEditText = (EditText) view.findViewById(R.id.editItem_name_edit);
        itemNameEditText.setText(theItem.getName());
        ownerEditText = (EditText) view.findViewById(R.id.editItem_owner);
        if(theItem.getOwner() != null){
            ownerEditText.setText(theItem.getOwner());
        }
        categorySpinner = (Spinner) view.findViewById(R.id.editItem_category_spinner);
        quantitySpinner = (Spinner) view.findViewById(R.id.editItem_quantity_spinner);

        setupSpinner(view,categorySpinner, R.array.categories_array, theItem.getCategory().getId());
        setupSpinner(view, quantitySpinner, R.array.quantities_array, theItem.getQuantity().getId());


        inputDatePicker = (DatePicker) view.findViewById(R.id.editItem_input_date);
        if (theItem.getInputDate() != null) {
            setupDatePicker(theItem.getInputDate(), inputDatePicker);
        }
        expirationDatePicker = (DatePicker) view.findViewById(R.id.editItem_expiration_date);
        if (theItem.getExpirationDate() != null) {
            setupDatePicker(theItem.getExpirationDate(), expirationDatePicker);
        }
        Button addButton = (Button) view.findViewById(R.id.editItem_finish_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String itemName =  itemNameEditText.getText().toString();
                int categoryPosition = categorySpinner.getSelectedItemPosition();
                int quantityPosition = quantitySpinner.getSelectedItemPosition();

                GregorianCalendar inputCalendar = new GregorianCalendar(inputDatePicker.getYear(),
                        inputDatePicker.getMonth(), inputDatePicker.getDayOfMonth());
                Date inputDate = new Date(inputCalendar.getTimeInMillis());

                GregorianCalendar expirationCalendar = new GregorianCalendar(expirationDatePicker.getYear(),
                        expirationDatePicker.getMonth(), expirationDatePicker.getDayOfMonth());
                Date expirationDate = new Date(expirationCalendar.getTimeInMillis());

                String owner = ownerEditText.getText().toString();




                if (itemName.equals("")){
                    Toast.makeText(v.getContext(), "Requires Item Name", Toast.LENGTH_LONG).show();
                }else {
                    Item item = new Item(itemName , Config.Category.getCategory(categoryPosition),
                            Config.Quantity.getQuantity(quantityPosition)).withInputDate(inputDate);

                    if(!owner.equals("")){
                        item.withOwner(owner);

                    }
                    if(inputDate.compareTo(expirationDate) == 0){
                        item.withExpirationDate(expirationDate);

                    }
                    DataAccess dataAccess = ((MainActivity) getActivity()).getDataAccess();
                    if (dataAccess.queryForItemOfName(itemName) != null){
                        //delete item then re add it
                        dataAccess.deleteItem(item);
                        dataAccess.saveItem(item);
                    }else {
                        //add item to database
                        dataAccess.saveItem(item);
                    }
                    EditItem.this.getFragmentManager().beginTransaction().replace(R.id.main_panel,
                            InventoryFragment.getInstance(((MainActivity) getActivity()).getDataAccess().queryForAllItems())).commit();
                }
            }
        });
        Button deleteButton = (Button) view.findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getDataAccess().deleteItem(theItem);
                EditItem.this.getFragmentManager().beginTransaction().replace(R.id.main_panel,
                        InventoryFragment.getInstance(((MainActivity) getActivity()).getDataAccess().queryForAllItems())).commit();
            }
        });

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


    private void setupDatePicker(Date setDate, DatePicker picker){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(setDate.getTime());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        picker.updateDate(year, month, day);

    }


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

    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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



}
