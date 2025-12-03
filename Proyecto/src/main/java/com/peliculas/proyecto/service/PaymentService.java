package com.peliculas.proyecto.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

public class PaymentService {

    public PaymentService() {
        Stripe.apiKey = "sk_test_51SaBP7GtONsWMoMfNOI7z2uLR7CWWHeN6rkmE3qL2Fyknje2t8012XPPM6EAbZUjDVFbCP21u5Wp3Lxl62iA5u4b00VQEv1Skl";  // tu clave
    }

    public String iniciarPago(double precioEuros) throws Exception {

        long precioCentimos = (long) (precioEuros * 100);

        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(precioCentimos)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Alquiler película")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://example.com/success")
                .setCancelUrl("https://example.com/cancel")
                .build();

        Session session = Session.create(params);

        return session.getUrl();
    }
}
