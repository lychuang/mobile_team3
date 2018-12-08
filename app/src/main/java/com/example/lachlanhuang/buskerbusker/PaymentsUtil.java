package com.example.lachlanhuang.buskerbusker;

import android.app.Activity;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MICROS;

// TODO: add payment gateway and unique merchant identifier (will be shown in PaymentConstants.java)

public class PaymentsUtil {

    private static final BigDecimal MICROS = new BigDecimal(1000000d);

    private PaymentsUtil() {}

    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0);
    }
    // creates instance of PaymentsClient to use in FeedPostActivity later

    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(PaymentsConstants.PAYMENTS_ENVIRONMENT).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }



    /* Google encrypts info about payer's card for secure processing
     * can either process it via a supported gateway or through a merchant's secure servers
     * probably have to register performers as merchants
     * Doing it with gateway
     */
    private static JSONObject getGatewayTokenizationSpecification()
            throws JSONException, RuntimeException {
        if (PaymentsConstants.PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS.isEmpty()) {
            throw new RuntimeException(
                    "Please edit the Constants.java file to add gateway name and other parameters your "
                            + "processor requires");
        }
        JSONObject tokenizationSpecification = new JSONObject();

        tokenizationSpecification.put("type", "PAYMENT_GATEWAY");
        JSONObject parameters = new JSONObject(PaymentsConstants.PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS);
        tokenizationSpecification.put("parameters", parameters);

        return tokenizationSpecification;
    }


    // card networks supported
    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray(PaymentsConstants.SUPPORTED_NETWORKS);
    }

    // card auth methods supported
    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray(PaymentsConstants.SUPPORTED_METHODS);
    }

    // describes accepted cards
    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());


        cardPaymentMethod.put("parameters", parameters);

        return cardPaymentMethod;
    }

    // describe expected accepted cards
    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());

        return cardPaymentMethod;
    }

    public static Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));

            return Optional.of(isReadyToPayRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    // give google API info about the payment
    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("currencyCode", PaymentsConstants.CURRENCY_CODE);

        return transactionInfo;
    }

    // merchant info
    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", "Example Merchant");
    }

    public static Optional<JSONObject> getPaymentDataRequest(String price) {
        try {
            JSONObject paymentDataRequest = PaymentsUtil.getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(PaymentsUtil.getCardPaymentMethod()));
            paymentDataRequest.put("transactionInfo", PaymentsUtil.getTransactionInfo(price));
            paymentDataRequest.put("merchantInfo", PaymentsUtil.getMerchantInfo());


            return Optional.of(paymentDataRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    public static String microsToString(long micros) {
        return new BigDecimal(micros).divide(MICROS).setScale(2, RoundingMode.HALF_EVEN).toString();
    }



}
