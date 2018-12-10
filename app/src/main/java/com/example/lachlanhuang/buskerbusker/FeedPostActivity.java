package com.example.lachlanhuang.buskerbusker;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lachlanhuang.buskerbusker.database.Post;
import com.example.lachlanhuang.buskerbusker.database.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;




import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class FeedPostActivity extends AppCompatActivity {

    private static final String TAG = "FeedPostActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    DatabaseReference ref;

    private DatabaseReference postReference;
    private ValueEventListener postListener;
    private String postKey;



    // images downloaded from the web for the sake of V I S U A L S
    // rando european places
    private String[] images = {
            "https://images.unsplash.com/photo-1543996991-8e851c2dc841?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1651&q=80",
            "https://images.unsplash.com/photo-1482042519045-a51ea0cfb61d?ixlib=rb-1.2.1&auto=format&fit=crop&w=1650&q=80",
            "https://images.unsplash.com/photo-1453747063559-36695c8771bd?ixlib=rb-1.2.1&auto=format&fit=crop&w=1650&q=80"

    };

    ViewPager viewPager;
    SpringDotsIndicator springDotsIndicator;
    private  int dotsCount;

    private PaymentsClient paymentsClient;
    private Button tipButton;

    private TextView buskerNameView;
    private TextView buskerLocationView;
    private TextView textDescView;






    private long totalCost = 90 * 1000000;

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_post);


        viewPager = (ViewPager) findViewById(R.id.view_pager);
        springDotsIndicator = (SpringDotsIndicator) findViewById(R.id.spring_dots_indicator);

        PhotoViewPagerAdapter photoViewPagerAdapter = new PhotoViewPagerAdapter(FeedPostActivity.this, images);

        viewPager.setAdapter(photoViewPagerAdapter);
        springDotsIndicator.setViewPager(viewPager);


        dotsCount = photoViewPagerAdapter.getCount();

        tipButton = findViewById(R.id.tip_btn);

        paymentsClient = PaymentsUtil.createPaymentsClient(this);
        possiblyShowGooglePayButton();

        tipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPayment(view);
            }
        });

        /* THis might not belong here

         */
        // Get post key from intent
//        postKey = getIntent().getStringExtra(EXTRA_POST_KEY);
//        postKey = ref.child("posts").push().getKey();
//        if (postKey == null) {
//            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
//        }
//
//        // Initialize Database
//        postReference = FirebaseDatabase.getInstance().getReference()
//                .child("posts").child(postKey);
//
//        buskerNameView = (TextView) findViewById(R.id.busker_name);
//        buskerLocationView = (TextView) findViewById(R.id.busker_location);
//        textDescView = (TextView) findViewById(R.id.post_description);



    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // Add value event listener to the post
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object
//                Post post = dataSnapshot.getValue(Post.class);
//
//                buskerNameView.setText(post.buskerName);
//                buskerLocationView.setText(post.buskerLocation);
//                textDescView.setText(post.textDesc);
//                // [END_EXCLUDE]
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                Toast.makeText(FeedPostActivity.this, "Failed to load post.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        };
//        postReference.addValueEventListener(postListener);
//
//        // Keep copy of post listener to remove it when app stops
//
//
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // Remove post value event listener
//        if (postListener != null) {
//            postReference.removeEventListener(postListener);
//        }
//
//    }



    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }

        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(Task<Boolean> task) {
                        try {
                            boolean result = task.getResult(ApiException.class);
//                            setGooglePayAvailable(result);
                        } catch (ApiException exception) {
                            // Process error
                            Log.w("isReadyToPay failed", exception);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        // user cancelled so break
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                    default:
                        // Do nothing.
                }

                // Re-enables the Google Pay payment button.
                tipButton.setClickable(true);
                break;
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        String paymentInformation = paymentData.toJson();

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String)
        if (paymentInformation == null) {
            return;
        }
        JSONObject paymentMethodData;

        try {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken"
            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type")
                    .equals("PAYMENT_GATEWAY")
                    && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
                    .equals("examplePaymentMethodToken")) {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(this)
                                .setTitle("Warning")
                                .setMessage(
                                        "Gateway name set to \"example\" - please modify "
                                                + "Constants.java and replace it with your own gateway.")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();
            }



            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData").getString("token"));
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString());
            return;
        }
    }

    private void handleError(int statusCode) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }


    public void requestPayment(View view) {
        // Disables the button to prevent multiple clicks.
        tipButton.setClickable(false);

        // This price is not displayed to the user.
        // temporary price. TODO: currently hardcoded, so need to allow the user to input their own tip
        String price = PaymentsUtil.microsToString(totalCost);

        // TransactionInfo transaction = PaymentsUtil.createTransaction(price);
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }



}
