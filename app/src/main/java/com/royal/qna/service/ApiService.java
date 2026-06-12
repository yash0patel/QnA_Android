package com.royal.qna.service;

import com.royal.qna.model.LoginRequestModel;
import com.royal.qna.model.LoginResponseModel;
import com.royal.qna.model.UserRequestModel;
import com.royal.qna.model.QuizRequestDTO;
import com.royal.qna.model.QuizResponseDTO;
import com.royal.qna.model.QuizQuestionRequestDTO;
import com.royal.qna.model.QuizQuestionResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ----------------- AUTH / USER -----------------

    @Headers("Content-Type: application/json")
    @POST("users")
    Call<Void> createUser(@Body UserRequestModel userRequestModel);

    @Headers("Content-Type: application/json")
    @POST("login")
    Call<LoginResponseModel> loginUser(@Body LoginRequestModel loginRequest);

    // ----------------- QUIZ CONTROLLER -----------------

    // Get all quizzes
    @GET("quizzes")
    Call<List<QuizResponseDTO>> getAllQuizzes();

    // Get quiz by id
    @GET("quizzes/{id}")
    Call<QuizResponseDTO> getQuizById(@Path("id") long id);

    // Create a new quiz
    @Headers("Content-Type: application/json")
    @POST("quizzes")
    Call<QuizResponseDTO> createQuiz(@Body QuizRequestDTO quizRequest);

    // Update quiz by id
    @Headers("Content-Type: application/json")
    @PUT("quizzes/{id}")
    Call<QuizResponseDTO> updateQuiz(@Path("id") long id, @Body QuizRequestDTO quizRequest);

    // Delete quiz by id
    @DELETE("quizzes/{id}")
    Call<Void> deleteQuiz(@Path("id") long id);

    // Get active quizzes
    @GET("quizzes/active")
    Call<List<QuizResponseDTO>> getActiveQuizzes();


    // ----------------- QUIZ QUESTION CONTROLLER -----------------

    // Get question by id
    @GET("quizQuestions/{id}")
    Call<QuizQuestionResponseDTO> getQuestionById(@Path("id") long id);

    // Create a new quiz question
    @Headers("Content-Type: application/json")
    @POST("quizQuestions")
    Call<QuizQuestionResponseDTO> createQuestion(@Body QuizQuestionRequestDTO questionRequest);

    // Update quiz question by id
    @Headers("Content-Type: application/json")
    @PUT("quizQuestions/{id}")
    Call<QuizQuestionResponseDTO> updateQuestion(@Path("id") long id, @Body QuizQuestionRequestDTO questionRequest);

    // Delete quiz question
    @DELETE("quizQuestions/{id}")
    Call<Void> deleteQuestion(@Path("id") long id);

    // Submit answer for a question (query param: submittedAnswer)
    @POST("quizQuestions/{id}/submitAnswer")
    Call<Boolean> submitAnswer(@Path("id") long id, @Query("submittedAnswer") String submittedAnswer);

    // Get correct answer of a question
    @GET("quizQuestions/{id}/correctAnswer")
    Call<String> getCorrectAnswer(@Path("id") long id);

    // Get all questions for a quiz
    @GET("quizQuestions/quiz/{quizId}")
    Call<List<QuizQuestionResponseDTO>> getQuestionsByQuizId(@Path("quizId") long quizId);
}
