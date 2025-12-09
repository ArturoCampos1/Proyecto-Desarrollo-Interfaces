package com.peliculas.proyecto.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

public class PaymentService {

    public PaymentService() {
        Stripe.apiKey = "sk_test_51SaBP7GtONsWMoMfNOI7z2uLR7CWWHeN6rkmE3qL2Fyknje2t8012XPPM6EAbZUjDVFbCP21u5Wp3Lxl62iA5u4b00VQEv1Skl";
    }

    public String iniciarPago(double precioEuros) {
        try {
            long precioCentimos = (long) (precioEuros * 100);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setSuccessUrl("http://localhost/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost/cancel")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("eur")
                                                    .setUnitAmount(precioCentimos)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Alquiler de película")
                                                                    .setDescription("Alquiler por 48 horas")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setQuantity(1L)
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            return session.getUrl();

        } catch (StripeException e) {
            System.err.println("Error creando sesión de pago: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /*

   - Tarjetas de Prueba Básicas

    Tarjeta exitosa (usar siempre para pagos correctos):
    Número: 4242 4242 4242 4242
    Fecha: Cualquier fecha futura (ej: 12/34)
    CVC: Cualquier 3 dígitos (ej: 123)

    - Tarjetas para Escenarios Específicos

    Rechazo genérico:
    Número: 4000 0000 0000 0002

    Fondos insuficientes:
    Número: 4000 0000 0000 9995

    CVC incorrecto:
    Número: 4000 0000 0000 0127

     */
}
