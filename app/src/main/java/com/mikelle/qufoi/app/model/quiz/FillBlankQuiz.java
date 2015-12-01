package com.mikelle.qufoi.app.model.quiz;

import android.annotation.SuppressLint;
import android.os.Parcel;

@SuppressLint("ParcelCreator")
public final class FillBlankQuiz extends Quiz<String> {

    private final String mStart;
    private final String mEnd;

    public FillBlankQuiz(String question, String answer, String start, String end, boolean solved) {
        super(question, answer, solved);
        mStart = start;
        mEnd = end;
    }

    @SuppressWarnings("unused")
    public FillBlankQuiz(Parcel in) {
        super(in);
        setAnswer(in.readString());
        mStart = in.readString();
        mEnd = in.readString();
    }

    @Override
    public String getStringAnswer() {
        return getAnswer();
    }

    public String getStart() {
        return mStart;
    }

    public String getEnd() {
        return mEnd;
    }

    @Override
    public QuizType getType() {
        return QuizType.FILL_BLANK;
    }

    @Override
    public boolean isAnswerCorrect(String answer) {
        return getAnswer().equalsIgnoreCase(answer);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getAnswer());
        dest.writeString(mStart);
        dest.writeString(mEnd);
    }
}
