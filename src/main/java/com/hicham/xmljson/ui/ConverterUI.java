package com.hicham.xmljson.ui;

import com.hicham.xmljson.service.ConverterWithApi;
import com.hicham.xmljson.service.ConverterWithoutApi;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConverterUI extends Application {

    @Override
    public void start(Stage stage) {

        // ===== HEADER =====
        Label title = new Label("XML / JSON Converter");
        title.setStyle("-fx-font-size:28px; -fx-font-weight:bold; -fx-text-fill:white;");
        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #34495e, #2c3e50);");

        // ===== CODE AREAS =====
        CodeArea inputArea = new CodeArea();
        inputArea.setParagraphGraphicFactory(LineNumberFactory.get(inputArea));
        inputArea.setWrapText(true);
        inputArea.setStyle(
                "-fx-font-family: 'Consolas'; -fx-font-size:14px;" +
                        "-fx-border-color:#2980b9; -fx-border-width:2px; -fx-border-radius:5px;" +
                        "-fx-background-color:#ffffff;"
        );

        CodeArea outputArea = new CodeArea();
        outputArea.setParagraphGraphicFactory(LineNumberFactory.get(outputArea));
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setStyle(
                "-fx-font-family: 'Consolas'; -fx-font-size:14px;" +
                        "-fx-border-color:#27ae60; -fx-border-width:2px; -fx-border-radius:5px;" +
                        "-fx-background-color:#f4f4f4;"
        );

        // ===== TOP BARS =====
        Label inputLabel = new Label("Input Editor");
        inputLabel.setStyle("-fx-font-weight:bold; -fx-font-size:14px;");
        Button clearInputBtn = new Button("Clear");
        clearInputBtn.setOnAction(e -> inputArea.clear());
        HBox inputTopBar = new HBox(10, inputLabel, clearInputBtn);
        inputTopBar.setAlignment(Pos.CENTER_LEFT);
        inputTopBar.setPadding(new Insets(5));

        Label outputLabel = new Label("Output");
        outputLabel.setStyle("-fx-font-weight:bold; -fx-font-size:14px;");
        Button clearOutputBtn = new Button("Clear");
        clearOutputBtn.setOnAction(e -> outputArea.clear());
        HBox outputTopBar = new HBox(10, outputLabel, clearOutputBtn);
        outputTopBar.setAlignment(Pos.CENTER_LEFT);
        outputTopBar.setPadding(new Insets(5));

        // ===== SCROLLABLE AREAS =====
        ScrollPane inputScroll = new ScrollPane(inputArea);
        inputScroll.setFitToWidth(true);
        inputScroll.setFitToHeight(true);
        inputScroll.setPannable(true);

        ScrollPane outputScroll = new ScrollPane(outputArea);
        outputScroll.setFitToWidth(true);
        outputScroll.setFitToHeight(true);
        outputScroll.setPannable(true);

        VBox inputBox = new VBox(5, inputTopBar, inputScroll);
        VBox.setVgrow(inputScroll, Priority.ALWAYS);

        VBox outputBox = new VBox(5, outputTopBar, outputScroll);
        VBox.setVgrow(outputScroll, Priority.ALWAYS);

        HBox center = new HBox(15, inputBox, outputBox);
        HBox.setHgrow(inputBox, Priority.ALWAYS);
        HBox.setHgrow(outputBox, Priority.ALWAYS);
        center.setPadding(new Insets(20));
        center.setBackground(new Background(new BackgroundFill(Color.web("#ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));

        // ===== BUTTONS =====
        Button importBtn = new Button("Import File");
        Button xmlJsonApiBtn = new Button("XML → JSON (API)");
        Button jsonXmlApiBtn = new Button("JSON → XML (API)");
        Button xmlJsonNoApiBtn = new Button("XML → JSON (No API)");
        Button jsonXmlNoApiBtn = new Button("JSON → XML (No API)");
        Button exploiterBtn = new Button("Exploiter");

        Button[] allButtons = {importBtn, xmlJsonApiBtn, jsonXmlApiBtn, xmlJsonNoApiBtn,
                jsonXmlNoApiBtn, exploiterBtn};
        for (Button btn : allButtons) {
            btn.setStyle("-fx-background-color:#2980b9; -fx-text-fill:white; -fx-font-weight:bold; -fx-cursor: hand;");
            btn.setPrefWidth(140);
        }

        // ===== BUTTON ACTIONS =====
        // --- Import File ---
        importBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Ouvrir un fichier XML ou JSON");
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("XML Files", "*.xml"),
                    new FileChooser.ExtensionFilter("JSON Files", "*.json")
            );

            File file = fc.showOpenDialog(stage);
            if (file != null && file.exists()) {
                try {
                    String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                    inputArea.replaceText(text);
                } catch (Exception ex) {
                    outputArea.replaceText("❌ Erreur lecture fichier : " + ex.getMessage());
                }
            }
        });

        // --- Conversion Buttons ---
        xmlJsonApiBtn.setOnAction(e ->
                outputArea.replaceText(formatText(ConverterWithApi.xmlToJson(inputArea.getText())))
        );

        jsonXmlApiBtn.setOnAction(e ->
                outputArea.replaceText(formatText(ConverterWithApi.jsonToXml(inputArea.getText())))
        );

        xmlJsonNoApiBtn.setOnAction(e ->
                outputArea.replaceText(formatText(ConverterWithoutApi.xmlToJson(inputArea.getText())))
        );

        jsonXmlNoApiBtn.setOnAction(e ->
                outputArea.replaceText(formatText(ConverterWithoutApi.jsonToXml(inputArea.getText())))
        );

        // --- Exploiter Button ---
        exploiterBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Enregistrer le contenu");
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("XML Files", "*.xml"),
                    new FileChooser.ExtensionFilter("JSON Files", "*.json"),
                    new FileChooser.ExtensionFilter("TXT Files", "*.txt")
            );

            File file = fc.showSaveDialog(stage);
            if (file != null) {
                try {
                    Files.writeString(file.toPath(), outputArea.getText(), StandardCharsets.UTF_8);
                    outputArea.replaceText("💾 Contenu sauvegardé dans : " + file.getAbsolutePath());
                } catch (Exception ex) {
                    outputArea.replaceText("❌ Erreur sauvegarde : " + ex.getMessage());
                }
            }
        });

        // ===== BOTTOM BAR =====
        HBox bottom = new HBox(10, importBtn, xmlJsonApiBtn, jsonXmlApiBtn,
                xmlJsonNoApiBtn, jsonXmlNoApiBtn, exploiterBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(15));
        bottom.setBackground(new Background(new BackgroundFill(Color.web("#ecf0f1"), CornerRadii.EMPTY, Insets.EMPTY)));

        // ===== ROOT =====
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(center);
        root.setBottom(bottom);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color:#bdc3c7;");

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("XML / JSON Converter");
        stage.show();
    }

    // ===== FORMATAGE DU TEXTE =====
    private String formatText(String text) {
        if (text == null || text.isEmpty()) return "";

        text = text.trim();
        if (text.startsWith("<")) { // XML
            return prettyPrintXML(text);
        } else {
            // JSON simple
            text = text.replaceAll("(\\{|\\[)", "$1\n");
            text = text.replaceAll("(\\}|\\])", "$1\n");
            text = text.replaceAll("(,)", "$1\n");
            return text;
        }
    }

    // ===== FORMATEUR XML =====
    private String prettyPrintXML(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = db.parse(is);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception e) {
            return xml; // si erreur, retourne texte original
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
