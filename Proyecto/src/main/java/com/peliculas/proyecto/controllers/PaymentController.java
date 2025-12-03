package com.peliculas.proyecto.controllers;

import com.peliculas.proyecto.dto.Usuario;
import com.peliculas.proyecto.service.PaymentService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PaymentController {

    public void setUsuario(Usuario usuarioActual) {
    }
    @FXML
    private Label lblPrecio;

    private final PaymentService paymentService = new PaymentService();

    @FXML
    private void handlePagar() {
        try {
            String checkoutUrl = paymentService.iniciarPago(3.99);

            if (checkoutUrl == null) return;

            java.awt.Desktop.getDesktop().browse(new java.net.URI(checkoutUrl));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
