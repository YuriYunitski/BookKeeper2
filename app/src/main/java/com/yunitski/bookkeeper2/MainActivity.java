package com.yunitski.bookkeeper2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Element> elements;
    FloatingActionButton fab;
    String mValue;
    String mDate;
    String mTotalValue;
    static boolean outcome, income;
    String inputValue;
    EditText inpValueET;
    RadioButton radioButtonOut, radioButtonIn;
    RadioGroup radioGroup;
    TextView balance, tvValue, tvTotalValue, tvDate;
    DBHelper dbHelper;
    ConstraintLayout constraintLayout;
    ElementAdapter adapter;
    RecyclerView recyclerView;
    ArrayAdapter arrayAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this);

        recyclerView = findViewById(R.id.recycler_list);
        balance = findViewById(R.id.balance);
        dbHelper = new DBHelper(this);
        loadBalance();
        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveBalance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveBalance();
    }

    @Override
    public void onClick(View v) {
        launchDialogAdd();

    }
    private void launchDialogAdd(){
        loadBalance();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Добавить операцию");
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.add_dialog, null);
        builder.setView(view);
        inpValueET = view.findViewById(R.id.input_value);
        radioButtonIn = view.findViewById(R.id.income);
        radioButtonOut = view.findViewById(R.id.outcome);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.income:
                        income = true;
                        outcome = false;
                        break;
                    case R.id.outcome:
                        income = false;
                        outcome = true;
                        break;
                }
            }

        });
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mValue = "Баланс: " + inpValueET.getText().toString();
                if (income){
                    int i = Integer.parseInt(inpValueET.getText().toString());
                    int bal = Integer.parseInt(balance.getText().toString());
                    int k = bal + i;
                    balance.setText("" + k);
//                    elements.add(new Element(""+inpValueET.getText().toString(), ""+balance.getText().toString(), "" + dateC()));
//                    sharedPreferences = getPreferences(MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("b", balance.getText().toString());
//                    editor.apply();
                    ContentValues cv = new ContentValues();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    cv.put(InputData.TaskEntry.VALUE, i);
                    cv.put(InputData.TaskEntry.TOTAL_VALUE, bal);
                    cv.put(InputData.TaskEntry.DATE, dateC());
                    db.insert(InputData.TaskEntry.TABLE, null, cv);
                    db.close();
                    saveBalance();
                    updateUI();
                } else {
                    int i = Integer.parseInt(inpValueET.getText().toString());
                    int bal = Integer.parseInt(balance.getText().toString());
                    int k = bal - i;
                    balance.setText("" + k);
//                    sharedPreferences = getPreferences(MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("b", balance.getText().toString());
//                    editor.apply();
                    ContentValues cv = new ContentValues();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    cv.put(InputData.TaskEntry.VALUE, i);
                    cv.put(InputData.TaskEntry.TOTAL_VALUE, bal);
                    cv.put(InputData.TaskEntry.DATE, dateC());
                    db.insert(InputData.TaskEntry.TABLE, null, cv);
                    db.close();
                    saveBalance();
                    updateUI();
                }

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        updateUI();
        builder.create().show();
    }
    public String dateC(){
        Calendar c = new GregorianCalendar();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        return d + "." + m + "." + y;
    }
    private void updateUI(){
        elements = new ArrayList<Element>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(InputData.TaskEntry.TABLE, new String[]{InputData.TaskEntry._ID, InputData.TaskEntry.VALUE, InputData.TaskEntry.TOTAL_VALUE, InputData.TaskEntry.DATE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(InputData.TaskEntry.DATE);
            int idx1 = cursor.getColumnIndex(InputData.TaskEntry.TOTAL_VALUE);
            int idx2 = cursor.getColumnIndex(InputData.TaskEntry.VALUE);
            elements.add(0, new Element("" + cursor.getString(idx2), "" + cursor.getString(idx1),  "" + cursor.getString(idx)));
        }
        if (adapter == null) {
            adapter = new ElementAdapter(getLayoutInflater(), elements);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(elements);
            adapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
    }
    private void saveBalance(){
                    sharedPreferences = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("b", balance.getText().toString());
                    editor.apply();
    }
    private void loadBalance(){
        sharedPreferences = getPreferences(MODE_PRIVATE);
        String bb =sharedPreferences.getString("b", "0");
        balance.setText(bb);
    }
}