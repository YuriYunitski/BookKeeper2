package com.yunitski.bookkeeper2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Element> elements;
    FloatingActionButton fab;
    String mValue;
    static boolean outcome, income;
    EditText inpValueET;
    RadioButton radioButtonOut, radioButtonIn;
    RadioGroup radioGroup;
    TextView balance;
    DBHelper dbHelper;
    ElementAdapter adapter;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    int res;
    SQLiteDatabase database;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_content);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#D5D5D5"));
        setTitle("События");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this);
        outcome = true;
        income = false;
        recyclerView = findViewById(R.id.recycler_list);
        balance = findViewById(R.id.balance);
        dbHelper = new DBHelper(this);
        loadBalance();
        updateUI();
        registerForContextMenu(recyclerView);
        registerForContextMenu(balance);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Очистить историю");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.deleteDatabase(InputData.DB_NAME);
        loadBalance();
        updateUI();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch (v.getId()){
            case R.id.balance:
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.balance_context_menu, menu);
                break;
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.balance_delete:
                balance.setText("" + 0);
                saveBalance();
                updateUI();
                break;
        }
        return super.onContextItemSelected(item);
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
                if (checkedId == R.id.income) {
                    income = true;
                    outcome = false;
                } else if (checkedId == R.id.outcome) {
                    income = false;
                    outcome = true;
                }
            }

        });
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mValue = "Баланс: " + inpValueET.getText().toString();
                if (income){
                    if (!inpValueET.getText().toString().isEmpty()) {
                        res = R.drawable.ic_baseline_arrow_drop_up_24;
                        int i = Integer.parseInt(inpValueET.getText().toString());
                        int bal = Integer.parseInt(balance.getText().toString());
                        int k = bal + i;
                        balance.setText("" + k);
                        ContentValues cv = new ContentValues();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        cv.put(InputData.TaskEntry.VALUE, i);
                        cv.put(InputData.TaskEntry.TOTAL_VALUE, bal);
                        cv.put(InputData.TaskEntry.DATE, dateC());
                        cv.put(InputData.TaskEntry.OPERATION, res);
                        db.insert(InputData.TaskEntry.TABLE, null, cv);
                        db.close();
                        saveBalance();
                        updateUI();
                    }
                } else if(outcome){
                    if (!inpValueET.getText().toString().isEmpty()) {
                        res = R.drawable.ic_baseline_arrow_drop_down_24;
                        int i = Integer.parseInt(inpValueET.getText().toString());
                        int bal = Integer.parseInt(balance.getText().toString());
                        int k = bal - i;
                        balance.setText("" + k);
                        ContentValues cv = new ContentValues();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        cv.put(InputData.TaskEntry.VALUE, i);
                        cv.put(InputData.TaskEntry.TOTAL_VALUE, bal);
                        cv.put(InputData.TaskEntry.DATE, dateC());
                        cv.put(InputData.TaskEntry.OPERATION, res);
                        db.insert(InputData.TaskEntry.TABLE, null, cv);
                        db.close();
                        saveBalance();
                        updateUI();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "no operation selected", Toast.LENGTH_SHORT).show();
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
        Cursor cursor = db.query(InputData.TaskEntry.TABLE, new String[]{InputData.TaskEntry._ID, InputData.TaskEntry.VALUE, InputData.TaskEntry.TOTAL_VALUE, InputData.TaskEntry.DATE, InputData.TaskEntry.OPERATION}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(InputData.TaskEntry.DATE);
            int idx1 = cursor.getColumnIndex(InputData.TaskEntry.TOTAL_VALUE);
            int idx2 = cursor.getColumnIndex(InputData.TaskEntry.VALUE);
            int idx3 = cursor.getColumnIndex(InputData.TaskEntry.OPERATION);
            elements.add(0, new Element("" + cursor.getString(idx2), "" + cursor.getString(idx1),  "" + cursor.getString(idx), cursor.getInt(idx3)));
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.IdItem1){
            Toast.makeText(getApplicationContext(), "item1", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.IdItem2){
            Toast.makeText(getApplicationContext(), "item2", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.IdItem3){
            Toast.makeText(getApplicationContext(), "item3", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}