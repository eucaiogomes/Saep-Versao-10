package util;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega a tela de Login como a primeira tela
        // Certifique-se de que o caminho para o FXML está correto
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml")); // <<< AJUSTE O CAMINHO AQUI
        primaryStage.setTitle("SAEP - Sistema de Apoio Educacional Pedagógico");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}