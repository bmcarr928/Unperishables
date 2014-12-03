package com.bmcarr.unperishable.view;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bmcarr.unperishable.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LoginFragment extends Fragment {
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
        this.passwordField = (EditText) view.findViewById(R.id.txtUsername);

        Button loginButton = (Button) view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!emptyFields()){
                  if (existingUser()) {

                  } else {
                      Toast toast= Toast.makeText(getActivity(), "User already exist!", Toast.LENGTH_SHORT);
                      toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, -50);
                      toast.show();
                  }
                }

                ((MainActivity)LoginFragment.this.getActivity()).loginUser(LoginFragment.this.usernameField.getText().toString());


            }
        });

        Button createAccountButton = (Button) view.findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });


        return view;
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
    private boolean existingUser() {
        boolean userExist = true;

        // TODO: Check if user exist here


        if(userExist) {
            usernameField.requestFocus();
            return true;
        }
        return false;
    }

}
