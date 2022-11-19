package ru.sinredemption.launcher;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.*;


public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("SinRedemption's launcher - beta 0.2");
        stage.setScene(getScene());
        stage.show();
    }

    static Scene getScene(){
        Configuration conf = new Configuration();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 640, 480);
        scene.getStylesheets().add
                (Launcher.class.getResource("main.css").toExternalForm());

        //Заголовок
        Text scenetitle = new Text("Добро пожаловать!");
        scenetitle.setId("welcome-text");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        //Строка с логином
        Label userName = new Label("Имя пользователя:");
        userName.setId("label");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        //Строка с паролем
        Label pw = new Label("Пароль:");
        pw.setId("label");
        grid.add(pw, 0, 2);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        //Дроплист с оперативкой
        ObservableList<String> options = FXCollections.observableArrayList("1024", "2048", "4096");
        ComboBox<String> comboBox = new ComboBox<>(options);
        grid.add(comboBox, 1, 4);

        //Кнопка запуска
        Button btn = new Button("Запуск");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 5);


        Text errorText = new Text();
        errorText.setId("actiontarget");
        grid.add(errorText, 0, 6);

        userTextField.setText(Configuration.username);
        pwBox.setText(Configuration.password);
        comboBox.setValue(Configuration.ram);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Configuration.username = userTextField.getText();
                Configuration.password = pwBox.getText();
                Configuration.ram = comboBox.getValue().toString();
                conf.saveConfiguration();
                try {
                    errorText.setText("");
                    API.runMinecraft();
                    conf.saveConfiguration();
                    errorText.setText("Done");
                    errorText.setFill(Color.GREEN);
                    System.exit(0);
                }catch (Exception ex){
                    errorText.setText(ex.toString());
                    errorText.setFill(Color.FIREBRICK);
                }
            }
        });

        comboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Select RAM");
            }
        });

        return scene;
    }

    public static void main(String[] args) {
        launch();
    }
}