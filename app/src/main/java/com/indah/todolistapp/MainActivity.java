package com.indah.todolistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvTodos;
    FloatingActionButton fabAdd;
    EditText edtTodo;

    // CATATAN!!!! yang bertanda // adalah catatan dan langkah-langkah

    //1. Siapkan Data
    //String[] data = {"Nonton drakor","Sleep","Nonton drakor","Eat",}; // diganti menjadi ArrayList berikut :
    ArrayList<String> data = new ArrayList<String>();

    //3. Buat Adapter untuk List View
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //1. Siapkan data

        // 9.2 Panggil method loadDataFromPreferences() agar data dari SP dimasukkan ke array list saat activity pertama dipanggil
        loadDataFromPreferences();

        //2. Buat List View
        lvTodos = findViewById(R.id.lvToDoList); // define list view



        // 3. Buat Adapter dan masukkan parameter yg dibutuhkan. (context, layout_content,tv,data)
        //      parameter data diambil dari langkah 1.
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.todo_content,R.id.tv_todo,data);

        // 4. Set Adapter kepada List View
        lvTodos.setAdapter(arrayAdapter);

        // 5. Define FAB dan buat onClickListener nya.
        fabAdd = findViewById(R.id.fab1);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //6. Method di bawah ini dibuat sendiri di bawah
                onClickFabAdd();
            }
        });

        // 7.1 Buat onItemLongClickListener di list view untuk hapus data
        lvTodos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //panggil method deleteItem()

                showDialogOption(position);
                //10.2 Panggil method deleteFromSP untuk menghapus data dari Shared Preferences
                //deleteFromSP(position); // Sampai sini akan terjadi error karena key d SP tidak berurutan

            }
        });
        /*lvTodos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //panggil method deleteItem()

                deleteItem(position);
                //10.2 Panggil method deleteFromSP untuk menghapus data dari Shared Preferences
                //deleteFromSP(position); // Sampai sini akan terjadi error karena key d SP tidak berurutan

                return false;
            }
        });*/

        // 12.4 Buat OnItemClickListener dan panggil method showDialogEdit()
        lvTodos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Panggil method showDialogEdit()
                showDialogOption(position);
            }
        });

    }

    // 1. Siapkan Dummy Data
    private void createTodos(){
        data.add("Nonton Drakor");
        data.add("Eat");
        data.add("Sleep");
        data.add("Rebahan");
    }

    //6. Buat Method ketika FAB Add di click untuk menambahkan data
    private void onClickFabAdd(){
        //Cara pertama tambah edit text ke dialog
        //EditText edtTodo = new EditText(this);

        //Cara dua tambah edit text ke dialog
        //proses ini disebut dengan inflate layout
        View view = View.inflate(this,R.layout.dialog_add_view, null);

        //EditText ini di deklarasikan di atas di dalam class
        edtTodo = view.findViewById(R.id.edtTodo);
        edtTodo.setError("Tidak Boleh Kosong");

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Ingin menambah catatan?");
        dialog.setView(view);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 8.2 hitung size dari arraylist data untuk dijadikan calon key untuk SP :
                int newKey= data.size();

                String item = edtTodo.getText().toString();
                data.add(item); // tambah data ke object ArrayList data.
                arrayAdapter.notifyDataSetChanged(); // merefresh list view

                // 8.3 Tambahkan data ke Shared Preferences
                // Panggil method addToSP() untuk menyimpan data ke SP
                addToSP(newKey,item);

                Toast.makeText(getApplicationContext(),String.valueOf(newKey),Toast.LENGTH_LONG).show();
            }
        });
        dialog.setNegativeButton("Cancel",null);
        dialog.create();
        dialog.show();
    }


    // 7.2 Buat method delete Item untuk menghapus data dari array list dan mengupdate list view
    private void deleteItem(int position){ // beri parameter position untuk mewadahi position dari list view
        // konstanta untuk menampung data position yang di passing dari onItemLongClickListener
        final int index = position;

        //Buat alert dialog
        AlertDialog.Builder dialog  = new AlertDialog.Builder(this);
        dialog.setTitle("Ingin Menghapus Catatan?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //hapus item dari array list data berdasarkan index/position dari item di list view
                data.remove(index); // index didapat position parameter

                //11.2 Panggil method reGenerateAndSortSP()
                //reGenerateAndSortSP();
                reGenerateAndSortSP();

                //suruh adapter untuk notify  ke List View kalau data telah berubah //merefresh list view
                arrayAdapter.notifyDataSetChanged();
            }
        });
        dialog.setNegativeButton("Cancel",null);
        dialog.create().show();
    }

    //8.1 Buat method untuk input data ke Shared Preferences
    private void addToSP(int key, String item){
        // buat key untuk SP diambil dari size terakhir array list data
        String newKey = String.valueOf(key);
        SharedPreferences todosPref = getSharedPreferences("todosPref",MODE_PRIVATE);
        SharedPreferences.Editor todosPrefEditor = todosPref.edit();
        // simpan ke SP dengan key dari size terakhir array list
        todosPrefEditor.putString(newKey,item);
        todosPrefEditor.apply();

    }

    // 9.1 Load Data dari Shared Preferences
    // Buat method loadPreferences
    private void loadDataFromPreferences(){
        SharedPreferences todosPref = getSharedPreferences("todosPref",MODE_PRIVATE);
        //cek dalam SP ada data atau tidak
        if(todosPref.getAll().size() > 0) { //2
            //masukkan semua data di SP ke array list data
            for (int i = 0; i < todosPref.getAll().size(); i++) { // i < 2
                String key = String.valueOf(i);// i = 1
                String item = todosPref.getString(key, null);
                data.add(item);
            }
        }

    }

    //10.1 Menghapus data dari Shared Preferences
    // Buat method hapus data dari shared preferences
    private void deleteFromSP(int position){
        String key = String.valueOf(position);
        SharedPreferences todosPref = getSharedPreferences("todosPref",MODE_PRIVATE);
        SharedPreferences.Editor todosPrefEditor = todosPref.edit();
        todosPrefEditor.remove(key);
        todosPrefEditor.apply();
    }


    //11.1 Fix Error di langkah 10 untuk mengurutkan kembali key dan value di dalam Shared Preference
    private void reGenerateAndSortSP(){
        SharedPreferences todosPref = getSharedPreferences("todosPref",MODE_PRIVATE);
        SharedPreferences.Editor todosPrefEditor = todosPref.edit();
        // hapus semua data di Shared Preference
        todosPrefEditor.clear();
        todosPrefEditor.apply();

        // isi ulang Shared Preference dengan data dari array list yang sudah otomatis terurut
        for(int i = 0; i < data.size();i++){
            String key = String.valueOf(i);
            todosPrefEditor.putString(key,data.get(i));
        }
        todosPrefEditor.apply();

    }

    // 12.1 Membuat fitur Edit Item
    //  Buat method untuk menampilkan AlertDialog data yang hendak diedit

    private void showDialogEdit(final int position){

        View view = View.inflate(this,R.layout.dialog_add_view, null);

        //EditText ini dideklarisikan di atas di dalam class
        edtTodo = view.findViewById(R.id.edtTodo);

        //EditText diisi dengan data dari list view yang dipilih berdasarkan parameter position
        edtTodo.setText(arrayAdapter.getItem(position)); //diambil dari adapter list view
        //edtTodo.setText(data.get(position)); //diambil dari array list : alternatif dari cara diatas ini.

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Ingin Mengubah Catatan?");
        dialog.setView(view);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //12.3 Panggil method editItem() di bawah yang telah dibuat pada langkah 12.2
                editItem(position,edtTodo.getText().toString());
            }
        });
        dialog.setNegativeButton("No",null);
        dialog.create();
        dialog.show();
    }

    // 12.2 Buat method untuk mengubah item dengan parameter postition dan text item baru.
    private void editItem(int position, String newItem){
        //set data di array dengan value baru berdasarkan index/position
        data.set(position, newItem);
        //jangan lupa Shared Preferences di generate ulang
        reGenerateAndSortSP();

        //refresh list view
        arrayAdapter.notifyDataSetChanged();

    }
    private void showDialogOption(int pos){
        final int position = pos;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("ingin mengganti catatan?");
        dialog.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                showDialogEdit(position);
            }
        });
        dialog.setNeutralButton("Batal", null);
        dialog.setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(position);
            }
        });
        dialog.create();
        dialog.show();
    }

}