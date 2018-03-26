package com.example.itachi.loginrstnk1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.List;

/**
 * Created by itachi on 22/3/18.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ViewPager viewPager;
    MyViewPagerAdapter myViewPagerAdapter;


    TextView[] dots;
    Button skip_button, final_login_btn, final_signin_btn, initial_login_btn, initial_signin_btn, google_login_btn,
            twitter_login_btn, facebook_login_btn;
    TextView status_text, text_for_login, text_for_signup, forgot_password;
    EditText fname, lname, email_s, phone, password_s, email_l, password_l;
    LinearLayout dotsLayout, mix_btn_layout, status_lin_layout, login_layout, signup_layout, feature_page_layout;
    ImageButton upbutton;


    GoogleSignInClient gsc;
    FirebaseAuth auth;

    TwitterLoginButton twitter_original_Login_Button;


    CallbackManager callbackManager;
    String mobile, firstname, lastname;

    public static final int MAIN = 0;
    public static final int LOGIN = 1;
    public static final int SIGNUP = 2;
    int logi = -1;

    public static final int GOOGLE = 3;
    public static final int FACEBOOK = 4;
    public static final int TWITTER = 5;

    private static final int RC_SIGN_IN = 9001;


    public ProgressDialog mProgressDialog;

    SignInButton signinbtn;

    GoogleSignInClient googleSignInClient;
    FirebaseUser user;

    private static final String TAG = "PasswordlessSignIn";
    private static final String KEY_PENDING_EMAIL = "key_pending_email";
    private static String KEY_PENDING_PASSWORD = "";

    int[] imglist = {
            R.drawable.vp1,
            R.drawable.vp2,
            R.drawable.vp3,
            R.drawable.vp4
    };

    String[] textlist = {
            " dnjenvfvkfflblfbkf lk l ffb f ",
            "fnnfbfjkfjfkjnfjkvnfkvnf",
            "klfnvfkjnknbkdnkblnbdklnbklbnlkbdk",
            "kljdblkblkgmlmkgmgkmkglmgmgmglkmlmfm"
    };
    LoginButton lb;
    boolean valid;


    private String mPendingEmail;
    private String mEmailLink;
    private String mpendingpassword;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(MainActivity.this, afterlogin.class));
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        startActivity(new Intent(MainActivity.this, test1.class));


        FacebookSdk.sdkInitialize(getApplicationContext());

        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(getApplicationContext())
                .twitterAuthConfig(authConfig).build();
        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_main);
        changeStausBarColor();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutdots);
        mix_btn_layout = (LinearLayout) findViewById(R.id.log_sign_start_layout);
        status_lin_layout = (LinearLayout) findViewById(R.id.status_linear_layout);
        status_text = (TextView) findViewById(R.id.status_text_in_heading);
        login_layout = (LinearLayout) findViewById(R.id.login_details_layout);
        signup_layout = (LinearLayout) findViewById(R.id.signup_details_layout);
        initial_signin_btn = (Button) findViewById(R.id.btn_signup);
        initial_login_btn = (Button) findViewById(R.id.btn_login);
        feature_page_layout = (LinearLayout) findViewById(R.id.features_viewpager_layout);
        upbutton = (ImageButton) findViewById(R.id.upbutton_in_heading);

        //custom
        google_login_btn = (Button) findViewById(R.id.google_login_btn);
        twitter_login_btn = (Button) findViewById(R.id.twitter_login_btn);
        facebook_login_btn = (Button) findViewById(R.id.facebook_login_btn);
        //original
        lb = (LoginButton) findViewById(R.id.facebook_login_button);
        twitter_original_Login_Button = (TwitterLoginButton) findViewById(R.id.button_twitter_login);
        signinbtn = (SignInButton) findViewById(R.id.sign_in_button);
        signinbtn.setColorScheme(SignInButton.COLOR_LIGHT);

        //signup
        text_for_login = (TextView) findViewById(R.id.text_for_login);
        fname = (EditText) findViewById(R.id.fname_signup_details);
        lname = (EditText) findViewById(R.id.lname_signup_details);
        email_s = (EditText) findViewById(R.id.emailid_signup_details);
        phone = (EditText) findViewById(R.id.mobile_signup_details);
        password_s = (EditText) findViewById(R.id.password_signup_details);
        final_signin_btn = (Button) findViewById(R.id.signin_finalize_btn);

        //login
        text_for_signup = (TextView) findViewById(R.id.text_for_signup);
        final_login_btn = (Button) findViewById(R.id.login_finalize_btn);
        email_l = (EditText) findViewById(R.id.emailid_login_details);
        password_l = (EditText) findViewById(R.id.password_login_details);
        forgot_password = (TextView) findViewById(R.id.forgot_password_text);


        auth = FirebaseAuth.getInstance();

        addbottomdots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        updateUI(MAIN);

        findViewById(R.id.btn_skip_in_heading).setOnClickListener(this);
        findViewById(R.id.upbutton_in_heading).setOnClickListener(this);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_signup).setOnClickListener(this);

        findViewById(R.id.google_login_btn).setOnClickListener(this);
        findViewById(R.id.twitter_login_btn).setOnClickListener(this);
        findViewById(R.id.facebook_login_btn).setOnClickListener(this);

        findViewById(R.id.text_for_login).setOnClickListener(this);
        findViewById(R.id.signin_finalize_btn).setOnClickListener(this);

        findViewById(R.id.text_for_signup).setOnClickListener(this);
        findViewById(R.id.login_finalize_btn).setOnClickListener(this);
        findViewById(R.id.forgot_password_text).setOnClickListener(this);


        twitter_original_Login_Button.setCallback(new Callback<TwitterSession>() {
                                                      @Override
                                                      public void success(Result<TwitterSession> result) {
                                                          firebaseauthwithtwitter(result.data);
                                                      }

                                                      @Override
                                                      public void failure(TwitterException exception) {
                                                          Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_SHORT).show();
                                                          ;
                                                      }
                                                  }
        );

        twitter_original_Login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logi = 2;
                Toast.makeText(MainActivity.this, "logi=2", Toast.LENGTH_SHORT).show();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        lb.setReadPermissions("email", "public_profile");
        lb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logi = 0;
                Toast.makeText(MainActivity.this, "logi=0", Toast.LENGTH_SHORT).show();
            }
        });
        lb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        firebaseauthwithfacebook(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "on cancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, "on error", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        signinbtn.setOnClickListener(this);

        getsigninclient();


    }

    public GoogleSignInClient getsigninclient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        return googleSignInClient;
    }

    public void onskipbuttonclicked() {
        Intent intent = new Intent(MainActivity.this, afterlogin.class);
        intent.putExtra("logi", logi);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (logi == 0) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (logi == 1) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    Toast.makeText(MainActivity.this, "inside resut", Toast.LENGTH_SHORT).show();
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "signed in as " + account.getEmail(), Toast.LENGTH_SHORT).show();
                    firebaseauthwithgoogle(account);
                } catch (ApiException e) {
                    Toast.makeText(MainActivity.this, e.getStatusMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (logi == 2) {
            twitter_original_Login_Button.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_skip_in_heading:
                onskipbuttonclicked();
                break;
            case R.id.btn_login:
                updateUI(LOGIN);
                break;

            case R.id.btn_signup:
                updateUI(SIGNUP);
                break;

            case R.id.upbutton_in_heading:
                updateUI(MAIN);
                break;

            case R.id.sign_in_button:
                logi = 1;
                Toast.makeText(MainActivity.this, "logi=1", Toast.LENGTH_SHORT).show();
                Intent signinintent = googleSignInClient.getSignInIntent();
                startActivityForResult(signinintent, RC_SIGN_IN);
                break;

            case R.id.text_for_login:
                updateUI(LOGIN);
                break;

            case R.id.text_for_signup:
                updateUI(SIGNUP);
                break;

            case R.id.signin_finalize_btn:
                firebaseauthcreateAccount(email_s.getText().toString(), password_s.getText().toString());
                break;

            case R.id.login_finalize_btn:
                firebaseauthsignInaccount(email_l.getText().toString(), password_l.getText().toString());
                break;

            case R.id.forgot_password_text:
                firebaseauthresetemailpassword(email_l.getText().toString());
                break;

            default:
                break;
        }
    }

    public void firebaseauthwithgoogle(GoogleSignInAccount account) {
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "logged in as google user:" + user.getDisplayName().toString(), Toast.LENGTH_SHORT).show();
                            onskipbuttonclicked();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    public void firebaseauthwithfacebook(AccessToken token) {
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    Toast.makeText(MainActivity.this, "logged in as fb user", Toast.LENGTH_SHORT).show();
                    onskipbuttonclicked();
                } else {
                    Toast.makeText(MainActivity.this, "Authentication failed fb.", Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });
    }

    public void firebaseauthwithtwitter(TwitterSession session) {
        showProgressDialog();
        AuthCredential credential = TwitterAuthProvider.getCredential(session.getAuthToken().token, session.getAuthToken().secret);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    Toast.makeText(MainActivity.this, "logged in as twitter user", Toast.LENGTH_SHORT).show();
                    onskipbuttonclicked();
                } else {
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });
    }


    private void firebaseauthcreateAccount(final String email, String password) {
        if (!validatesignupForm()) {
            return;
        }
        showProgressDialog();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();

                            user.updateProfile(
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(firstname+"@@"+lastname+"@@"+mobile.toString())
                                            .build()
                            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
//                                        Toast.makeText(getContext(), "User profile updated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            Toast.makeText(MainActivity.this, "logged in as email user", Toast.LENGTH_SHORT).show();
                            onskipbuttonclicked();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed."+task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }


    private void firebaseauthsignInaccount(String email, String password) {
        if (!validateloginForm()) {
            return;
        }
        showProgressDialog();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "logged in as email user", Toast.LENGTH_SHORT).show();
                            onskipbuttonclicked();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void firebaseauthresetemailpassword(String email) {
        if (!validatepasswordresetemail()) {
            return;
        }

        showProgressDialog();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "email sent to id", Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                });
    }

    private boolean validatepasswordresetemail() {
        valid = true;
        String email = email_l.getText().toString();
        if (TextUtils.isEmpty(email)) {
            email_l.setError("Required.");
            valid = false;

        } else {
            email_l.setError(null);
            auth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                    List<String> a = task.getResult().getProviders();
                    if (a.size() == 0) {
                        Toast.makeText(MainActivity.this, "Email not registered", Toast.LENGTH_SHORT).show();
                        valid = false;
                    }
                }
            });
        }


        return valid;
    }

    private boolean validateloginForm() {
        boolean valid = true;
        String email = email_l.getText().toString();
        if (TextUtils.isEmpty(email)) {
            email_l.setError("Required.");
            valid = false;
        } else {
            email_l.setError(null);
        }

        String password = password_l.getText().toString();
        if (TextUtils.isEmpty(password)) {
            password_l.setError("Required.");
            valid = false;
        } else {
            password_l.setError(null);
        }

        return valid;
    }

    private boolean validatesignupForm() {
        boolean valid = true;

        firstname = fname.getText().toString();
        if (TextUtils.isEmpty(firstname)) {
            fname.setError("Required.");
            valid = false;
        } else {
            fname.setError(null);
        }

        lastname = lname.getText().toString();
        if (TextUtils.isEmpty(lastname)) {
            lname.setError("Required.");
            valid = false;
        } else {
            lname.setError(null);
        }

        mobile = phone.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            phone.setError("Required.");
            valid = false;
        } else {
            phone.setError(null);
        }

        String email = email_s.getText().toString();

        if (TextUtils.isEmpty(email)) {
            email_s.setError("Required");
            valid = false;
        } else {
            email_s.setError(null);
        }

        String password = password_s.getText().toString();
        if (TextUtils.isEmpty(password)) {
            password_s.setError("Required.");
            valid = false;
        } else {
            password_s.setError(null);
        }

        return valid;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void changeStausBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window win = this.getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.setStatusBarColor(getResources().getColor(R.color.color1));
        }
    }

    public void addbottomdots(int position) {
        dots = new TextView[imglist.length];
        dotsLayout.removeAllViews();
        for (int i = 0; i < imglist.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(40);
            dots[i].setTextColor(Color.GRAY);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[position].setTextColor(getResources().getColor(R.color.color1));
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addbottomdots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater li;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = li.inflate(R.layout.viewpager_item_layout, container, false);
            ImageView iv = (ImageView) v.findViewById(R.id.img_in_viewpager);
            TextView tv = (TextView) v.findViewById(R.id.text_in_viewpager);
            iv.setImageResource(imglist[position]);
            tv.setText(textlist[position]);
            container.addView(v);
            return v;
        }

        @Override
        public int getCount() {
            return imglist.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public void updateUI(int state) {
        switch (state) {
            //0=visible
            //8=gone
            case MAIN:
                showlogin(8);
                showsignup(8);
                showmain(0);
                break;
            case LOGIN:
                showmain(8);
                showsignup(8);
                showlogin(0);

                break;
            case SIGNUP:
                showmain(8);
                showlogin(8);
                showsignup(0);
                break;
            default:
                break;
        }
    }

    public void showlogin(int state) {
        status_lin_layout.setVisibility(state);
        if (state == 0)
            status_text.setText("LOGIN");
        final_login_btn.setVisibility(state);
        login_layout.setVisibility(state);
    }

    public void showsignup(int state) {
        status_lin_layout.setVisibility(state);
        if (state == 0)
            status_text.setText("SIGN UP");
        final_signin_btn.setVisibility(state);
        signup_layout.setVisibility(state);
    }

    public void showmain(int state) {
        feature_page_layout.setVisibility(state);
        mix_btn_layout.setVisibility(state);
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}