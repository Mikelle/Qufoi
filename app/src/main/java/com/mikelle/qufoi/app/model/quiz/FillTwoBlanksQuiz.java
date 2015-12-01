package com.mikelle.qufoi.app.model.quiz;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.mikelle.qufoi.app.helper.AnswerHelper;

import java.util.Arrays;

@SuppressLint("ParcelCreator")
public final class FillTwoBlanksQuiz extends Quiz<String[]> {

    public FillTwoBlanksQuiz(String question, String[] answer, boolean solved) {
        super(question, answer, solved);
    }

    @SuppressWarnings("unused")
    public FillTwoBlanksQuiz(Parcel in) {
        super(in);
        String answer[] = in.createStringArray();
        setAnswer(answer);
    }

    @Override
    public QuizType getType() {
        return QuizType.FILL_TWO_BLANKS;
    }

    @Override
    public String getStringAnswer() {
        return AnswerHelper.getAnswer(getAnswer());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeStringArray(getAnswer());
    }

    @Override
    public boolean isAnswerCorrect(String[] answer) {
        String[] correctAnswers = getAnswer();
        for (int i = 0; i < answer.length; i++) {
            if (!answer[i].equalsIgnoreCase(correctAnswers[i])) {
                return false;
            }
        }
        return answer.length == correctAnswers.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FillTwoBlanksQuiz)) {
            return false;
        }

        FillTwoBlanksQuiz quiz = (FillTwoBlanksQuiz) o;
        final String[] answer = getAnswer();
        final String question = getQuestion();
        if (answer != null ? !Arrays.equals(answer, quiz.getAnswer()) : quiz.getAnswer() != null) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (!question.equals(quiz.getQuestion())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(getAnswer());
        return result;
    }

}
