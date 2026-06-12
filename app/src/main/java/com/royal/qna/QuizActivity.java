package com.royal.qna;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.royal.qna.model.QuizQuestionResponseDTO;
import com.royal.qna.service.ApiClient;
import com.royal.qna.service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvHeading;
    private TextView tvQuestion;
    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnEndQuiz;
    private ProgressBar progressBar;

    private List<QuizQuestionResponseDTO> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String selectedAnswer = "";

    private ApiService apiService;
    private long quizId = 1L;
    private String quizTitle = "Quiz";

    private ColorStateList defaultTint;
    private ColorStateList selectedTint;
    private ColorStateList correctTint;
    private ColorStateList wrongTint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        // insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // read intent extras
        quizId = getIntent().getLongExtra("quizId", 1L);
        String p = getIntent().getStringExtra("quizTitle");
        if (p != null && !p.trim().isEmpty()) quizTitle = p;

        bindViews();
        initColors();
        apiService = ApiClient.getApiService();
        loadQuestions();
    }

    private void bindViews() {
        tvHeading = findViewById(R.id.tvHeading);
        tvQuestion = findViewById(R.id.tvQuestion);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnEndQuiz = findViewById(R.id.btnEndQuiz);
        progressBar = findViewById(R.id.progressBar);

        btnOption1.setOnClickListener(this);
        btnOption2.setOnClickListener(this);
        btnOption3.setOnClickListener(this);
        btnOption4.setOnClickListener(this);
        btnEndQuiz.setOnClickListener(this);
    }

    private void initColors() {
        defaultTint = ContextCompat.getColorStateList(this, R.color.primaryColor);
        selectedTint = ContextCompat.getColorStateList(this, R.color.selectedColor);
        correctTint = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.correctColor));
        wrongTint = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.wrongColor));
    }

    private void loadQuestions() {
        setLoading(true);
        disableOptions();

        Call<List<QuizQuestionResponseDTO>> call = apiService.getQuestionsByQuizId(quizId);
        call.enqueue(new Callback<List<QuizQuestionResponseDTO>>() {
            @Override
            public void onResponse(Call<List<QuizQuestionResponseDTO>> call, Response<List<QuizQuestionResponseDTO>> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body();
                    if (questionList != null && !questionList.isEmpty()) {
                        currentQuestionIndex = 0;
                        score = 0;
                        displayQuestion();
                        enableOptions();
                    } else {
                        showError("No questions available for this quiz");
                    }
                } else {
                    showError("Failed to load questions: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<QuizQuestionResponseDTO>> call, Throwable t) {
                setLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void setLoading(boolean loading) {
        if (progressBar != null) progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        int contentVis = loading ? View.INVISIBLE : View.VISIBLE;
        tvHeading.setVisibility(contentVis);
        tvQuestion.setVisibility(contentVis);
        btnOption1.setVisibility(contentVis);
        btnOption2.setVisibility(contentVis);
        btnOption3.setVisibility(contentVis);
        btnOption4.setVisibility(contentVis);
        btnEndQuiz.setVisibility(contentVis);

        // if not loading and we have questions, update heading
        if (!loading && questionList != null && !questionList.isEmpty()) {
            setHeading(quizTitle, currentQuestionIndex + 1, questionList.size());
        }
    }

    private void displayQuestion() {
        if (questionList == null || currentQuestionIndex >= questionList.size()) return;
        QuizQuestionResponseDTO q = questionList.get(currentQuestionIndex);

        setHeading(quizTitle, currentQuestionIndex + 1, questionList.size());
        tvQuestion.setText(safe(q.getQuestion()));
        btnOption1.setText(safe(q.getOption1()));
        btnOption2.setText(safe(q.getOption2()));
        btnOption3.setText(safe(q.getOption3()));
        btnOption4.setText(safe(q.getOption4()));

        resetButtonColors();
        selectedAnswer = "";
        enableOptions();
    }

    // New: set heading using Spannable: title (bold) + small progress line
    private void setHeading(String title, int current, int total) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        String t = title == null || title.isEmpty() ? "Quiz" : title;
        sb.append(t);
        int titleLen = sb.length();

        // append newline + progress
        sb.append("\n");
        String progress = "Question " + current + " of " + total;
        sb.append(progress);

        // style title bold and slightly larger
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, titleLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new RelativeSizeSpan(1.08f), 0, titleLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // style progress smaller and lighter color
        int progStart = titleLen + 1;
        int progEnd = progStart + progress.length();
        sb.setSpan(new RelativeSizeSpan(0.85f), progStart, progEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int secondaryColor = ContextCompat.getColor(this, R.color.textSecondary);
        sb.setSpan(new ForegroundColorSpan(secondaryColor), progStart, progEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvHeading.setText(sb);
        tvHeading.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    private String safe(String s) { return s == null ? "" : s; }

    private void resetButtonColors() {
        btnOption1.setBackgroundTintList(defaultTint);
        btnOption2.setBackgroundTintList(defaultTint);
        btnOption3.setBackgroundTintList(defaultTint);
        btnOption4.setBackgroundTintList(defaultTint);
    }

    private void highlightSelected(Button b) {
        b.setBackgroundTintList(selectedTint);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOption1) {
            selectedAnswer = btnOption1.getText().toString().trim();
            highlightSelected(btnOption1);
            disableOptions();
            evaluateAnswer(btnOption1);
        } else if (v.getId() == R.id.btnOption2) {
            selectedAnswer = btnOption2.getText().toString().trim();
            highlightSelected(btnOption2);
            disableOptions();
            evaluateAnswer(btnOption2);
        } else if (v.getId() == R.id.btnOption3) {
            selectedAnswer = btnOption3.getText().toString().trim();
            highlightSelected(btnOption3);
            disableOptions();
            evaluateAnswer(btnOption3);
        } else if (v.getId() == R.id.btnOption4) {
            selectedAnswer = btnOption4.getText().toString().trim();
            highlightSelected(btnOption4);
            disableOptions();
            evaluateAnswer(btnOption4);
        } else if (v.getId() == R.id.btnEndQuiz) {
            endQuiz();
        }
    }

    private void evaluateAnswer(Button clickedButton) {
        if (questionList == null || currentQuestionIndex >= questionList.size()) {
            showError("No current question");
            return;
        }
        QuizQuestionResponseDTO q = questionList.get(currentQuestionIndex);
        String correctText = mapCorrectAnsToText(q).trim();

        boolean isCorrect = false;
        if (!correctText.isEmpty()) {
            isCorrect = selectedAnswer.equals(correctText);
        } else {
            isCorrect = selectedAnswer.equalsIgnoreCase(safe(q.getCorrectAns()));
        }

        if (isCorrect) {
            clickedButton.setBackgroundTintList(correctTint);
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            clickedButton.setBackgroundTintList(wrongTint);
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }

        // next after a short delay
        new Handler(Looper.getMainLooper()).postDelayed(this::nextQuestion, 1100);
    }

    private String mapCorrectAnsToText(QuizQuestionResponseDTO q) {
        if (q == null || q.getCorrectAns() == null) return "";
        String ca = q.getCorrectAns().trim().toUpperCase();
        switch (ca) {
            case "OPTION1": return safe(q.getOption1());
            case "OPTION2": return safe(q.getOption2());
            case "OPTION3": return safe(q.getOption3());
            case "OPTION4": return safe(q.getOption4());
            default: return q.getCorrectAns();
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) displayQuestion();
        else showResult();
    }

    private void showResult() {
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("totalQuestions", questionList == null ? 0 : questionList.size());
        startActivity(intent);
        finish();
    }

    private void endQuiz() { showResult(); }
    private void enableOptions() {
        btnOption1.setEnabled(true); btnOption2.setEnabled(true); btnOption3.setEnabled(true); btnOption4.setEnabled(true);
    }
    private void disableOptions() {
        btnOption1.setEnabled(false); btnOption2.setEnabled(false); btnOption3.setEnabled(false); btnOption4.setEnabled(false);
    }
    private void showError(String message) {
        tvQuestion.setText("Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
