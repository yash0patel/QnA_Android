package com.royal.qna;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.royal.qna.model.QuizResponseDTO;
import com.royal.qna.service.ApiClient;
import com.royal.qna.service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizSelectorActivity extends AppCompatActivity {

    private LinearLayout layoutQuizzes;
    private TextView tvHeading, tvSubHeading;
    private ProgressBar progressBar;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_selector);

        // Safe area / insets handling (requires root id = main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        apiService = ApiClient.getApiService();
        fetchQuizzes();
    }

    private void initViews() {
        layoutQuizzes = findViewById(R.id.layoutQuizzes);
        tvHeading = findViewById(R.id.tvHeading);
        tvSubHeading = findViewById(R.id.tvSubHeading);
        progressBar = findViewById(R.id.progressBar);
    }

    private void fetchQuizzes() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<QuizResponseDTO>> call = apiService.getActiveQuizzes();
        call.enqueue(new Callback<List<QuizResponseDTO>>() {
            @Override
            public void onResponse(Call<List<QuizResponseDTO>> call, Response<List<QuizResponseDTO>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    displayQuizzes(response.body());
                } else {
                    showError("Failed to fetch quizzes");
                }
            }

            @Override
            public void onFailure(Call<List<QuizResponseDTO>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void displayQuizzes(List<QuizResponseDTO> quizzes) {
        layoutQuizzes.removeAllViews();

        if (quizzes == null || quizzes.isEmpty()) {
            tvSubHeading.setText("No quizzes available");
            return;
        }

        for (QuizResponseDTO quiz : quizzes) {
            // Create CardView
            CardView card = new CardView(this);
            card.setCardElevation(6f);
            card.setRadius(16f);
            card.setUseCompatPadding(true);
            card.setClickable(true);
            card.setForeground(getDrawable(R.drawable.ripple));

            // Layout params with margin
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int topMargin = (int) (12 * getResources().getDisplayMetrics().density);
            params.setMargins(0, topMargin, 0, 0);
            card.setLayoutParams(params);

            // TextView for quiz title
            TextView tvQuizTitle = new TextView(this);
            tvQuizTitle.setText(quiz.getTitle() == null ? ("Quiz " + quiz.getQuizId()) : quiz.getTitle());
            tvQuizTitle.setTextSize(18f);
            tvQuizTitle.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            tvQuizTitle.setPadding(32, 32, 32, 32);
            tvQuizTitle.setAllCaps(false);
            tvQuizTitle.setTypeface(tvQuizTitle.getTypeface(), android.graphics.Typeface.BOLD);
            tvQuizTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryColor));
            tvQuizTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            card.addView(tvQuizTitle);

            // Click listener
            card.setOnClickListener(v -> {
                Intent intent = new Intent(QuizSelectorActivity.this, QuizActivity.class);
                intent.putExtra("quizId", quiz.getQuizId());
                intent.putExtra("quizTitle", quiz.getTitle());
                startActivity(intent);
            });

            layoutQuizzes.addView(card);
        }
    }

    private void showError(String message) {
        tvSubHeading.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
