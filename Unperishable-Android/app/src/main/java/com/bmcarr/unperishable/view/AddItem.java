package com.bmcarr.unperishable.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bmcarr.unperishable.R;
import com.bmcarr.unperishable.data.DataAccess;
import com.bmcarr.unperishable.data.Item;
import com.bmcarr.unperishable.util.ApiRequestTask;
import com.bmcarr.unperishable.util.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddItem.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddItem#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class AddItem extends Fragment {

    private OnFragmentInteractionListener mListener;


    // additem components
    private EditText itemNameEditText;
    private EditText ownerEditText;
    private Spinner categorySpinner;
    private Spinner quantitySpinner;
    private int inputYear, inputMonth, inputDay;
    private int expYear =0, expMonth=0, expDay =0;
    private TextView inputDate;
    private TextView expirationDate;





    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddItem.
     */
    public static AddItem newInstance() {
        AddItem fragment = new AddItem();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public AddItem() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        // get EditTexts
        itemNameEditText = (EditText) view.findViewById(R.id.name_edit);
        ownerEditText = (EditText) view.findViewById(R.id.owner);
        // get Spinners
        categorySpinner = (Spinner) view.findViewById(R.id.category_spinner);
        quantitySpinner = (Spinner) view.findViewById(R.id.quantity_spinner);
        //set up Spinners to have the right options
        setupSpinner(view,categorySpinner, R.array.categories_array);
        setupSpinner(view,quantitySpinner, R.array.quantities_array);
         Calendar cal = Calendar.getInstance();
        // get date texts
        inputDate = (TextView) view.findViewById(R.id.addItem_input_date);
        inputYear = cal.get(Calendar.YEAR);
        inputMonth = cal.get(Calendar.MONTH);
        inputDay = cal.get(Calendar.DAY_OF_MONTH);
        inputDate.setText((inputMonth + 1) + "-" + inputDay + "-" + inputYear);

        expirationDate = (TextView) view.findViewById(R.id.addItem_exp_date);

        expYear = cal.get(Calendar.YEAR);
        expMonth = cal.get(Calendar.MONTH);
        expDay = cal.get(Calendar.DAY_OF_MONTH);
        expirationDate.setText((expMonth + 1) + "-" + expDay + "-" + expYear);



        // button to add an item
        Button addButton = (Button) view.findViewById(R.id.finish_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String owner = ownerEditText.getText().toString();
                String itemName =  itemNameEditText.getText().toString();
                int categoryPosition = categorySpinner.getSelectedItemPosition();
                int quantityPosition = quantitySpinner.getSelectedItemPosition();

                // must do this, going from java.sql.Data to java.util.Data types
                GregorianCalendar inputCalendar = new GregorianCalendar(inputYear,
                        inputMonth, inputDay);
                Date inputDate = new Date(inputCalendar.getTimeInMillis());

                GregorianCalendar expirationCalendar = new GregorianCalendar(expYear,
                        expMonth, expDay);
                Date expirationDate = new Date(expirationCalendar.getTimeInMillis());


                if (itemName.equals("")){
                    Toast.makeText(v.getContext(),"Requires Item Name", Toast.LENGTH_LONG).show();
                }else {
                    Item addedItem = new Item(itemName ,Config.Category.getCategory(categoryPosition),
                            Config.Quantity.getQuantity(quantityPosition)).withInputDate(inputDate);

                    if(!owner.equals("")){
                        addedItem.withOwner(owner);

                    }
                    if(inputDate.compareTo(expirationDate) != 0){
                        addedItem.withExpirationDate(expirationDate);

                    }
                    DataAccess dataAccess = ((MainActivity) getActivity()).getDataAccess();

                    if (dataAccess.queryForItemOfName(itemName) != null){
                        Toast.makeText(v.getContext(),"Item of this name already exists", Toast.LENGTH_LONG).show();
                    }else {
                        //add item to database
                        dataAccess.saveItem(addedItem);
                        // switch to inv view
                        AddItem.this.getFragmentManager().beginTransaction().replace(R.id.main_panel,

                                InventoryFragment.getInstance(((MainActivity) getActivity()).getDataAccess().queryForAllItems())).commit();
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("owner", owner);
                            obj.put("name", itemName);
                            obj.put("category", categoryPosition);
                            obj.put("quantity", quantityPosition);
                            obj.put("input_date", inputCalendar.getTimeInMillis());
                            obj.put("expiration_date", expirationCalendar.getTimeInMillis());

                            ApiRequestTask art = ApiRequestTask.createPostRequest("api/additem", ApiRequestTask.RequestType.POST_ONLY,
                                    null, obj);
                            Thread t = new Thread(art);
                            t.start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    }

//                    String owner = ownerEditText.getText().toString();
//                    String itemName =  itemNameEditText.getText().toString();
//                    int categoryPosition = categorySpinner.getSelectedItemPosition();
//                    int quantityPosition = quantitySpinner.getSelectedItemPosition();
//
//                    // must do this, going from java.util.Data to java.sql.Data types
//                    GregorianCalendar inputCalendar = new GregorianCalendar(inputDatePicker.getYear(),
//                            inputDatePicker.getMonth(), inputDatePicker.getDayOfMonth());
//                    Date inputDate = new Date(inputCalendar.getTimeInMillis());
//
//                    GregorianCalendar expirationCalendar = new GregorianCalendar(expirationDatePicker.getYear(),
//                            expirationDatePicker.getMonth(), expirationDatePicker.getDayOfMonth());
//                    Date expirationDate = new Date(expirationCalendar.getTimeInMillis());
                }





            }
        });
        // button for backing out of this fragment
        Button cancelButton = (Button) view.findViewById(R.id.cancel_add_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItem.this.getFragmentManager().beginTransaction().replace(R.id.main_panel,
                        InventoryFragment.getInstance(((MainActivity) getActivity()).getDataAccess().queryForAllItems())).commit();
            }
        });

        // button for changing input date
        Button currentDateButton = (Button) view.findViewById(R.id.addCurrent_date_button);

        currentDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                inputDate.setText((monthOfYear + 1) + "-"+ dayOfMonth + "-" + year);
                                inputYear = year;
                                inputMonth = monthOfYear + 1;
                                inputDay = dayOfMonth;

                            }
                        }, inputYear, inputMonth, inputDay);
                dpd.show();

            }
        });

        // button for backing out of this fragment
        Button expDateButton = (Button) view.findViewById(R.id.addExp_date_button);

        expDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                expirationDate.setText((monthOfYear + 1) + "-"+ dayOfMonth + "-" + year);
                                expYear = year;
                                expMonth = monthOfYear + 1;
                                expDay = dayOfMonth;
                            }
                        }, expYear, expMonth, expDay);
                dpd.show();

            }
        });

        return view;
    }

    /**
     * Set the dataAdapter of a Spinner and have it add the correct Strings
     * @param view the View the spinner is in
     * @param spinner the spinner itself
     * @param stringArrayID ID of the string array for the spinner in @/strings
     */
    private void setupSpinner(View view, Spinner spinner, int stringArrayID) {
        List<String> list = new ArrayList<String>();

        for (String i : view.getResources().getStringArray(stringArrayID)){
            list.add(i);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
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

    private class CustomOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }
}
