package com.sound.keloomacaua.activities.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.app.Activity;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sound.keloomacaua.R;
import com.sound.keloomacaua.activities.data.logindata.LoginRepository;
import com.sound.keloomacaua.activities.data.logindata.Result;
import com.sound.keloomacaua.activities.data.model.LoggedInUser;

import java.util.UUID;

import static android.view.View.GONE;

public class LoginViewModel extends ViewModel {


    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private FirebaseAuth mAuth;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void register(String username, String password, Activity activity) {
        // can be launched in a separate asynchronous job
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(activity, taskRegister -> {
            if (taskRegister.isSuccessful()) {
                Toast.makeText(activity, "User succesfully registeted to database",
                        Toast.LENGTH_SHORT).show();
//                LoggedInUser regsiterUser =
//                        new LoggedInUser(
//                                java.util.UUID.randomUUID().toString(),
//                                username);

                loginResult.setValue(new LoginResult(new LoggedInUserView(username)));

            } else {
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(activity, taskLogin -> {
                            if (taskLogin.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginViewModel", "signInWithEmail:success");
//                                final LoggedInUser[] loginUser = {new LoggedInUser(
//                                        UUID.randomUUID().toString(),
//                                        username)};

                                loginResult.setValue(new LoginResult(new LoggedInUserView(username)));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LoginViewModel", "signInWithEmail:failure", taskLogin.getException());
                                Toast.makeText(activity, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                loginResult.setValue(new LoginResult(R.string.login_failed));
                            }
                        });
            }
        });
        /*Result<LoggedInUser> result = loginRepository.login(username, password, activity);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }*/
    }

    /*public void register(String username, String password, Activity activity) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.register(username, password, activity);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.register_failed));
        }
    }*/

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}