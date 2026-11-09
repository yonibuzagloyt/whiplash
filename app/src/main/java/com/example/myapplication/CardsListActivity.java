package com.example.myapplication;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CardsListActivity extends AppCompatActivity {

    private ListView lvCards;
    private TextView tvCount;

    private ArrayList<Card> cards = new ArrayList<>();   // הכרטיסים בזיכרון
    private ArrayAdapter<Card> adapter;                  // מחבר בין הרשימה ל-ListView

    private DatabaseReference ref;                       // הצומת cards
    private ValueEventListener listener;                 // שומרים אותו כדי להסיר בסוף

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_list);
        setTitle("הכרטיסים שלי");

        lvCards = findViewById(R.id.lvCards);
        tvCount = findViewById(R.id.tvCount);

        // ➕ כרטיס חדש: סוגרים את הרשימה וחוזרים לטופס (הוא מתחת לרשימה, והוא כבר ריק)
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cards);
        lvCards.setAdapter(adapter);

        if (!FirebaseConfig.isConfigured()) {
            tvCount.setText("חסרים ערכים ב-FirebaseConfig.java");
            return;
        }
        ref = CardsDb.cards(this);

        // ===== קריאה (Read) =====
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // TODO 4: רוקנו את הרשימה cards (למה? ראו את הרמזים)

                // TODO 5: עברו בלולאה על כל הילדים של snapshot. לכל ילד:
                //         הפכו אותו לעצם Card, שמרו בעצם את המפתח של הילד,
                //         והוסיפו את העצם לרשימה cards

                // TODO 6: אמרו למתאם שהנתונים השתנו,
                //         וכתבו ב-tvCount כמה כרטיסים יש
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CardsListActivity.this,
                        "הקריאה נכשלה: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        // TODO 7: הירשמו עם listener לשינויים בצומת ref

        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 9: קחו מהרשימה cards את הכרטיס שעליו לחצו (לפי position),
                //         בנו Intent ל-CardActivity עם 5 Extras: "key", "name", "job", "phone", "site",
                //         ופתחו את המסך
            }
        });
        lvCards.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 13: קראו ל-askDelete עם הכרטיס שבמקום position,
                //          והחזירו ערך שאומר "טיפלתי בלחיצה" (כדי שלא תיפתח גם לחיצה רגילה)
            }
        });
    }
    // ===== מחיקה (Delete) =====
    private void askDelete(final Card c) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת כרטיס")
                .setMessage("למחוק את הכרטיס של " + c.getName() + "?")
                .setPositiveButton("מחק", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 14: מחקו מהענן את הצומת של הכרטיס c (לפי המפתח שלו),
                        //          ובהצלחה הציגו Toast "נמחק".
                        //          אל תמחקו בעצמכם מהרשימה cards. מי יעשה את זה?
                    }
                })
                .setNegativeButton("ביטול", null)
                .show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO 8: אם ref ו-listener לא null, בטלו את ההרשמה של listener
    }
}
