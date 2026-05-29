package com.sentinelav.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principal de UI do SentinelAV, responsável por inicializar
 * o motor de renderização JavaFX, carregar os estilos Cyberpunk 
 * e montar o palco principal (Stage) onde a mágica acontece.
 */
public class SentinelApp extends Application {

    /**
     * O método start() constrói a Scene inicial lendo o arquivo FXML base
     * e o stylesheet global.
     * 
     * @param stage O Stage (Palco) principal injetado pelo ciclo de vida do JavaFX.
     * @throws Exception Se os FXMLs não forem encontrados (ou se o dev esqueceu de buildar).
     */
    @Override
    public void start(Stage stage) throws Exception {
        // "Eu não sei como funciona o FXMLLoader por debaixo dos panos, mas funciona."
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/sentinelav/ui/views/main.fxml")
        );

        Scene scene = new Scene(loader.load());
        
        // Injetando o CSS Cyberpunk para não cegar o usuário com a tela branca padrão.
        scene.getStylesheets().add(
                getClass().getResource("/com/sentinelav/ui/styles/theme.css").toExternalForm()
        );

        stage.setTitle("SentinelAV");
        stage.setScene(scene);
        stage.show();
    }
}
