package controller;

import DAO.AvisoDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea; // Importar TextArea
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Aviso;
import util.Sessao;
import model.Professor;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrincipalController {

    @FXML
    private Label lblBemVindo; // Rótulo para exibir a mensagem de boas-vindas
    @FXML
    private HBox hboxAvisosFlashcards; // Contêiner para os flashcards de avisos

    /**
     * Método de inicialização do controlador.
     * É chamado automaticamente pelo JavaFX após o carregamento do FXML.
     * Define a mensagem de boas-vindas e carrega os avisos mais recentes.
     */
    @FXML
    public void initialize() {
        Professor professorLogado = Sessao.getInstance().getProfessorLogado();
        if (professorLogado != null) {
            lblBemVindo.setText("Bem-vindo(a), " + professorLogado.getNmProfessor() + "!");
            carregarAvisosFlashcards(); // Carrega os avisos quando o professor está logado
        } else {
            lblBemVindo.setText("Bem-vindo(a)!");
            // Se, por algum motivo, não houver professor logado, redireciona para a tela de login.
            try {
                handleLogout(null);
            } catch (IOException e) {
                System.err.println("Erro ao redirecionar para login na inicialização: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Carrega os avisos mais recentes publicados pelo professor logado
     * e os exibe como "flashcards" na interface.
     */
    private void carregarAvisosFlashcards() {
        // Verifica se o HBox para os flashcards está inicializado
        if (hboxAvisosFlashcards == null) {
            System.err.println("Erro: hboxAvisosFlashcards é nulo. Verifique o FXML.");
            return;
        }

        Professor professorLogado = Sessao.getInstance().getProfessorLogado();
        if (professorLogado == null) {
            return; // Não há professor logado, não há avisos para carregar.
        }

        hboxAvisosFlashcards.getChildren().clear(); // Limpa quaisquer avisos anteriores

        try {
            // Busca os 3 avisos mais recentes do professor logado no banco de dados
            List<Aviso> ultimosAvisos = AvisoDAO.listarUltimosAvisosPorProfessor(professorLogado.getIdProfessor(), 3);

            if (ultimosAvisos.isEmpty()) {
                // Se não houver avisos, exibe uma mensagem indicando isso
                Label noAvisosLabel = new Label("Nenhum aviso recente.");
                noAvisosLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");
                hboxAvisosFlashcards.getChildren().add(noAvisosLabel);
                hboxAvisosFlashcards.setAlignment(Pos.CENTER); // Centraliza a mensagem
            } else {
                // Para cada aviso encontrado, cria um flashcard e o adiciona ao HBox
                for (Aviso aviso : ultimosAvisos) {
                    VBox flashcard = criarFlashcardAviso(aviso);
                    hboxAvisosFlashcards.getChildren().add(flashcard);
                }
                hboxAvisosFlashcards.setAlignment(Pos.CENTER); // Centraliza os flashcards
            }

        } catch (SQLException e) {
            System.err.println("Erro ao carregar avisos flashcards: " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os avisos recentes.");
            e.printStackTrace();
        }
    }

    /**
     * Cria um componente VBox que representa um "flashcard" visual para um aviso.
     * Este flashcard é clicável para exibir os detalhes completos do aviso.
     * @param aviso O objeto Aviso cujos dados serão exibidos no flashcard.
     * @return Um VBox estilizado como flashcard.
     */
    private VBox criarFlashcardAviso(Aviso aviso) {
        VBox flashcard = new VBox(5); // VBox com espaçamento de 5px entre os filhos
        flashcard.getStyleClass().add("flashcard-aviso"); // Aplica a classe CSS
        flashcard.setAlignment(Pos.CENTER); // Alinha o conteúdo ao centro

        Label titleLabel = new Label(aviso.getTitulo());
        titleLabel.getStyleClass().add("flashcard-title"); // Aplica a classe CSS para o título

        String dataFormatada = "";
        if (aviso.getDataPublicacao() != null) {
            // Formata a data para exibição no flashcard
            dataFormatada = aviso.getDataPublicacao().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        }
        Label dateLabel = new Label(dataFormatada);
        dateLabel.getStyleClass().add("flashcard-date"); // Aplica a classe CSS para a data

        flashcard.getChildren().addAll(titleLabel, dateLabel); // Adiciona título e data ao flashcard

        // Define a ação de clique para o flashcard: exibir detalhes do aviso
        flashcard.setOnMouseClicked(event -> mostrarDetalhesAviso(aviso));

        return flashcard;
    }

    /**
     * Exibe os detalhes completos de um aviso em uma janela de alerta (pop-up).
     * @param aviso O objeto Aviso cujos detalhes serão mostrados.
     */
    private void mostrarDetalhesAviso(Aviso aviso) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalhes do Aviso");
        alert.setHeaderText(aviso.getTitulo());

        // Usa um TextArea para exibir o conteúdo, permitindo quebras de linha e rolagem se necessário
        TextArea contentArea = new TextArea(aviso.getConteudo());
        contentArea.setEditable(false); // Impede edição do conteúdo
        contentArea.setWrapText(true); // Ativa quebra de linha automática
        contentArea.setPrefRowCount(8); // Define uma altura preferencial em número de linhas
        contentArea.setMaxWidth(Double.MAX_VALUE);
        contentArea.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setContent(contentArea); // Define o TextArea como conteúdo do alerta
        alert.setResizable(true); // Permite que o usuário redimensione o pop-up
        alert.showAndWait(); // Exibe o pop-up e aguarda a interação do usuário
    }

    /**
     * Lida com a ação de clique no botão "Gerenciar Turmas".
     * Abre a tela de gerenciamento de turmas, verificando antes se há um professor logado.
     * @param event O evento de ação (não usado diretamente, mas parte da assinatura do método).
     */
    @FXML
    private void handleCadastrarTurma(ActionEvent event) {
        Professor professorLogado = Sessao.getInstance().getProfessorLogado();

        if (professorLogado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Acesso Negado", "Nenhum professor logado. Faça login para gerenciar turmas.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/cadastroturma.fxml"));
            Parent root = loader.load();

            CDTurmaController turmaController = loader.getController();
            turmaController.setProfessorLogado(professorLogado);

            Stage stage = new Stage();
            stage.setTitle("Gerenciar Turmas - SAEP");
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
     * Abre a tela de gerenciamento de atividades, verificando antes se há um professor logado.
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/atividade.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gerenciar Atividades - SAEP");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de gerenciamento de atividades: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de Gerenciar Atividades.");
        }
    }

    /**
     * Lida com a ação de clique no botão "Mural de Avisos Completo".
     * Abre a tela completa do mural de avisos, verificando antes se há um professor logado.
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MuralAvisos.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Mural de Avisos - SAEP");
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
     * Limpa a sessão do professor logado e redireciona para a tela de login.
     * @param event O evento de ação (pode ser nulo se chamado internamente).
     * @throws IOException Se ocorrer um erro ao carregar a tela de login.
     */
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Sessao.getInstance().clearSession(); // Limpa todas as informações da sessão
        // Obtém o Stage atual a partir de qualquer nó da cena (aqui, lblBemVindo)
        Stage currentStage = (Stage) lblBemVindo.getScene().getWindow();
        abrirTelaLogin(currentStage); // Redireciona para a tela de login
    }

    /**
     * Método auxiliar para exibir alertas informativos, de erro ou aviso para o usuário.
     * @param tipo O tipo de alerta (Alert.AlertType.INFORMATION, ERROR, WARNING, etc.).
     * @param titulo O título da janela do alerta.
     * @param mensagem A mensagem principal a ser exibida no alerta.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Método auxiliar para encapsular a lógica de abertura da tela de login.
     * @param stage O Stage onde a nova cena de login será exibida.
     * @throws IOException Se o arquivo FXML de login não puder ser carregado.
     */
    private void abrirTelaLogin(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("SAEP - Login");
        stage.show();
    }
}