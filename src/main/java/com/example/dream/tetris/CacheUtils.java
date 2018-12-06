package com.example.dream.tetris;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.List;
import java.util.Set;

public class CacheUtils {
    private String fileName;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @SuppressLint("CommitPrefEdits")
    CacheUtils(Context context, String fileName)//fileName是缓存的唯一标识
    {
        this.fileName = fileName;
        // 数据只能被本应用程序读写
        preferences = context.getSharedPreferences(this.fileName, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void putValue(String key, String value)
    {
        editor.putString(key, value);
        // 提交所做的修改
        editor.commit();
    }

    public void putValue(String key, int value)
    {
        editor.putInt(key, value);
        // 提交所做的修改
        editor.commit();
    }

    public void putValue(String key, List<String> value)
    {
        editor.putStringSet(key, (Set<String>) value);
        // 提交所做的修改
        editor.commit();
    }

    public void putValue(String key, boolean value)
    {
        editor.putBoolean(key, value);
        // 提交所做的修改
        editor.commit();
    }


    /**
     * 获取Cache数据里指定key对应的value。如果key不存在，则返回默认值def。
     *
     * @param key
     * @param def
     * @return
     */

    public String getValue(String key, String def)
    {
        return preferences.getString(key, def);
    }


    /**
     * 清空Cache里所有数据
     */

    public void clearCache()
    {
        editor.clear();
        // 保存修改
        editor.commit();
    }

}