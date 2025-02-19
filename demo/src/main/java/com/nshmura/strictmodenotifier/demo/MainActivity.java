package com.nshmura.strictmodenotifier.demo;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //noinspection ConstantConditions
    findViewById(R.id.fire_error).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        StrictMode.noteSlowCall("fireSlowCall");
      }
    });
  }
}
