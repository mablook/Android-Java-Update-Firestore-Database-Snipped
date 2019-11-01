// Created By Marcelo Bossle
// Problem: I just need the information to be saved "without creating a listener".
// My database should be open for next writes.
// Solution:

package com.look.project;

import ...

public class PortalActivity extends AppCompatActivity {
    private WebView mWebview ;
    String link = "";// global variable
    Resources res;// global variable

    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_portal);
        mWebview = (WebView) findViewById(R.id.portal_webview);
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        final FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(view, "Do you want to go back to the menu?", Snackbar.LENGTH_LONG)
                        .setAction("User Info", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(PortalActivity.this, ContentActivity.class));
                            }});
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(ContextCompat.getColor(PortalActivity.this, R.color.stockSpotsBackground));
                snackbar.show();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mWebview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View Webview, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY && scrollY > 0) {
                        fab.hide();
                    }
                    if (scrollY < oldScrollY) {
                        fab.show();
                    }
                }

            });

            final Activity activity = this;

            mWebview.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
                }

            });
            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mWebview.loadUrl("https://portal.stockspots.eu/fSearch.html/?userUID=" + uid);

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( PortalActivity.this,
                    new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String newToken = instanceIdResult.getToken();
                            updateToken(newToken);
                        }
                    });

        }

    }


    private void updateToken(final String token) {

        final Query needsQuery;
        final Query offersQuery;
        final DatabaseReference mDatabase;
        final String manufacturer = Build.MANUFACTURER;
        final String brand = Build.BRAND;
        final String model = Build.MODEL;
        final String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        final String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String femail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference uidRef = rootRef.child("user").child(uid);

        offersQuery = uidRef.child("deviceGroup").orderByChild("token").equalTo(token);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Query query = mDatabase.child("user").child(uid).child("deviceGroup").child(androidId);

        // Check if user exist
        final Query gameQuery = mDatabase.child("user").child(uid);

        gameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){

                    Map<String, Object> map = new HashMap<>();
                    map.put("date", date);
                    map.put("androidUID", androidId);
                    map.put("manufacturer", manufacturer);
                    map.put("brand", brand);
                    map.put("model", model);
                    map.put("token", token);

                    // Save info in deviceGroup to send notifications if token is refresh
                    mDatabase.child("user").child(uid).child("deviceGroup").child(androidId).setValue(map);

                }else{

                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("create", date);
                    map1.put("emailverify", "false");
                    map1.put("useremail", femail);
                    map1.put("username", femail);
                    map1.put("userpass", "_look_");

                    mDatabase.child("user").child(uid).updateChildren(map1);

                    Map<String, Object> map = new HashMap<>();
                    map.put("date", date);
                    map.put("androidUID", androidId);
                    map.put("manufacturer", manufacturer);
                    map.put("brand", brand);
                    map.put("model", model);
                    map.put("token", token);

                    mDatabase.child("user").child(uid).child("deviceGroup").child(androidId).setValue(map);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

}
