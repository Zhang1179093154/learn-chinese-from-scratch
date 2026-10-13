package com.yizhi.learn.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import com.yizhi.learn.R;
import com.yizhi.learn.data.CharacterData;
import com.yizhi.learn.data.ChineseCharacter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizActivity extends Activity {

    private TextView tvQuizTitle, tvScore, tvQuestionPinyin, tvQuestionPrompt, tvFeedback;
    private LinearLayout layoutOptions;
    private Button btnNextQuestion;
    private List<ChineseCharacter> characters;
    private ChineseCharacter currentQuestion;
    private int score = 0;
    private int totalQuestions = 0;
    private boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        String category = getIntent().getStringExtra("category");
        characters = CharacterData.getByCategory(category);

        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvScore = findViewById(R.id.tvScore);
        tvQuestionPinyin = findViewById(R.id.tvQuestionPinyin);
        tvQuestionPrompt = findViewById(R.id.tvQuestionPrompt);
        tvFeedback = findViewById(R.id.tvFeedback);
        layoutOptions = findViewById(R.id.layoutOptions);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);

        tvQuizTitle.setText(CharacterData.getCategoryName(category) + " 测验");

        btnNextQuestion.setOnClickListener(v -> {
            answered = false;
            tvFeedback.setVisibility(View.GONE);
            btnNextQuestion.setVisibility(View.GONE);
            generateQuestion();
        });

        generateQuestion();
    }

    private void generateQuestion() {
        if (characters.size() < 3) return;

        layoutOptions.removeAllViews();

        Random random = new Random();
        currentQuestion = characters.get(random.nextInt(characters.size()));

        tvQuestionPinyin.setText(currentQuestion.getPinyin());
        tvQuestionPrompt.setText("下面哪个字的拼音是上面的?");

        List<ChineseCharacter> options = new ArrayList<>();
        options.add(currentQuestion);

        List<ChineseCharacter> pool = new ArrayList<>(characters);
        pool.remove(currentQuestion);
        Collections.shuffle(pool);

        for (int i = 0; i < 3 && i < pool.size(); i++) {
            options.add(pool.get(i));
        }

        Collections.shuffle(options);

        for (ChineseCharacter option : options) {
            Button btn = new Button(this);
            btn.setText(option.getCharacter());
            btn.setTextSize(36);
            btn.setTextColor(Color.WHITE);
            btn.setBackgroundColor(Color.parseColor("#45B7D1"));
            btn.setPadding(50, 24, 50, 24);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(16, 12, 16, 12);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> onOptionSelected(btn, option));
            layoutOptions.addView(btn);
        }
    }

    private void onOptionSelected(Button selectedBtn, ChineseCharacter selected) {
        if (answered) return;
        answered = true;
        totalQuestions++;

        if (selected.getCharacter().equals(currentQuestion.getCharacter())) {
            score++;
            tvFeedback.setText("太棒了! 回答正确! 🎉");
            tvFeedback.setTextColor(Color.parseColor("#4CAF50"));
            selectedBtn.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else {
            tvFeedback.setText("不对哦，正确答案是: " + currentQuestion.getCharacter() +
                               " (" + currentQuestion.getMeaning() + ")");
            tvFeedback.setTextColor(Color.parseColor("#F44336"));
            selectedBtn.setBackgroundColor(Color.parseColor("#F44336"));

            for (int i = 0; i < layoutOptions.getChildCount(); i++) {
                Button btn = (Button) layoutOptions.getChildAt(i);
                if (btn.getText().equals(currentQuestion.getCharacter())) {
                    btn.setBackgroundColor(Color.parseColor("#4CAF50"));
                }
            }
        }

        tvScore.setText("⭐ " + score + " / " + totalQuestions);
        tvFeedback.setVisibility(View.VISIBLE);
        btnNextQuestion.setVisibility(View.VISIBLE);
    }
}