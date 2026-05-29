package com.sentinelav.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

/**
 * Controller do Painel de Configurações Cyberpunk.
 * Onde o usuário se sente um verdadeiro SysAdmin.
 */
public class SettingsController {

    @FXML private CheckBox deepScanToggle;
    @FXML private CheckBox autoQuarantineToggle;
    @FXML private CheckBox networkToggle;

    /**
     * Inicializa os botões cibernéticos.
     */
    @FXML
    public void initialize() {
        // WIP: Fazer o binding (ponte) real com o Singleton do SentinelEngine.
        // Se o usuário clicar em "Network", a gente avisa que ainda não foi implementado e pede doação no Patreon.
    }
}
