package com.bmcarr.unperishable.view;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        this.passwordField = (EditText) view.findViewById(R.id.txtPassword);

        Button loginButton = (Button) view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Make sure the name and password are not empty before allowing access */
                if(!emptyLoginFields()) {
                    ((MainActivity)LoginFragment.this.getActivity()).loginUser(LoginFragment.this.usernameField.getText().toString());
                }
            }
        });

        Button createAccountButton = (Button) view.findViewById(R.id.login_button);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });


        return view;
    }

    /**
     * Check to see if either the username or password login field are empty.
     * If they are the it alerts the user through a toast, color change and
     * changes to focus to the first field that is empty.
     *
     * @return true if any of the login fields are empty
     *         false neither of the fields are empty
     *
     */
    private boolean emptyLoginFields() {
        boolean emptyField = false;
        StringBuilder stringBuilder = new StringBuilder(); // used for building error string
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        /* Test if the username field is empty */
        if(TextUtils.isEmpty(username)) {
            stringBuilder.append("Please enter a username");
            usernameField.requestFocus();
            /*TODO: Change this to background color transitions instead of alpha*/
            /* Haven't figured out how to do this on the background with color yet */
            ObjectAnimator usernameHighlight = ObjectAnimator.ofFloat(usernameField, "alpha", 0f, 1f);
            usernameHighlight.setDuration(1000);
            usernameHighlight.start();
            emptyField = true;
        }
        /* Test if the password field is empty */
        if(TextUtils.isEmpty(password)) {
            if(emptyField) { // User name is empty as well
                stringBuilder.append(" & password");
            } else { // Just password
                stringBuilder.append("Please enter a password");
                passwordField.requestFocus();
                emptyField = true;
            }
            /*TODO: Change this to background color transitions instead of alpha*/
            /* Haven't figured out how to do this on the background with color yet */
            ObjectAnimator passwordHighlight = ObjectAnimator.ofFloat(passwordField, "alpha", 0f, 1f);
            passwordHighlight.setDuration(1000);
            passwordHighlight.start();
        }
        /* If either field is empty then show an error as a Toast */
        if(emptyField) {
            Toast.makeText(getActivity(), stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

}
