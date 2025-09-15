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

import java.util.HashMap;
import java.util.Map;

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
            // Navigate to SignupActivity
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }

    private void submitLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequestModel loginRequest = new LoginRequestModel();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        ApiService apiService = ApiClient.getApiService();

        // Force same headers as Postman
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "*/*");
        headers.put("User-Agent", "PostmanRuntime/7.46.0");
        headers.put("Cache-Control", "no-cache");
        headers.put("Connection", "keep-alive");
        headers.put("Accept-Encoding", "gzip, deflate, br");

        apiService.loginUser(headers, loginRequest).enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseModel loginResponse = response.body();
                    Log.d(TAG, "Login successful");

                    // Save info in SharedPreferences
                    getSharedPreferences("qna", MODE_PRIVATE)
                            .edit()
                            .putBoolean("isLoggedIn", true)
                            .putLong("userId", loginResponse.getUserId())
                            .putString("userName", loginResponse.getFirstName())
                            .putString("userEmail", loginResponse.getEmail())
                            .putString("token", loginResponse.getToken())
                            .apply();

                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    // Navigate to QuizSelectorActivity
                    startActivity(new Intent(LoginActivity.this, QuizSelectorActivity.class));
                    finish();
                } else {
                    Log.e(TAG, "Login failed: " + response.code());
                    Toast.makeText(LoginActivity.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
