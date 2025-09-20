// LoginActivity.java
package com.royal.qna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.royal.qna.model.LoginRequestModel;
import com.royal.qna.model.LoginResponseModel;
import com.royal.qna.service.ApiClient;
import com.royal.qna.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView tvSignupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtEmail = findViewById(R.id.edtLoginEmail);
        edtPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLoginSubmit);
        tvSignupLink = findViewById(R.id.tvLoginNewUser);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userEmail")) {
            String email = intent.getStringExtra("userEmail");
            edtEmail.setText(email);
        }

        btnLogin.setOnClickListener(v -> submitLogin());
        tvSignupLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }

    private void submitLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        Log.d(TAG, "submitLogin: email=" + email + " password=" + (password.isEmpty() ? "[empty]" : "[hidden]"));

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequestModel loginRequest = new LoginRequestModel();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        Log.d(TAG, "Calling API: loginUser with " + loginRequest);

        ApiService apiService = ApiClient.getApiService();
        apiService.loginUser(loginRequest).enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                Log.d(TAG, "onResponse: HTTP " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseModel loginResponse = response.body();
                    Log.d(TAG, "Login successful: " + loginResponse);

                    getSharedPreferences("qna", MODE_PRIVATE)
                            .edit()
                            .putBoolean("isLoggedIn", true)
                            .putLong("userId", loginResponse.getUserId())
                            .putString("userName", loginResponse.getFirstName())
                            .putString("userEmail", loginResponse.getEmail())
                            .apply();

                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, QuizSelectorActivity.class));
                    finish();
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody", e);
                    }
                    Log.e(TAG, "Login failed: code=" + response.code() + " body=" + errorBody);
                    Toast.makeText(LoginActivity.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
