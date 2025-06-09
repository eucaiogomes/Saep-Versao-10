package controller;

import DAO.ProfessorDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Professor;
import util.Sessao;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtSenha;

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String email = txtEmail.getText();
        String senha = txtSenha.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Login", "Preencha todos os campos.");
            return;
        }

        try {
            Professor professor = ProfessorDAO.buscarProfessorPorEmailESenha(email, senha);

            if (professor != null) {
                Sessao.getInstance().setProfessorLogado(professor);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Login realizado com sucesso!");
                abrirTelaPrincipal(event);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Login", "Email ou senha incorretos.");
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Erro ao tentar fazer login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- NOVO MÉTODO: ABRIR TELA DE CADASTRO DE PROFESSOR ---
    @FXML
    private void handleCadastrarProfessor(ActionEvent event) {
        try {
            // Ajuste o caminho conforme a localização do seu FXML de cadastro de professor
            Parent root = FXMLLoader.load(getClass().getResource("/view/cadastroprofessor.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Cadastro de Professor");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de cadastro de professor: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de Cadastro de Professor.");
        }
    }

    private void abrirTelaPrincipal(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/principal.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Principal");
        stage.show();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}