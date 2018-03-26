package com.example.itachi.loginrstnk1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.w3c.dom.Text;

/**
 * Created by itachi on 22/3/18.
 */

public class afterlogin extends AppCompatActivity {

int logi =-1;
    GoogleSignInClient googleSignInClient;
  FirebaseAuth auth;
  FirebaseUser user;

    public static afterlogin initializeview() {
        return new afterlogin();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afterlogin);

        auth =FirebaseAuth.getInstance();
        user =auth.getCurrentUser();
        googleSignInClient=  getsigninclient();
      logi = getIntent().getExtras().getInt("logi");

        Button btn =(Button)findViewById(R.id.btn);
        TextView tw =(TextView)findViewById(R.id.text1);

        if(logi==3){
            if(!user.isEmailVerified()){
       tw.setText("please first verify email to continue");
      }
      else{
                tw.setText("email verified");
            }
        }




               btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               auth.signOut();
//                google
                if(logi==1){
                googleSignInClient.signOut();
                }
//                twitter
                else if(logi==2) {
                    TwitterCore.getInstance().getSessionManager().clearActiveSession();
                }
//                facebook
                else if(logi==0) {
                    LoginManager.getInstance().logOut();
                }
            }
        });

    }

    public GoogleSignInClient getsigninclient(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        return googleSignInClient;
    }

}