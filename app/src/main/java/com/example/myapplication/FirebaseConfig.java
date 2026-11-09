package com.example.myapplication;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class FirebaseConfig {

    // ↓↓↓ מעתיקים את שלושת הערכים מ-FirebaseConfig.java של פרויקט פרק 11 ↓↓↓
    public static final String API_KEY = "AIzaSyBrU-x1vbKsisYaFjFE6NbvO3FuyEZhqG4";
    public static final String APPLICATION_ID = "1:389366463217:android:36a9981f8afe3a9d8a4750";
    public static final String DATABASE_URL = "https://jkdjdgjglkklkljldgjlkdgjlk-default-rtdb.europe-west1.firebasedatabase.app/";
    // ↑↑↑ בלי רווחים, ובתוך המרכאות ↑↑↑

    // האם מילאו את שלושת הערכים?
    public static boolean isConfigured() {
        return !API_KEY.isEmpty() && !APPLICATION_ID.isEmpty() && !DATABASE_URL.isEmpty();
    }

    // מחבר את האפליקציה ל-Firebase. מספיק פעם אחת: בפעם השנייה יוצאים מיד
    public static void ensureInitialized(Context context) {
        if (!FirebaseApp.getApps(context).isEmpty()) {
            return;                                     // כבר מחובר
        }
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey(API_KEY)
                .setApplicationId(APPLICATION_ID)
                .setDatabaseUrl(DATABASE_URL)
                .build();
        FirebaseApp.initializeApp(context.getApplicationContext(), options);
    }
}
