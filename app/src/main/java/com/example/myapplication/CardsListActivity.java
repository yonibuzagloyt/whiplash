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

import androidx.activity.result.contract.ActivityResultContracts;
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
        setTitle(getString(R.string.my_cards));

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
            tvCount.setText(R.string.firebaseconfig_java);
            return;
        }
        ref = CardsDb.cards(this);

        // ===== קריאה (Read) =====
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cards.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Card c = child.getValue(Card.class);
                    if (c != null) {
                        c.setKey(child.getKey());
                        cards.add(c);
                    }
                }
                adapter.notifyDataSetChanged();
                if (cards.size() > 0) {
                    tvCount.setText(getString(R.string.cards_in_the_cloud)+ cards.size());
                }
                else {
                    tvCount.setText(R.string.no_cards);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CardsListActivity.this,
                        getString(R.string.failed_to_read) + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        ref.addValueEventListener(listener);

        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Card c = cards.get(position);
                Intent intent = new Intent(CardsListActivity.this, CardActivity.class);
                intent.putExtra("key", c.getKey());
                intent.putExtra("name", c.getName());
                intent.putExtra("job", c.getJob());
                intent.putExtra("phone", c.getPhone());
                intent.putExtra("website", c.getSite());
                startActivity(intent);
            }
        });
        lvCards.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                askDelete(cards.get(position));
                return true;
            }
        });
    }

    // ===== מחיקה (Delete) =====
    private void askDelete(final Card c) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.card_deletion)
                .setMessage(getString(R.string.card_del) + c.getName() + "?")
                .setPositiveButton(R.string.del_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ref.child(c.getKey()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(CardsListActivity.this, R.string.successfully_deleted, Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ref != null && listener != null) {
            ref.removeEventListener(listener);
        }
    }
}
