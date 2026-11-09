package com.example.myapplication;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CardActivity extends AppCompatActivity {
    public static final String KEY_NAME = "name";
    public static final String KEY_JOB = "job";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_WEBSITE = "website";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Intent intent = getIntent();          // המעטפה שהגיעה
        String name  = intent.getStringExtra(KEY_NAME);
        String job = intent.getStringExtra(KEY_JOB);
        String phone = intent.getStringExtra(KEY_PHONE);
        final String website = intent.getStringExtra(KEY_WEBSITE); // התוכנית בוכה עם זה לא final
        TextView tvName = findViewById(R.id.title);
        tvName.setText(name);
        TextView tvJob = findViewById(R.id.tvJob);
        tvJob.setHint(job);
        TextView tvOther = findViewById(R.id.tvOther);
        tvOther.setText("טלפון: " + phone + "\n" + website);
        Button btnCall = findViewById(R.id.btnCall);
        Button btnWeb = findViewById(R.id.btnWeb);
        Button btnShare = findViewById(R.id.btnShare);
        Button btnBack = findViewById(R.id.btnBack);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if (phone == null)
                    {
                        Toast.makeText(CardActivity.this, "בבקשה תכניס מספר טלפון.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if (phone.isBlank())
                        {
                            Toast.makeText(CardActivity.this, "בבקשה תכניס מספר טלפון.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                            startActivity(dial);
                        }
                    }
                }
                catch (Exception ActivityNotFoundException)
                {
                    Toast.makeText(CardActivity.this, "אין אפליקציה מתאימה.", Toast.LENGTH_SHORT).show();
                    return;                         // לא עוברים מסך
                }
            }
        });
        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    if (website != null) {
                        if (website.isBlank())
                        {
                            Toast.makeText(CardActivity.this, "בבקשה תכניס אתר.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if (!website.startsWith("http")) {
                                Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + website));
                                startActivity(web);
                            } else {
                                Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                                startActivity(web);
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(CardActivity.this, "בבקשה תכניס אתר.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ActivityNotFoundException)
                {
                    Toast.makeText(CardActivity.this, "אין אפליקציה מתאימה.", Toast.LENGTH_SHORT).show();
                    return;                         // לא עוברים מסך
                }
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, name + "\n" + job + "\n" + phone + "\n" + website);
                    startActivity(Intent.createChooser(share, "שתף כרטיס עם…"));
                }
                catch (Exception ActivityNotFoundException)
                {
                    Toast.makeText(CardActivity.this, "אין אפליקציה מתאימה.", Toast.LENGTH_SHORT).show();
                    return;                         // לא עוברים מסך
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // ★ סוגר את המסך הזה וחוזר לקודם — בדיוק כמו כפתור Back
            }
        });
    }
}