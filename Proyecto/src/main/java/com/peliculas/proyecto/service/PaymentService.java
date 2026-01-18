package com.peliculas.proyecto.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

/**
 * Servicio encargado de gestionar los pagos mediante Stripe.
 * <p>
 * Esta clase permite crear sesiones de pago para el alquiler de películas,
 * configurando los datos necesarios como precio, moneda y URL de éxito o cancelación.
 * <p>
 * Contiene también ejemplos de tarjetas de prueba para escenarios de éxito y error.
 *
 * @author Arturo Campos
 */
public class PaymentService {

    /**
     * Constructor del servicio de pago.
     * <p>
     * Inicializa la API key de Stripe para permitir la creación de sesiones de pago.
     * Esta API key es de prueba y debe ser reemplazada por una key de producción
     * en un entorno real.
     *
     * @author Arturo Campos
     */
    public PaymentService() {
        Stripe.apiKey = "sk_test_51SaBP7GtONsWMoMfNOI7z2uLR7CWWHeN6rkmE3qL2Fyknje2t8012XPPM6EAbZUjDVFbCP21u5Wp3Lxl62iA5u4b00VQEv1Skl";
    }
    /**
     * Inicia una sesión de pago mediante Stripe Checkout.
     * <p>
     * Convierte el precio de euros a céntimos, configura los parámetros de la sesión
     * incluyendo moneda, descripción, cantidad y URLs de éxito y cancelación.
     * Crea la sesión y devuelve la URL de Stripe Checkout.
     * <p>
     * En caso de error en la creación de la sesión, devuelve null y muestra
     * un mensaje de error en consola.
     *
     * @param precioEuros Precio del alquiler de la película en euros
     * @return URL de la sesión de pago de Stripe o null si ocurrió un error
     *
     * @author Arturo Campos
     */
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
