package com.example.lachlanhuang.buskerbusker;

import android.app.Activity;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TODO: add payment gateway and unique merchant identifier (will be shown in PaymentConstants.java)

public class PaymentsUtil {

    private PaymentsUtil() {}

    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0);
    }
    /* creates instance of PaymentsClient to use in FeedPostActivity later
     *TODO: work on it/fix it

    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions().Builder().setEnvironment(PaymentsConstants.PAYMENTS_ENVIRONMENT).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }
    */


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


    // info about the payment
    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("currencyCode", PaymentsConstants.CURRENCY_CODE);

        return transactionInfo;
    }

}
