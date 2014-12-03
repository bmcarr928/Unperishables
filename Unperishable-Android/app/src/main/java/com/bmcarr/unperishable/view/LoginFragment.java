package com.bmcarr.unperishable.view;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bmcarr.unperishable.R;
import com.bmcarr.unperishable.util.CreateAccountTask;
import com.bmcarr.unperishable.util.LoginUserTask;

import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LoginFragment extends Fragment implements Observer {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private EditText usernameField;
    private EditText passwordField;

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
    public LoginFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        this.usernameField = (EditText) view.findViewById(R.id.txtUsername);
        this.passwordField = (EditText) view.findViewById(R.id.txtPassword);

        Button loginButton = (Button) view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!emptyFields()){
                    ProgressDialog progressDialog = ProgressDialog.show(LoginFragment.this.getActivity(),
                            "Logging In", "Please wait...");
                    progressDialog.setCancelable(true);
                    LoginUserTask createAccountTask = new LoginUserTask(usernameField.getText().toString(),
                          passwordField.getText().toString(), progressDialog);
                    createAccountTask.addObserver(LoginFragment.this);
                    Thread t = new Thread(createAccountTask);
                    t.start();
                }
            }
        });

        Button createAccountButton = (Button) view.findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment.this.getFragmentManager().beginTransaction().replace(R.id.main_panel, new CreateAccount()).commit();
            }
        });
        return view;
    }

    private void clearErrors() {
        usernameField.setError(null);
        passwordField.setError(null);
    }

    private boolean emptyFields() {
        /* Test if the username field is empty */
        if(TextUtils.isEmpty(usernameField.getText().toString())) {
            usernameField.setError("Please enter a username");
            usernameField.requestFocus();
            return true;
        }
        /* Test if the password field is empty */
        if(TextUtils.isEmpty(passwordField.getText().toString())) {
            passwordField.setError("Please enter a password");
            passwordField.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    public void update(Observable observable, Object data) {
        MainActivity mainActivity = (MainActivity)this.getActivity();
        final LoginUserTask loginUserTask = (LoginUserTask)observable;
        if ( loginUserTask.getResponseCode() == 403 ) {
            mainActivity.runOnUiThread(new ToastMaker("Error, username/password combo invalid"));
        } else if ( loginUserTask.getResponseCode() != 200 ) {
            mainActivity.runOnUiThread(new ToastMaker("Error communicating with the server"));
        } else {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity)LoginFragment.this.getActivity()).loginUser(loginUserTask.getUsername());
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
            MainActivity mainActivity = (MainActivity)LoginFragment.this.getActivity();
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginFragment.this.getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}

