package com.example.q.wordphoto;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends WordPhotoParentActivity {
    public static final String MEMBERS_PREFS_NAME = "Members";
    SharedPreferences memberPreferences;

    EditText emailView;
    EditText passwordView;
    EditText rePasswordView;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        memberPreferences = getSharedPreferences(MEMBERS_PREFS_NAME, MODE_PRIVATE);
        setup();
    }

    protected void setup() {
        emailView = (EditText) findViewById(R.id.registerEmail);
        passwordView = (EditText) findViewById(R.id.registerPassword);
        rePasswordView = (EditText) findViewById(R.id.registerRePassword);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String rePassword = rePasswordView.getText().toString();

        if(memberPreferences.contains(email)) {
            Toast.makeText(RegisterActivity.this, "이미 등록된 메일입니다.", Toast.LENGTH_SHORT).show();
        } else if(email.contains("@") & password.equals(rePassword)) {
            Toast.makeText(RegisterActivity.this, "회원가입에 성공 했습니다.", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = memberPreferences.edit();
            editor.putString(email, password);
            editor.apply();
            this.finish();
        } else if (!email.contains("@")) {
            Toast.makeText(RegisterActivity.this, "올바른 이메일 양식이 아닙니다.", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(rePassword)) {
            Toast.makeText(RegisterActivity.this, "입력한 두 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RegisterActivity.this, "회원가입 중 문제가 생겼습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
