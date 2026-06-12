package com.royal.qna.model;

public class QuizQuestionRequestDTO {
    private long quizId;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctAns; // OPTION1 | OPTION2 | OPTION3 | OPTION4

    // Constructors
    public QuizQuestionRequestDTO() {}

    public QuizQuestionRequestDTO(long quizId, String question, String option1, String option2,
                                  String option3, String option4, String correctAns) {
        this.quizId = quizId;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAns = correctAns;
    }

    // Getters & Setters
    public long getQuizId() { return quizId; }
    public void setQuizId(long quizId) { this.quizId = quizId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getOption1() { return option1; }
    public void setOption1(String option1) { this.option1 = option1; }

    public String getOption2() { return option2; }
    public void setOption2(String option2) { this.option2 = option2; }

    public String getOption3() { return option3; }
    public void setOption3(String option3) { this.option3 = option3; }

    public String getOption4() { return option4; }
    public void setOption4(String option4) { this.option4 = option4; }

    public String getCorrectAns() { return correctAns; }
    public void setCorrectAns(String correctAns) { this.correctAns = correctAns; }
}
