package com.sentinelav.ui.controllers;

import com.sentinelav.core.SentinelEngine;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * O Carcereiro do Sentinel. 
 * Controller responsável por exibir, excluir ou restaurar 
 * os arquivos isolados pela varredura heurística.
 */
public class QuarantineController {

    @FXML private TableView<File> quarantineTable;
    @FXML private TableColumn<File, String> fileNameCol;
    @FXML private TableColumn<File, String> filePathCol;
    @FXML private TableColumn<File, String> dateCol;

    private SentinelEngine engine;
    private ObservableList<File> quarantinedFiles;

    @FXML
    public void initialize() {
        engine = SentinelEngine.getInstance();
        quarantinedFiles = FXCollections.observableArrayList();

        // Se o arquivo tá aqui, ele já está condenado.
        fileNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        filePathCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAbsolutePath()));
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(sdf.format(cellData.getValue().lastModified())));

        quarantineTable.setItems(quarantinedFiles);
        loadQuarantine();
    }

    /**
     * Puxa a lista de prisioneiros atuais.
     */
    public void loadQuarantine() {
        quarantinedFiles.clear();
        File[] files = engine.getScanner().listQuarantineFiles();
        if (files != null) {
            quarantinedFiles.addAll(files);
        }
    }

    /**
     * O botão do perdão.
     * Restaura o arquivo para o seu local de origem, caso o usuário tenha 100% de certeza.
     */
    @FXML
    private void restoreSelected() {
        File selected = quarantineTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Em um sistema real teríamos que salvar o caminho original em um banco de dados
            // Aqui vamos apenas "fingir" um restore local por enquanto
            System.out.println("Restaurando arquivo (WIP)...");
            loadQuarantine();
        }
    }

    /**
     * O botão da execução sumária.
     * Deleta o arquivo maligno da face da terra.
     */
    @FXML
    private void deleteSelected() {
        File selected = quarantineTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // "Hasta la vista, Malware."
            engine.getScanner().deleteFileFromQuarantine(selected);
            loadQuarantine();
        }
    }
}
