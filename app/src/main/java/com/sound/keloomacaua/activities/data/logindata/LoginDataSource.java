package com.sound.keloomacaua.activities.data.logindata;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sound.keloomacaua.activities.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public static final String TAG = LoginDataSource.class.getSimpleName();
    private FirebaseAuth mAuth;

    public Result<LoggedInUser> registerUser(String email, String password, Activity activity) {
        LoggedInUser regsiterUser =
                new LoggedInUser(
                        java.util.UUID.randomUUID().toString(),
                        email);
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(activity, "User succesfully registeted to database",
                        Toast.LENGTH_SHORT).show();
            }
        });
        return new Result.Success<>(regsiterUser);

    }

    public Result<LoggedInUser> login(String email, String password, Activity activity) {
        final boolean[] success = {false};
        LoggedInUser loggedInUser =
                new LoggedInUser(
                        java.util.UUID.randomUUID().toString(),
                        email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        success[0] = true;
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(activity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                        success[0] = false;
                    }
                });

        if (success[0]) {
            return new Result.Success<>(loggedInUser);
        } else {
            return new Result.Error(new IOException("Error logging in", new Exception("Error loggin in")));
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}