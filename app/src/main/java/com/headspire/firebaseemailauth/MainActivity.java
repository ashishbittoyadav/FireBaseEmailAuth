package com.headspire.firebaseemailauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private static final int REQUEST_CODE =2 ;
    private SignInButton signInButton;

    //a constant for detecting the login intent result
    private static final int RC_SIGN_IN = 234;

    //Tag for the logs optional
    private static final String TAG = "simplifiedcoding";

    //creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;


    //And also a Firebase Auth object
    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null)
        {
            finish();
            //start a new activity..
            startActivity(new Intent(this,Profile.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(MainActivity.this);
        mAuth=FirebaseAuth.getInstance();
        signInButton=findViewById(R.id.googlesignin);
        signInButton.setOnClickListener(this);
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("781260974811-4ne5hp4bfb3d0mqhmj8gclekq5sbee5t.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);
    //    GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.googlesignin:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE)
        {
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                    GoogleSignInAccount account=task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                Log.e("Taggg",account.getEmail());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user=mAuth.getCurrentUser();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
