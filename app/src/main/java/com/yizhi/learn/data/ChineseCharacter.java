package com.yizhi.learn.data;

public class ChineseCharacter {
    private String character;
    private String pinyin;
    private String meaning;
    private String emoji;
    private String category;

    public ChineseCharacter(String character, String pinyin, String meaning, String emoji, String category) {
        this.character = character;
        this.pinyin = pinyin;
        this.meaning = meaning;
        this.emoji = emoji;
        this.category = category;
    }

    public String getCharacter() { return character; }
    public String getPinyin() { return pinyin; }
    public String getMeaning() { return meaning; }
    public String getEmoji() { return emoji; }
    public String getCategory() { return category; }
}