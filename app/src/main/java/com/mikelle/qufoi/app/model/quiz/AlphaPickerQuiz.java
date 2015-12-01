package com.mikelle.qufoi.app.model.quiz;

import android.annotation.SuppressLint;
import android.os.Parcel;

@SuppressLint("ParcelCreator")
public final class AlphaPickerQuiz extends Quiz<String> {

    public AlphaPickerQuiz(String question, String answer, boolean solved) {
        super(question, answer, solved);
    }

    @SuppressWarnings("unused")
    public AlphaPickerQuiz(Parcel in) {
        super(in);
        setAnswer(in.readString());
    }

    @Override
    public QuizType getType() {
        return QuizType.ALPHA_PICKER;
    }

    @Override
    public String getStringAnswer() {
        return getAnswer();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getAnswer());
    }
}
