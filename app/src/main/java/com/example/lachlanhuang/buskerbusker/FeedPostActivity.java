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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.BuyButtonText;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentMode;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class FeedPostActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient
        .OnConnectionFailedListener {

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

    private GoogleApiClient mGoogleApiClient;
    private SupportWalletFragment mWalletFragment;
    private SupportWalletFragment mXmlWalletFragment;

    private MaskedWallet mMaskedWallet;
    private FullWallet mFullWallet;


    public static final int MASKED_WALLET_REQUEST_CODE = 888;
    public static final int FULL_WALLET_REQUEST_CODE = 889;

    public static final String WALLET_FRAGMENT_ID = "wallet_fragment";

    private long totalCost = 90 * 1000000;

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;



    // TODO: Create page for when you click on each post
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


//        mWalletFragment = (SupportWalletFragment) getSupportFragmentManager().findFragmentByTag(WALLET_FRAGMENT_ID);
//
//        WalletFragmentInitParams startParams;
//        WalletFragmentInitParams.Builder startParamBuilder = WalletFragmentInitParams.newBuilder()
//                .setMaskedWalletRequest(generateMaskedWalletRequest());
//
//        startParams = startParamBuilder.build();
//
//        if (mWalletFragment == null) {
//            WalletFragmentStyle walletFragmentStyle = new WalletFragmentStyle()
//                    .setBuyButtonText(WalletFragmentStyle.BuyButtonText.BUY_WITH)
//                    .setBuyButtonWidth(WalletFragmentStyle.Dimension.MATCH_PARENT);
//
//            WalletFragmentOptions walletFragmentOptions = WalletFragmentOptions.newBuilder()
//                    .setEnvironment(WalletConstants.ENVIRONMENT_SANDBOX)
//                    .setFragmentStyle(walletFragmentStyle)
//                    .setTheme(WalletConstants.THEME_DARK)
//                    .setMode(WalletFragmentMode.BUY_BUTTON)
//                    .build();
//
//            mWalletFragment = SupportWalletFragment.newInstance(walletFragmentOptions);
//
//            mWalletFragment.initialize(startParams);
//        }
//
//        getSupportFragmentManager().beginTransaction().replace(R.id.wallet_button_holder, mWalletFragment, WALLET_FRAGMENT_ID)
//                .commit();
//
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this).addApi(Wallet.API, new Wallet.WalletOptions.Builder()
//                        .setEnvironment(WalletConstants.ENVIRONMENT_SANDBOX)
//                        .setTheme(WalletConstants.THEME_DARK)
//                        .build())
//                .build();



        paymentsClient = PaymentsUtil.createPaymentsClient(this);
        possiblyShowGooglePayButton();

        tipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPayment(view);
            }
        });


//
    }

//    @Override
//    protected void onStart() {
//        mGoogleApiClient.connect();
//        super.onStart();
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, this);
//
//        switch (requestCode) {
//            case MASKED_WALLET_REQUEST_CODE:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        mMaskedWallet = data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
//                        break;
//
//
//                    case Activity.RESULT_CANCELED:
//                        break;
//
//                    default:
//                        Toast.makeText(this, "An error occured", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//                break;
//            case FULL_WALLET_REQUEST_CODE:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        mFullWallet = data.getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);
//                        Toast.makeText(this, mFullWallet.getProxyCard().getPan().toString(), Toast.LENGTH_SHORT).show();
//                        break;
//
//                    default:
//                        Toast.makeText(this, "An error occured", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//                break;
//            case WalletConstants.RESULT_ERROR:
//                Toast.makeText(this, "An error occured", Toast.LENGTH_SHORT).show();
//                break;
//
//        }
//    }

//    public static NotifyTransactionStatusRequest generateNotifyTransactionStatusRequest(String googleTransactionId, int status) {
//
//    }

//    private MaskedWalletRequest generateMaskedWalletRequest() {
//        MaskedWalletRequest maskedWalletRequest = MaskedWalletRequest.newBuilder()
//                .setMerchantName("GOOGLE")
//                .setPhoneNumberRequired(true)
//                .setShippingAddressRequired(true)
//                .setCurrencyCode("USD")
//
//                .setEstimatedTotalPrice("10.00")
//                .build();
//        return maskedWalletRequest;
//    }

//    private FullWalletRequest generateFullWalletRequest(String googleTransactionID) {
//        FullWalletRequest fullWalletRequest = FullWalletRequest.newBuilder()
//                .setCart(Cart.newBuilder()
//                        .setCurrencyCode("USD")
//                        .setTotalPrice("10.10")
//                        .addLineItem(LineItem.newBuilder()
//                                .setCurrencyCode("USD")
//                                .setQuantity("1")
//                                .setUnitPrice("10.00")
//                                .setTotalPrice("10.00")
//                                .build())
//                        .build())
//                .build();
//        return fullWalletRequest;
//    }

//    public void requestFullWallet() {
//        if (mGoogleApiClient.isConnected()) {
//            Wallet.Payments.loadFullWallet(mGoogleApiClient,
//                    generateFullWalletRequest(mMaskedWallet.getGoogleTransactionId()),
//                    FULL_WALLET_REQUEST_CODE);
//        }
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


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
