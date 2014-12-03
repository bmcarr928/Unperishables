package com.bmcarr.unperishable.view;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bmcarr.unperishable.R;
import com.bmcarr.unperishable.util.CreateAccountTask;
import com.bmcarr.unperishable.util.Utilities;

import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CreateAccount extends Fragment implements Observer {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "CreateAccount";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private EditText username;
    private EditText psw;
    private EditText cpsw;
    private EditText email;
    private EditText cemail;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);


        this.username = (EditText) view.findViewById(R.id.createAcct_username);
        this.psw = (EditText) view.findViewById(R.id.createAcct_pwd);
        this.cpsw = (EditText) view.findViewById(R.id.createAcct_pwd_confirm);
        this.email = (EditText) view.findViewById(R.id.createAcct_email);
        this.cemail = (EditText) view.findViewById(R.id.createAcct_email_confirm);


        Button createUser = (Button) view.findViewById(R.id.createAcct_createButton);

        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearErrors();
                if(emptyFields()) {
                    // One or more of the fields were empty. Error message comes form emptyFields()
                } else if (!isEmailValid(email.getText().toString())) {
                    email.requestFocus();
                    email.setError("Not a valid email address");
                } else if (!fieldsMatch(psw, cpsw)) {
                    psw.getText().clear();
                    cpsw.getText().clear();
                    psw.setError("Passwords don't match");
                }else if (!fieldsMatch(cemail, email)) {
                    cemail.getText().clear();
                    cemail.setError("Emails don't match");
                } else {
                    ProgressDialog progressDialog = ProgressDialog.show(CreateAccount.this.getActivity(),
                            "Creating Account", "Please wait...");
                    progressDialog.setCancelable(true);
                    CreateAccountTask createAccountTask = new CreateAccountTask(
                            username.getText().toString(), psw.getText().toString(), progressDialog);
                    createAccountTask.addObserver(CreateAccount.this);
                    Thread t = new Thread(createAccountTask);
                    t.start();
                }
            }
        });
        return view;
    }

    private void clearErrors() {
        username.setError(null);
        psw.setError(null);
        cpsw.setError(null);
        email.setError(null);
        cemail.setError(null);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean emptyFields() {
        /* Test if the username field is empty */
        if(TextUtils.isEmpty(username.getText().toString())) {
            username.setError("Please enter a username");
            username.requestFocus();
            return true;
        }
        /* Test if the password field is empty */
        if(TextUtils.isEmpty(psw.getText().toString())) {
            psw.setError("Please enter a password");
            psw.requestFocus();
            return true;
        }
        /* Test if the password confirmation field is empty */
        if(TextUtils.isEmpty(cpsw.getText().toString())) {
            cpsw.setError("Please confirm password");
            cpsw.requestFocus();
            return true;
        }
        /* Test if the email field is empty */
        if(TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Please enter an email");
            email.requestFocus();
            return true;
        }
        /* Test if the email confirmation field is empty */
        if(TextUtils.isEmpty(cemail.getText().toString())) {
            cemail.setError("Please confirm email");
            cemail.requestFocus();
            return true;
        }
        return false;
    }


    private boolean fieldsMatch(EditText srcField, EditText otherField) {
        String src = srcField.getText().toString();
        String other = otherField.getText().toString();

        /* Test if the username field is empty */
        if(!TextUtils.equals(src, other)) {
            srcField.requestFocus();
            return false;
        }
        return true;
    }

    private boolean existingUser() {
        boolean userExist = true;

        // TODO: Check if user exist here

        if(userExist) {
            username.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    public void update(Observable observable, Object data) {
        MainActivity mainActivity = (MainActivity)this.getActivity();
        final CreateAccountTask createAccountTask = (CreateAccountTask)observable;
        if ( createAccountTask.getResponseCode() == 403 ) {
            Log.d(TAG, "403 response code");
            mainActivity.runOnUiThread(new ToastMaker("Error, username already exists"));
        } else if ( createAccountTask.getResponseCode() != 200 ) {
            mainActivity.runOnUiThread(new ToastMaker("Error communicating with the server"));
        } else {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity)CreateAccount.this.getActivity()).loginUser(createAccountTask.getUsername());
                }
            });
        }
    }

    private class ToastMaker implements Runnable {

        String message;

        ToastMaker(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            MainActivity mainActivity = (MainActivity)CreateAccount.this.getActivity();
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CreateAccount.this.getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
