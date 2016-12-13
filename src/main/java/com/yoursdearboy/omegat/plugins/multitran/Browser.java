package com.yoursdearboy.omegat.plugins.multitran;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * http://docs.oracle.com/javafx/2/swing/SimpleSwingBrowser.java.htm
 */
class Browser extends JFXPanel {
    private WebEngine engine;
    private final String domain;

    Browser(final String domain) {
        this.domain = domain;
        Platform.runLater(new Runnable() {
            public void run() {
                WebView webView = new WebView();
                engine = webView.getEngine();

                // FIXME: Prevent visiting the url
                // for now this is the best solution
                engine.locationProperty().addListener(new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> observable, final String oldValue, String newValue) {
                        if (!newValue.contains(domain)) {
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    engine.load(oldValue);
                                }
                            });
                            try {
                                Desktop.getDesktop().browse(new URI(newValue));
                            } catch (URISyntaxException ignored) {
                            } catch (IOException ignored) {
                            }
                        }
                    }
                });

                setScene(new Scene(webView));
                loadURL(domain);
            }
        });
    }

    void loadURL(final String url) {
        Platform.runLater(new Runnable() {
            public void run() {
                String tmp = toURL(url);
                if (tmp == null) tmp = toURL("http://" + url);
                engine.load(tmp);
            }
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException ignored) {
            return null;
        }
    }
}
