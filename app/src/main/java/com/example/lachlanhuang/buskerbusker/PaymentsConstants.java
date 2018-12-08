package com.example.lachlanhuang.buskerbusker;

import com.google.android.gms.wallet.WalletConstants;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

public class PaymentsConstants {

    /* there is difference between ENVIRONMENT_TEST and ENVIRONMENT_PRODUCTION

     */
    public static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST;

    public static final List<String> SUPPORTED_NETWORKS = Arrays.asList(
            "AMEX",
            "DISCOVER",
            "MASTERCARD",
            "VISA");

    public static final List<String> SUPPORTED_METHODS =
            Arrays.asList(
                    "PAN_ONLY",
                    "CRYPTOGRAM_3DS");


    /* required by API
     * local currency
     */
    public static final String CURRENCY_CODE = "USD";


    /* name of payment processor
     * change later
     */
    public static final String PAYMENT_GATEWAY_TOKENIZATION_NAME = "example";

    public static final HashMap<String, String> PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS =
            new HashMap<String, String>() {
                {
                    put("gateway", PAYMENT_GATEWAY_TOKENIZATION_NAME);
                    put("gatewayMerchantId", "exampleGatewayMerchantId");

                }
            };
}
