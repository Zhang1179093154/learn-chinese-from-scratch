package com.yizhi.learn.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import com.yizhi.learn.R;
import com.yizhi.learn.data.CharacterData;
import com.yizhi.learn.data.ChineseCharacter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LearnActivity extends Activity {

    private TextView tvCategoryTitle, tvProgress, tvEmoji, tvCharacter, tvPinyin, tvMeaning;
    private Button btnPrev, btnNext, btnQuiz, btnWrite, btnSpeak;
    private List<ChineseCharacter> characters;
    private int currentIndex = 0;
    private String category;
    private TextToSpeech tts;
    private SharedPreferences prefs;
    private Set<String> learnedChars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        category = getIntent().getStringExtra("category");
        characters = CharacterData.getByCategory(category);

        prefs = getSharedPreferences("learn_progress", MODE_PRIVATE);
        learnedChars = new HashSet<>(prefs.getStringSet("learned_chars", new HashSet<>()));

        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        tvProgress = findViewById(R.id.tvProgress);
        tvEmoji = findViewById(R.id.tvEmoji);
        tvCharacter = findViewById(R.id.tvCharacter);
        tvPinyin = findViewById(R.id.tvPinyin);
        tvMeaning = findViewById(R.id.tvMeaning);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnQuiz = findViewById(R.id.btnQuiz);
        btnWrite = findViewById(R.id.btnWrite);
        btnSpeak = findViewById(R.id.btnSpeak);

        tvCategoryTitle.setText(CharacterData.getCategoryName(category));

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.CHINESE);
                setFemaleVoice();
            }
        });

        btnSpeak.setOnClickListener(v -> {
            if (tts != null && !characters.isEmpty()) {
                ChineseCharacter ch = characters.get(currentIndex);
                // 先朗读单字，再朗读词组，帮助小朋友理解
                String text = ch.getCharacter() + "，" + ch.getMeaning();
                // Use child-friendly pitch and rate settings
                tts.setSpeechRate(0.78f);
                tts.setPitch(1.35f);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "speak_" + currentIndex);
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showCharacter();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < characters.size() - 1) {
                currentIndex++;
                showCharacter();
            }
        });

        btnQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });

        btnWrite.setOnClickListener(v -> {
            Intent intent = new Intent(this, WriteActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("index", currentIndex);
            startActivity(intent);
        });

        showCharacter();
    }

    private void setFemaleVoice() {
        if (tts == null) return;
        try {
            Set<Voice> voices = tts.getVoices();
            if (voices == null || voices.isEmpty()) {
                // No custom voices available, use default with female-like pitch
                tts.setPitch(1.4f);
                tts.setSpeechRate(0.8f);
                return;
            }

            // Priority 1: Professional children's broadcasting female neural voices
            // Xiaoxiao is the best Chinese children's voice - young, warm, cheerful
            String[] childrenVoices = {
                "xiaoxiao", "xiaoxiaoneural", "zh-cn-xiaoxiao", "zh-cn-xiaoxiaoneural",
                "xiaoyi", "xiaoyineural", "zh-cn-xiaoyi", "zh-cn-xiaoyineural",
                "xiao yi", "xiao xiao"
            };

            // Priority 2: Other female Chinese voices suitable for children
            String[] femaleVoices = {
                "xiaochen", "xiaochenneural", "zh-cn-xiaochen",
                "xiaohan", "xiaohan neural", "zh-cn-xiaohan",
                "xiaorui", "xiaoruineural", "zh-cn-xiaorui",
                "xiaoyou", "zh-cn-xiaoyou",
                "female", "yue", "ling", "mei", "fang",
                "yueyu", "yue yue", "xia", "wenwen"
            };

            Voice selectedVoice = null;
            Voice fallbackChinese = null;
            Voice fallbackAny = null;

            for (Voice voice : voices) {
                String name = voice.getName().toLowerCase();
                Locale vLocale = voice.getLocale();

                // Check if this is a Chinese voice
                boolean isChinese = (vLocale != null && (vLocale.equals(Locale.CHINESE)
                    || vLocale.getLanguage().equals("zh")
                    || vLocale.toString().startsWith("zh")));

                // Track fallbacks
                if (isChinese && fallbackChinese == null) {
                    fallbackChinese = voice;
                }
                if (fallbackAny == null) {
                    fallbackAny = voice;
                }

                // Skip voices with "male" in the name
                if (name.contains("male") || name.contains("yunxi")
                    || name.contains("yunjian") || name.contains("yunyang")) {
                    continue;
                }

                // Priority 1: Children's broadcasting voice
                for (String cv : childrenVoices) {
                    if (name.contains(cv)) {
                        selectedVoice = voice;
                        break;
                    }
                }
                if (selectedVoice != null) break;

                // Priority 2: Other female Chinese voices
                if (isChinese) {
                    for (String fv : femaleVoices) {
                        if (name.contains(fv)) {
                            selectedVoice = voice;
                            break;
                        }
                    }
                    if (selectedVoice != null) break;
                }
            }

            // Apply the best available voice
            if (selectedVoice != null) {
                tts.setVoice(selectedVoice);
            } else if (fallbackChinese != null) {
                tts.setVoice(fallbackChinese);
            } else if (fallbackAny != null) {
                tts.setVoice(fallbackAny);
            }

            // Set pitch and rate optimized for children's learning
            // Higher pitch for child-friendly tone, slower rate for clear pronunciation
            tts.setPitch(1.35f);
            tts.setSpeechRate(0.78f);

        } catch (Exception e) {
            // Fallback: use default voice with child-friendly settings
            try {
                tts.setPitch(1.35f);
                tts.setSpeechRate(0.78f);
            } catch (Exception ignored) {}
        }
    }

    private void showCharacter() {
        if (characters.isEmpty()) return;

        ChineseCharacter ch = characters.get(currentIndex);
        tvEmoji.setText(ch.getEmoji());
        tvCharacter.setText(ch.getCharacter());
        tvPinyin.setText(ch.getPinyin());
        tvMeaning.setText(ch.getMeaning());
        tvProgress.setText((currentIndex + 1) + " / " + characters.size());

        btnPrev.setEnabled(currentIndex > 0);
        btnPrev.setAlpha(currentIndex > 0 ? 1.0f : 0.5f);
        btnNext.setEnabled(currentIndex < characters.size() - 1);
        btnNext.setAlpha(currentIndex < characters.size() - 1 ? 1.0f : 0.5f);

        int oldSize = learnedChars.size();
        learnedChars.add(ch.getCharacter());
        if (learnedChars.size() > oldSize) {
            prefs.edit().putStringSet("learned_chars", learnedChars).apply();
            checkCertificate();
        }
    }

    private void checkCertificate() {
        int count = learnedChars.size();
        // 每认识100字颁发一个奖状，每个里程碑只颁发一次
        if (count > 0 && count % 100 == 0) {
            String milestone = "cert_" + count;
            boolean alreadyAwarded = prefs.getBoolean(milestone, false);
            if (!alreadyAwarded) {
                prefs.edit().putBoolean(milestone, true).apply();
                Intent intent = new Intent(this, CertificateActivity.class);
                intent.putExtra("char_count", count);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}