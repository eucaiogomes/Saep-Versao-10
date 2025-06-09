package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import util.Sessao;
import model.Professor;

import java.io.IOException;

public class PrincipalController {

    @FXML
    private Label lblBemVindo; // Certifique-se de que este ID fx:id="lblBemVindo" existe no seu FXML Principal

    /**
     * Método de inicialização do controlador.
     * É chamado automaticamente pelo JavaFX após o carregamento do FXML.
     */
    @FXML
    public void initialize() {
        Professor professorLogado = Sessao.getInstance().getProfessorLogado();
        if (professorLogado != null) {
            lblBemVindo.setText("Bem-vindo(a), " + professorLogado.getNmProfessor() + "!");
        } else {
            lblBemVindo.setText("Bem-vindo(a)!");
            // Se, por algum motivo, não houver professor logado ao entrar no PrincipalController,
            // podemos redirecionar para a tela de login. Isso serve como uma camada extra de segurança.
            try {
                handleLogout(null); // Chama o método de logout para garantir que a sessão seja limpa e volte ao login
            } catch (IOException e) {
                System.err.println("Erro ao redirecionar para login na inicialização: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Lida com a ação de clique no botão "Gerenciar Turmas".
     * Abre a tela de cadastro e gerenciamento de turmas.
     * Requer que um professor esteja logado.
     * @param event O evento de ação.
     */
    @FXML
    private void handleCadastrarTurma(ActionEvent event) {
        Professor professorLogado = Sessao.getInstance().getProfessorLogado();

        if (professorLogado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Acesso Negado", "Nenhum professor logado. Faça login para gerenciar turmas.");
            return;
        }

        try {
            // Carrega o FXML da tela de Turmas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/cadastroturma.fxml")); // Verifique o caminho correto
            Parent root = loader.load();

            // Obtém o controller da tela de Turmas para injetar o professor logado (se necessário)
            CDTurmaController turmaController = loader.getController();
            turmaController.setProfessorLogado(professorLogado); // Garante que o controller de turma receba o professor logado

            Stage stage = new Stage();
            stage.setTitle("Gerenciar Turmas - " + professorLogado.getNmProfessor());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de gerenciamento de turma: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de Gerenciar Turmas.");
        }
    }

    /**
     * Lida com a ação de clique no botão "Gerenciar Atividades".
     * Abre a tela de gerenciamento de atividades.
     * Requer que um professor esteja logado.
     * @param event O evento de ação.
     */
    @FXML
    private void handleGerenciarAtividade(ActionEvent event) {
        Professor professorLogado = Sessao.getInstance().getProfessorLogado();

        if (professorLogado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Acesso Negado", "Nenhum professor logado. Faça login para gerenciar atividades.");
            return;
        }

        try {
            // Carrega o FXML da tela de Atividades
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/atividade.fxml")); // Verifique o caminho correto
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gerenciar Atividades - " + professorLogado.getNmProfessor());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de gerenciamento de atividades: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de Gerenciar Atividades.");
        }
    }

    /**
     * Lida com a ação de clique no botão "Mural de Avisos".
     * Abre a nova tela do mural de avisos.
     * Requer que um professor esteja logado.
     * @param event O evento de ação.
     */
    @FXML
    private void handleGerenciarAvisos(ActionEvent event) {
        Professor professorLogado = Sessao.getInstance().getProfessorLogado();

        if (professorLogado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Acesso Negado", "Nenhum professor logado. Faça login para acessar o Mural de Avisos.");
            return;
        }

        try {
            // Carrega o FXML da nova tela de Mural de Avisos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MuralAvisos.fxml")); // Verifique o caminho correto
            Parent root = loader.load();

            // O MuralAvisosController já obtém o professor logado via Sessao.getInstance().getProfessorLogado(),
            // então não é necessário passar o objeto Professor explicitamente, a menos que haja outra lógica específica.

            Stage stage = new Stage();
            stage.setTitle("Mural de Avisos - " + professorLogado.getNmProfessor());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de Mural de Avisos: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir o Mural de Avisos.");
        }
    }

    /**
     * Lida com a ação de clique no botão "Sair".
     * Limpa a sessão do professor logado e retorna para a tela de login.
     * @param event O evento de ação (pode ser nulo se chamado internamente).
     * @throws IOException Se ocorrer um erro ao carregar a tela de login.
     */
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Sessao.getInstance().clearSession(); // Limpa todas as informações da sessão
        // Acessa o Stage atual a partir de qualquer nó da cena (aqui, lblBemVindo é usado)
        Stage currentStage = (Stage) lblBemVindo.getScene().getWindow();
        abrirTelaLogin(currentStage); // Chama o método auxiliar para abrir a tela de login
    }

    /**
     * Método auxiliar para exibir alertas para o usuário.
     * @param tipo O tipo de alerta (INFORMATION, ERROR, WARNING, etc.).
     * @param titulo O título da janela do alerta.
     * @param mensagem A mensagem principal do alerta.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Método auxiliar para centralizar a lógica de abertura da tela de login.
     * @param stage O Stage onde a nova cena será exibida.
     * @throws IOException Se o FXML de login não puder ser carregado.
     */
    private void abrirTelaLogin(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml")); // Verifique o caminho correto
        stage.setScene(new Scene(root));
        stage.setTitle("SAEP - Login");
        stage.show();
    }
}