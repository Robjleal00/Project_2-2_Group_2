package org.openjfx.UI;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class exitAppHandler {

    static boolean answer;

    public static boolean exitApp(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label exitLabel = new Label(message);

        //Creating two buttons
        Button yesExit = new Button("YES");
        Button noExit = new Button("NO");

        yesExit.setOnAction(e -> {
            answer = true;
            window.close();
        });

        noExit.setOnAction(e -> {
            answer = false;
            window.close();
        });

        VBox exitLayout = new VBox(20);
        exitLayout.getChildren().addAll(exitLabel, yesExit, noExit);
        exitLayout.setAlignment(Pos.CENTER);

        Scene exitScene = new Scene(exitLayout);
        window.setScene(exitScene);
        window.showAndWait();

        return answer;
    }
}