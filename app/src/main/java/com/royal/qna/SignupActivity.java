package com.royal.qna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.royal.qna.model.UserRequestModel;
import com.royal.qna.service.ApiClient;
import com.royal.qna.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText edtFirstName, edtEmail, edtPassword;
    Button btnSubmit;
    TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind UI views
        edtFirstName = findViewById(R.id.edtSignupName);
        edtEmail = findViewById(R.id.edtSignupEmail);
        edtPassword = findViewById(R.id.edtSignupPassword);
        btnSubmit = findViewById(R.id.btnSignupSubmit);
        tvLoginLink = findViewById(R.id.tvSignupToLogin);

        btnSubmit.setOnClickListener(v -> submitSignup());

        tvLoginLink.setOnClickListener(v -> {
            // Navigate to LoginActivity directly
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void submitSignup() {
        String firstName = edtFirstName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (firstName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        UserRequestModel userRequest = new UserRequestModel();
        userRequest.setFirstName(firstName);
        userRequest.setEmail(email);
        userRequest.setPassword(password);

        ApiService apiService = ApiClient.getApiService();
        apiService.createUser(userRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Signup successful");

                    Toast.makeText(SignupActivity.this, "Signup successful! Please login.", Toast.LENGTH_SHORT).show();

                    // Send the email and firstName to LoginActivity
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    intent.putExtra("userEmail", email);
                    intent.putExtra("userName", firstName);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Signup failed with code: " + response.code());
                    Toast.makeText(SignupActivity.this, "Signup failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
