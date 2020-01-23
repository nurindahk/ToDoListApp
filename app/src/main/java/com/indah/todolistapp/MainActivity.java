package com.indah.todolistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> data;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private static final String TAG = "MainActivity";
    private FloatingActionButton fab;
    private EditText edt_todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initial();

        //SharedPreferences mSetting = getSharedPreferences("todo", Context.MODE_PRIVATE);
        //String cookie = mSetting.getString("item", null);

        //edt_todo.setText(cookie);


        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //hapus item dari arraylist data
                //data.remove(position);
                deleteTask(position);
                //suruh adapter untuk menotify ke list view kalo data terubah
                //itemsAdapter.notifyDataSetChanged();

                return false;
            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Floating Action Button Berhasil dibuat", Toast.LENGTH_SHORT).show();
                addtask();
            }
        });
        data = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, data);
        lvItems.setAdapter(itemsAdapter);
    }

    private void initial() {
        lvItems = findViewById(R.id.lvToDoList);
        fab = findViewById(R.id.fab1);
    }

    public void addtask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ingin Menambah Catatan ? ");
        builder.setTitle("Add New");
        edt_todo = new EditText(this);
        builder.setView(edt_todo);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                int size = data.size();
                int new_key = size;
                data.add(edt_todo.getText().toString());
                addToSh(new_key, String.valueOf(edt_todo));
                itemsAdapter.notifyDataSetChanged();
                String text = String.valueOf(new_key);
                //String text = String.valueOf(size)+ ":" + String.valueOf(edt_todo);
                Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
            }

            private void addToSh(int key, String item) {
                SharedPreferences sh = getSharedPreferences("todo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sh.edit();
                String k = String.valueOf(key);
                editor.putString(k,item);
                editor.apply();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void deleteTask(int position){
        final int index = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ingin Menghapus catatan? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                data.remove(index);
                itemsAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("cancel", null);
        builder.create().show();
    }

    private void ShowDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Konfirmasi Keluar")
                .setMessage("Anda Yakin Ingin Keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false);
        alert.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ShowDialog();

        }
        return true;
    }
}