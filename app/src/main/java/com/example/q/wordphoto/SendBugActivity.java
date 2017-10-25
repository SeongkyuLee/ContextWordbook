package com.example.q.wordphoto;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SendBugActivity extends WordPhotoParentActivity {
    EditText subjectText;
    EditText contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_bug);
        setup();
    }

    private void setup() {
        subjectText = (EditText) findViewById(R.id.sendBugSubject);
        contentText = (EditText) findViewById(R.id.sendBugContent);
    }

    public void sendMail(View view) {
        String email = "wordPhoto@gmail.com";
        String subject = "[word photo bug report] " + subjectText.getText().toString();
        String content = contentText.getText().toString();
        String uriText = "mailto:"+ email + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(content);
        Uri uri = Uri.parse(uriText);

        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(uri);
        startActivity(Intent.createChooser(sendIntent, "Send email"));

        this.finish();
    }
}
