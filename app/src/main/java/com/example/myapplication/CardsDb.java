package com.example.myapplication;
import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CardsDb {

    // מחזיר מצביע לצומת "cards" בעץ. כל המסכים לוקחים אותו מכאן
    public static DatabaseReference cards(Context context) {
        FirebaseConfig.ensureInitialized(context);       // מחברים ל-Firebase (רק בפעם הראשונה)
        return FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL)
                .getReference("cards");
    }
}
