package templar.atakr;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShareActivity extends AppCompatActivity {

    //Firebase related variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mVideoDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);

        //TODO 'add user authentication'
        Intent intent = getIntent();
        TextView textView = (TextView) findViewById(R.id.share_text);

        if(intent != null){
            textView.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
    }
}
