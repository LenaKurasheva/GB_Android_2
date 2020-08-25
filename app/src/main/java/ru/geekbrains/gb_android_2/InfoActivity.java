package ru.geekbrains.gb_android_2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        showBackBtn();
        ImageButton sendMail = findViewById(R.id.writeToUs);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","lena.kurasheva@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WeatherApp");
                startActivity(Intent.createChooser(emailIntent, "Send email"));
            }
        });
    }
    // Показывает стрелку назад на панели действий
    private void showBackBtn() {
        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException exc) {
            exc.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}