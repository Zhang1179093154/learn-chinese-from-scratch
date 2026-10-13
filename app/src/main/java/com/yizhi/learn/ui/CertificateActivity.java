package com.yizhi.learn.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import com.yizhi.learn.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CertificateActivity extends Activity {

    private TextView tvCertName, tvCertCount, tvCertDate, tvCertEncourage, tvCertTitle;
    private Button btnCertBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        int count = getIntent().getIntExtra("char_count", 100);
        int nextMilestone = ((count / 100) + 1) * 100;

        // 根据识字数量评定等级称号
        String title;
        String medalEmoji;
        if (count >= 3000) {
            title = "🏆 识字大王 🏆";
            medalEmoji = "👑";
        } else if (count >= 2000) {
            title = "🌟 识字达人 🌟";
            medalEmoji = "🏅";
        } else if (count >= 1000) {
            title = "⭐ 识字能手 ⭐";
            medalEmoji = "🥇";
        } else if (count >= 500) {
            title = "🎖️ 识字新星 🎖️";
            medalEmoji = "🥈";
        } else if (count >= 300) {
            title = "🎀 识字小将 🎀";
            medalEmoji = "🥉";
        } else if (count >= 200) {
            title = "🌱 识字萌芽 🌱";
            medalEmoji = "🏆";
        } else {
            title = "🌈 识字小勇士 🌈";
            medalEmoji = "🎖️";
        }

        tvCertTitle = findViewById(R.id.tvCertTitle);
        tvCertName = findViewById(R.id.tvCertName);
        tvCertCount = findViewById(R.id.tvCertCount);
        tvCertDate = findViewById(R.id.tvCertDate);
        tvCertEncourage = findViewById(R.id.tvCertEncourage);
        btnCertBack = findViewById(R.id.btnCertBack);

        tvCertTitle.setText(medalEmoji);
        tvCertName.setText(title);
        tvCertName.setTextSize(28);
        tvCertName.setTextColor(Color.parseColor("#FF6B6B"));
        tvCertName.setTypeface(null, Typeface.BOLD);
        tvCertName.setGravity(Gravity.CENTER);

        tvCertCount.setText("🎉 恭喜你已认识 " + count + " 个汉字! 🎉");
        tvCertCount.setTextSize(20);
        tvCertCount.setTextColor(Color.parseColor("#333333"));
        tvCertCount.setGravity(Gravity.CENTER);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINESE);
        tvCertDate.setText("📅 颁发日期：" + sdf.format(new Date()));
        tvCertDate.setTextSize(16);
        tvCertDate.setTextColor(Color.parseColor("#666666"));
        tvCertDate.setGravity(Gravity.CENTER);

        tvCertEncourage.setText("💪 继续加油！下一个目标是认识 " + nextMilestone + " 个字！🌟");
        tvCertEncourage.setTextSize(18);
        tvCertEncourage.setTextColor(Color.parseColor("#45B7D1"));
        tvCertEncourage.setGravity(Gravity.CENTER);

        btnCertBack.setOnClickListener(v -> finish());
    }
}