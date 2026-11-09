package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_NAME = "name";
    public static final String KEY_JOB = "job";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_WEBSITE = "website";

    // null = כרטיס חדש. אם יש כאן מפתח = עורכים כרטיס קיים
    private String editKey = null;
    public EditText editName;
    public EditText editJob;
    public EditText editPhone;
    public EditText editWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editName = findViewById(R.id.etName);
        EditText editJob = findViewById(R.id.etTitle);
        EditText editPhone = findViewById(R.id.etJob);
        EditText editWebsite = findViewById(R.id.etWebsite);
        Button btnMain = findViewById(R.id.etDueDate);
        Button btnFill = findViewById(R.id.btnCancel);
        Button btnGo = findViewById(R.id.btnSave); // אודות
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ★ מסר מפורש: אנחנו מציינים בדיוק מי היעד
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(MainActivity.this, "נא להזין שם", Toast.LENGTH_SHORT).show();
                    return;                         // לא עוברים מסך
                }
                Intent intent = new Intent(MainActivity.this, CardActivity.class);
                intent.putExtra(KEY_NAME, name);
                String job = editJob.getText().toString().trim();
                intent.putExtra(KEY_JOB, job);
                String phone = editPhone.getText().toString().trim();
                intent.putExtra(KEY_PHONE, phone);
                String website = editWebsite.getText().toString().trim();
                intent.putExtra(KEY_WEBSITE, website);
                startActivity(intent);
            }
        });
        btnFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName.setText("שלו לוי");
                editJob.setText("מתכנת אנדרויד");
                editPhone.setText("054-7888321");
                editWebsite.setText("indeed.com");
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // טוענים את פריטי התפריט מתוך קובץ XML.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // מציינים שרוצים להציג את התפריט.
        return true;
    }
    // מתודה שמופעלת כאשר לוחצים על פריט בתפריט.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // האם לחצו על הוספה?
        if (item.getItemId() == R.id.action_add) {
            EditText editName = findViewById(R.id.etName);
            EditText editJob = findViewById(R.id.etTitle);
            EditText editPhone = findViewById(R.id.etJob);
            EditText editWebsite = findViewById(R.id.etWebsite);
            editName.setText("שלו לוי");
            editJob.setText("מתכנת אנדרויד");
            editPhone.setText("054-7888321");
            editWebsite.setText("indeed.com");
        }

        // האם לחצו על איפוס?
        if (item.getItemId() == R.id.action_reset) {
            EditText editName = findViewById(R.id.etName);
            EditText editJob = findViewById(R.id.etTitle);
            EditText editPhone = findViewById(R.id.etJob);
            EditText editWebsite = findViewById(R.id.etWebsite);
            editName.setText("");
            editJob.setText("");
            editPhone.setText("");
            editWebsite.setText("");
        }

        // האם לחצו על אודות?
        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.action_list) {
            startActivity(new Intent(this, CardsListActivity.class));
            return true;
        }
        // פריט אחר: מאפשרים ל-Android לטפל בו.
        return super.onOptionsItemSelected(item);
    }
    private void saveCard() {
        String name  = editName.getText().toString().trim();
        String job   = editJob.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String site  = editWebsite.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "חובה למלא שם", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!FirebaseConfig.isConfigured()) {
            Toast.makeText(this, "חסרים ערכים ב-FirebaseConfig.java", Toast.LENGTH_LONG).show();
            return;
        }

        final Card card = new Card(name, job, phone, site);
        DatabaseReference ref = CardsDb.cards(this);

        if (editKey == null) {
            // ===== הוספה (Create) =====

            // TODO 1: בקשו מ-ref מפתח חדש וייחודי, ושמרו אותו במשתנה String בשם key

            // TODO 2: שמרו את המפתח גם בתוך העצם card

            // TODO 3: כתבו את card לצומת-הילד key, וחברו שני מאזינים:
            //         הצלחה → Toast "נשמר בענן", אחר כך clearForm(), ואחר כך openCard(card)
            //         כישלון → Toast עם "השמירה נכשלה: " + e.getMessage()

        } else {
            // בסוף onCreate, אחרי כל ה-findViewById:
            editKey = getIntent().getStringExtra("key");
            if (editKey != null) {
                // TODO 11: פתחו אותנו מ"ערוך". מלאו את ארבעת השדות בערכים שהגיעו ב-Intent,
                //          שנו את הכיתוב של btnCreate ל"שמור שינויים", ואת הכותרת ל"עריכת כרטיס"
            }
        }
    }

    private void openCard(Card c) {
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra("key", c.getKey());
        intent.putExtra("name", c.getName());
        intent.putExtra("job", c.getJob());
        intent.putExtra("phone", c.getPhone());
        intent.putExtra("site", c.getSite());
        startActivity(intent);
    }

    // מרוקן את ארבעת השדות, כדי שאפשר יהיה להקליד מיד כרטיס חדש
    private void clearForm() {
        editName.setText("");
        editJob.setText("");
        editPhone.setText("");
        editWebsite.setText("");
        editName.requestFocus();
    }
}