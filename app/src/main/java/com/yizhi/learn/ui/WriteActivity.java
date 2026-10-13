package com.yizhi.learn.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import com.yizhi.learn.R;
import com.yizhi.learn.data.CharacterData;
import com.yizhi.learn.data.ChineseCharacter;
import java.util.List;
import java.util.Random;

public class WriteActivity extends Activity {

    private TextView tvWriteTitle, tvWriteHint, tvTargetChar, tvTargetPinyin, tvTargetMeaning;
    private Button btnClear, btnNextChar;
    private DrawingView drawingView;
    private List<ChineseCharacter> characters;
    private ChineseCharacter currentChar;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        String category = getIntent().getStringExtra("category");
        characters = CharacterData.getByCategory(category);

        tvWriteTitle = findViewById(R.id.tvWriteTitle);
        tvWriteHint = findViewById(R.id.tvWriteHint);
        tvTargetChar = findViewById(R.id.tvTargetChar);
        tvTargetPinyin = findViewById(R.id.tvTargetPinyin);
        tvTargetMeaning = findViewById(R.id.tvTargetMeaning);
        btnClear = findViewById(R.id.btnClear);
        btnNextChar = findViewById(R.id.btnNextChar);
        drawingView = findViewById(R.id.drawingView);

        tvWriteTitle.setText(CharacterData.getCategoryName(category));

        btnClear.setOnClickListener(v -> drawingView.clear());

        btnNextChar.setOnClickListener(v -> {
            drawingView.clear();
            pickRandomCharacter();
        });

        pickRandomCharacter();
    }

    private void pickRandomCharacter() {
        if (characters.isEmpty()) return;
        currentChar = characters.get(random.nextInt(characters.size()));
        tvTargetChar.setText(currentChar.getCharacter());
        tvTargetPinyin.setText(currentChar.getPinyin());
        tvTargetMeaning.setText(currentChar.getMeaning());
        tvWriteHint.setText("照着写一写吧!");
    }
}