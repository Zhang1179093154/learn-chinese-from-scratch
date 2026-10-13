package com.yizhi.learn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.app.Activity;
import com.yizhi.learn.data.CharacterData;
import com.yizhi.learn.ui.LearnActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onCategoryClick(View view) {
        String category = (String) view.getTag();
        String categoryName = CharacterData.getCategoryName(category);

        Toast.makeText(this, "正在进入" + categoryName + "分类...", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LearnActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}