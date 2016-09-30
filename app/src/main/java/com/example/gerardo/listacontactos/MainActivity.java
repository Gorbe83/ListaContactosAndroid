package com.example.gerardo.listacontactos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)){
                requestPermissions(new String[]{
                        Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS
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

        listaContactos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                String contacto = listaContactos.getItemAtPosition(position).toString();
                StringTokenizer tokens = new StringTokenizer(contacto, ":");
                tokens.nextToken();

                String numero = tokens.nextToken();
                realizarLlamada(numero);
                return true;
            }
        });

        listaContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contacto = listaContactos.getItemAtPosition(position).toString();
                StringTokenizer tokens = new StringTokenizer(contacto, ":");
                tokens.nextToken();

                String numero = tokens.nextToken();
                Toast.makeText(getApplicationContext(), numero, Toast.LENGTH_LONG).show();
                mandarMensaje(numero);
            }
        });
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

    public void realizarLlamada(String numero) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + numero));
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
            startActivity(intent);
        else
            Toast.makeText(getApplicationContext(), "La aplicación no cuenta con permisos para realizar llamadas", Toast.LENGTH_LONG).show();

    }

    public void mandarMensaje(String numero) {
        String mensajeDefecto = "Hola";

        Uri uri = Uri.parse("smsto:" + numero);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
        sendIntent.putExtra("sms_body", mensajeDefecto);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
            startActivity(sendIntent);
        else
            Toast.makeText(getApplicationContext(), "La aplicación no cuenta con permisos para enviar mensajes", Toast.LENGTH_LONG).show();
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