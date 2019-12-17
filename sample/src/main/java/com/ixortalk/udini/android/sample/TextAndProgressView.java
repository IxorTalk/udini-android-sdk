package com.ixortalk.udini.android.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Copyright © 2019 ixor. All rights reserved.
 *
 * @author <a href="mailto:wouter.zoons@ixor.be">Wouter</a> on 2019-09-06.
 */
@EViewGroup(R.layout.text_and_progress)
public class TextAndProgressView extends FrameLayout {

    @ViewById TextView textView;
    @ViewById ProgressBar progressBar;

    public TextAndProgressView(@NonNull Context context) {
        super(context);
    }

    public TextAndProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextAndProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
