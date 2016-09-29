package com.example.gerardo.listacontactos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private ListView listaContactos;
    private ArrayAdapter<String> adapter;
    private List<String> contactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Verificar si la versión de Android es superior a Android M para solicitar al usuario permisos de lectura de contactos
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_CONTACTS
                }, READ_CONTACTS_PERMISSIONS_REQUEST);
            } else
                imprimirContactos();
        } else
            imprimirContactos();
    }

    private void imprimirContactos() {
        listaContactos = (ListView) findViewById(R.id.lvwContactos);
        contactos = obtenerContactos();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactos);

        listaContactos.setAdapter(adapter);
    }

    private List<String> obtenerContactos() {
        List<String> contactos = new ArrayList<>();

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);

        String nombreContacto, telefonoContacto;

        if (cursor != null) {
            while (cursor.moveToNext()) {

                nombreContacto = cursor
                        .getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                telefonoContacto = cursor
                        .getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contactos.add(nombreContacto + ":" + telefonoContacto);

            }
        }
        cursor.close();

        return contactos;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case READ_CONTACTS_PERMISSIONS_REQUEST:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imprimirContactos();

                    Toast.makeText(MainActivity.this, "Permisos actualizados", Toast.LENGTH_SHORT).show();
                }
        }
    }
}