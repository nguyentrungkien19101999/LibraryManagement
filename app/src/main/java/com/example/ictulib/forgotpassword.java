package com.example.ictulib;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
public class forgotpassword extends AppCompatActivity {
    TextView text11, text22;
    Button back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        text11 = findViewById(R.id.text1);
        text22 = findViewById(R.id.text2);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(forgotpassword.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
