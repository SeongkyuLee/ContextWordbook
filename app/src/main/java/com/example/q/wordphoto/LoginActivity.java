package com.example.q.wordphoto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends WordPhotoParentActivity {
    public static final String MEMBERS_PREFS_NAME = "Members";
    SharedPreferences memberPreferences;

    private EditText mEmailView;
    private EditText mPasswordView;
    private CheckBox mAutoLoginCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        memberPreferences = getSharedPreferences(MEMBERS_PREFS_NAME, MODE_PRIVATE);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.loginEmail);
        mPasswordView = (EditText) findViewById(R.id.loginPassword);
        mAutoLoginCheckBox = (CheckBox) findViewById(R.id.loginAutoLoginCheckBox);
        Button mEmailSignInButton = (Button) findViewById(R.id.loginSignInBtn);
        Button mEmailRegisterButton = (Button) findViewById(R.id.loginRegisterBtn);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callRegister();
            }
        });
    }

    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if(password.equals(memberPreferences.getString(email,null))) {
            Toast.makeText(LoginActivity.this, "로그인에 성공 하였습니다.", Toast.LENGTH_SHORT).show();
            MainActivity.email = email;
            MainActivity.id = email.split("@")[0];
            MainActivity.logged = true;
            callQuestionActivity();

            //자동 로그인 설정
            if(mAutoLoginCheckBox.isChecked()) {
                MainActivity.autoLogged = true;
                saveMemberPreferences();
            }

            Log.d("Login info: logged", String.valueOf(MainActivity.logged));
            Log.d("Login info: auto_logged", String.valueOf(MainActivity.autoLogged));
            this.finish();
        } else if(!memberPreferences.contains(email)) {
            Toast.makeText(LoginActivity.this, "등록되지 않은 이메일입니다.", Toast.LENGTH_SHORT).show();
        } else if(!password.equals(memberPreferences.getString(email,null))) {
            Toast.makeText(LoginActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void callRegister() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void callQuestionActivity() {
        Intent questionIntent = new Intent(LoginActivity.this, QuestionActivity.class);
        startActivity(questionIntent);
    }

    private void saveMemberPreferences() {
        SharedPreferences.Editor editor = memberPreferences.edit();
        editor.putString("email", MainActivity.email);
        editor.putString("id", MainActivity.id);
        editor.putBoolean("logged",MainActivity.logged);
        editor.putBoolean("autoLogged",MainActivity.autoLogged);
        editor.apply();
    }
}

