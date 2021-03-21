package com.sound.keloomacaua.activities.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.sound.keloomacaua.R;
import com.sound.keloomacaua.activities.ui.game.CreateOrJoinActivity;

public class LoginActivity extends AppCompatActivity {

    Button registerButton;
    EditText usernameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.register);
        usernameEditText = findViewById(R.id.username);

        registerButton.setOnClickListener(v -> {
            registerButton.setEnabled(false);
            //register
            String username = usernameEditText.getText().toString();
            auth.signInAnonymously().addOnCompleteListener(authResultTask -> {
                if (authResultTask.isSuccessful()) {
                    UserProfileChangeRequest profileChange = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();
                    FirebaseUser loggedInUser = authResultTask.getResult().getUser();
                    loggedInUser.updateProfile(profileChange).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startGameListActivity();
                        } else {
                            showLoginFailed();
                        }
                    });
                } else {
                    showLoginFailed();
                }
            });
        });

        //autologin if user is already signed in
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            startGameListActivity();
        }
    }

    private void showLoginFailed() {
        Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
        registerButton.setEnabled(true);
    }

    private void startGameListActivity() {
        Intent intent = new Intent(getApplicationContext(), CreateOrJoinActivity.class);
        startActivity(intent);
        registerButton.setEnabled(true);
    }
}