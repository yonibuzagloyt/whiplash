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
import com.google.firebase.database.FirebaseDatabase;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_NAME = "name";
    public static final String KEY_JOB = "job";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_WEBSITE = "website";
    public  static final String CHANNEL_ID   = "cards_channel";   // מזהה הערוץ. פנימי, המשתמש לא רואה אותו
    private static final int    PERM_REQUEST = 1001;              // מזהה בקשת ההרשאה
    // null = כרטיס חדש. אם יש כאן מפתח = עורכים כרטיס קיים
    private String editKey = null;
    public EditText editName;
    public EditText editJob;
    public EditText editPhone;
    public EditText editWebsite;
    public Button btnMain;
    public Random random = new Random();
    public int RequestCode;
    int NOTIF_ID;
    // שלב 1: יוצרת את ערוץ ההתראות. קוראים לה פעם אחת, בפתיחת האפליקציה
    private void createNotificationChannel() {
        // ערוצים קיימים רק מאנדרואיד 8. במכשיר ישן יותר אין מה ליצור
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,                                 // מזהה פנימי

                    getString(R.string.about_title),                             // השם שהמשתמש רואה בהגדרות
                    NotificationManager.IMPORTANCE_DEFAULT);     // רמת חשיבות

            channel.setDescription("התראות של אפליקציית ההדגמה");

            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);   // יצירה חוזרת אינה מזיקה
        }
    }
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {   // API 33


            boolean granted = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;


            if (!granted) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }
    // שלב 3: שולחת התראה על כרטיס שנשמר זה עתה בענן
    private void notifyCardSaved(Card c) {
        RequestCode = random.nextInt(100);
        NOTIF_ID = random.nextInt(100);
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra("key", c.getKey());
        intent.putExtra("name", c.getName());
        intent.putExtra("job", c.getJob());
        intent.putExtra("phone", c.getPhone());
        intent.putExtra("website", c.getSite());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // ② PendingIntent — הרשאה למערכת להפעיל את ה-Intent הזה בשמנו

        PendingIntent pi = PendingIntent.getActivity(

                this,                       // הקשר

                RequestCode,                          // requestCode — מבדיל בין PendingIntent-ים
                intent,                     // ה-Intent שיופעל
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)   // חובה!
                        .setContentTitle(getString(R.string.success_firebase))
                        .setContentText(c.getName() +" "+ getString(R.string.card_s))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("זו התראה מלאה. לחיצה עליה מפעילה PendingIntent "
                                        + "שפותח את DetailsActivity ומעביר אליו נתונים."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)   // לתאימות לפני API 26
                        .setContentIntent(pi)
                        .setAutoCancel(true);      // ההתראה נעלמת אחרי הלחיצה

        // בלי הרשאה ההתראה לא תוצג, ובלי שום שגיאה. לכן בודקים ומודיעים בעצמנו
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "אין הרשאה להצגת התראות", Toast.LENGTH_LONG).show();
            return;
        }
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {

            nm.notify(NOTIF_ID, builder.build());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editName = findViewById(R.id.etName);
        editJob = findViewById(R.id.etTitle);
        editPhone = findViewById(R.id.etJob);
        editWebsite = findViewById(R.id.etWebsite);
        btnMain = findViewById(R.id.etDueDate);
//        Button btnFill = findViewById(R.id.btnCancel);
//        Button btnGo = findViewById(R.id.btnSave); // אודות
        // בסוף onCreate, אחרי כל ה-findViewById:
        editKey = getIntent().getStringExtra("key");
        if (editKey != null)
        {
            editName.setText(getIntent().getStringExtra("name"));
            editJob.setText(getIntent().getStringExtra("job"));
            editWebsite.setText(getIntent().getStringExtra("website"));
            editPhone.setText(getIntent().getStringExtra("phone"));
            btnMain.setText(R.string.save_changes);
            setTitle(getString(R.string.edit_card));
        }
//        btnGo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // ★ מסר מפורש: אנחנו מציינים בדיוק מי היעד
//                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
//                startActivity(intent);
//            }
//        });
        // שתי שורות חדשות, בסוף המתודה
        createNotificationChannel();     // שלב 1: הערוץ נוצר
        askNotificationPermission();     // שלב 2: הדיאלוג קופץ
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String name = editName.getText().toString().trim();
//                if (name.isEmpty()) {
//                    Toast.makeText(MainActivity.this, "נא להזין שם", Toast.LENGTH_SHORT).show();
//                    return;                         // לא עוברים מסך
//                }
//                Intent intent = new Intent(MainActivity.this, CardActivity.class);
//                intent.putExtra(KEY_NAME, name);
//                String job = editJob.getText().toString().trim();
//                intent.putExtra(KEY_JOB, job);
//                String phone = editPhone.getText().toString().trim();
//                intent.putExtra(KEY_PHONE, phone);
//                String website = editWebsite.getText().toString().trim();
//                intent.putExtra(KEY_WEBSITE, website);
//                startActivity(intent);
                saveCard();
            }
        });
//        btnFill.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editName.setText("שלו לוי");
//                editJob.setText("מתכנת אנדרויד");
//                editPhone.setText("054-7888321");
//                editWebsite.setText("indeed.com");
//            }
//        });
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
            editName.setText(R.string.name_example2);
            editJob.setText(R.string.role_example);
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
            Toast.makeText(this, R.string.must_fill_name, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!FirebaseConfig.isConfigured()) {
            Toast.makeText(this, getString(R.string.firebaseconfig_java), Toast.LENGTH_LONG).show();
            return;
        }

        final Card card = new Card(name, job, phone, site);
        DatabaseReference ref = CardsDb.cards(this);

        if (editKey == null) {
            String key = ref.push().getKey();
            card.setKey(key);
            ref.child(key).setValue(card).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, getString(R.string.success_firebase), Toast.LENGTH_SHORT).show();
                            clearForm();
                            notifyCardSaved(card);
                            openCard(card);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, getString(R.string.error_firebase), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            ref.child(editKey).setValue(card)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, getString(R.string.success_firebase), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, getString(R.string.error_firebase), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }

    private void openCard(Card c) {
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra("key", c.getKey());
        intent.putExtra("name", c.getName());
        intent.putExtra("job", c.getJob());
        intent.putExtra("phone", c.getPhone());
        intent.putExtra("website", c.getSite());
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