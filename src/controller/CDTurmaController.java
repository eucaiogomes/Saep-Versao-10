package controller;

import DAO.ProfessorDAO; // Ainda pode ser necessário para outras funcionalidades, mas não para a ComboBox
import DAO.TurmaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox; // Remova se você não tiver mais ComboBox alguma no FXML
import javafx.scene.control.Label; // Adicione se for usar um Label para o nome do professor
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Professor;
import model.Turma;
import util.Sessao;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CDTurmaController implements Initializable {

    @FXML
    private TextField txtNomeTurma;
    @FXML
    private TextField txtDescricaoTurma;
    // @FXML private ComboBox<Professor> cmbProfessorResponsavel; // <-- REMOVA OU COMENTE ESTA LINHA
    @FXML
    private ListView<Turma> listViewTurmas;
    @FXML
    private Label lblProfessorResponsavel; // <-- OPCIONAL: Adicione se quiser mostrar o nome do professor logado

    private ObservableList<Turma> observableListTurmas;
    // private ObservableList<Professor> observableListProfessores; // <-- REMOVA OU COMENTE ESTA LINHA

    private Professor professorLogado; // Atributo para armazenar o professor logado

    /**
     * MÉTODO ESSENCIAL: Chamado pelo PrincipalController para injetar o Professor logado.
     * @param professor O objeto Professor que está logado na sessão.
     */
    public void setProfessorLogado(Professor professor) {
        this.professorLogado = professor;
        if (lblProfessorResponsavel != null) { // Atualiza o Label se ele existir
            lblProfessorResponsavel.setText("Professor: " + professorLogado.getNmProfessor());
        }
        carregarTurmas(); // Carrega as turmas DELE
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        observableListTurmas = FXCollections.observableArrayList();
        listViewTurmas.setItems(observableListTurmas);

        // observableListProfessores = FXCollections.observableArrayList(); // <-- REMOVA OU COMENTE ESTA LINHA
        // cmbProfessorResponsavel.setItems(observableListProfessores);     // <-- REMOVA OU COMENTE ESTA LINHA

        // carregarProfessores(); // <-- REMOVA OU COMENTE ESTA CHAMADA

        // Listener para preencher os campos quando uma turma é selecionada
        listViewTurmas.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    txtNomeTurma.setText(newValue.getNmTurma());
                    txtDescricaoTurma.setText(newValue.getDsTurma());
                    // Não é mais necessário selecionar o professor na ComboBox
                } else {
                    limparCampos();
                }
            });

        // carregarTurmas() NÃO é chamado aqui, ele será chamado pelo setProfessorLogado().
    }

    /**
     * Carrega APENAS as turmas do professor logado.
     * Este método é chamado somente depois que o professorLogado é definido.
     */
    private void carregarTurmas() {
        if (professorLogado == null) {
            System.out.println("Professor logado não definido. Não é possível carregar turmas.");
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Carregamento", "Professor não identificado. Faça login novamente.");
            return;
        }
        try {
            observableListTurmas.clear();
            observableListTurmas.addAll(TurmaDAO.listarTurmasPorProfessor(professorLogado.getIdProfessor()));
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar turmas do professor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Este método não é mais necessário se a ComboBox de professores for removida.
     */
    // private void carregarProfessores() {
    //     try {
    //         observableListProfessores.clear();
    //         observableListProfessores.addAll(ProfessorDAO.listarProfessores());
    //     } catch (SQLException e) {
    //         mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar professores: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }

    @FXML
    private void handleAdicionarTurma(ActionEvent event) {
        // A validação de cmbProfessorResponsavel.getSelectionModel().getSelectedItem() não é mais necessária
        if (validarCamposSemProfessorComboBox()) { // Novo método de validação
            if (professorLogado == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Professor logado não identificado para adicionar turma.");
                return;
            }

            // A turma é sempre atribuída ao professor logado
            Turma turma = new Turma(
                    txtNomeTurma.getText(),
                    txtDescricaoTurma.getText(),
                    professorLogado // O professor responsável é o professor logado
            );
            try {
                TurmaDAO.inserirTurma(turma);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Turma adicionada com sucesso!");
                limparCampos();
                carregarTurmas(); // Recarrega as turmas do professor logado
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao adicionar turma: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAtualizarTurma(ActionEvent event) {
        Turma turmaSelecionada = listViewTurmas.getSelectionModel().getSelectedItem();
        if (turmaSelecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma turma para atualizar.");
            return;
        }

        // Não precisa mais validar cmbProfessorResponsavel
        if (validarCamposSemProfessorComboBox()) { // Novo método de validação

            // --- REGRA DE NEGÓCIO: Só permite atualizar as próprias turmas ---
            // Esta validação já estava no lugar e é crucial
            if (professorLogado == null || turmaSelecionada.getProfessor_idProfessor() != professorLogado.getIdProfessor()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Você não pode atualizar turmas que não são suas.");
                return;
            }

            turmaSelecionada.setNmTurma(txtNomeTurma.getText());
            turmaSelecionada.setDsTurma(txtDescricaoTurma.getText());
            // O professor da turma atualizada continua sendo o professor logado
            turmaSelecionada.setProfessor(professorLogado);

            try {
                TurmaDAO.atualizarTurma(turmaSelecionada);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Turma atualizada com sucesso!");
                limparCampos();
                carregarTurmas(); // Recarrega as turmas do professor logado
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar turma: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExcluirTurma(ActionEvent event) {
        Turma turmaSelecionada = listViewTurmas.getSelectionModel().getSelectedItem();
        if (turmaSelecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma turma para excluir.");
            return;
        }

        // --- REGRA DE NEGÓCIO: Só permite excluir as próprias turmas ---
        // Esta validação já estava no lugar e é crucial
        if (professorLogado == null || turmaSelecionada.getProfessor_idProfessor() != professorLogado.getIdProfessor()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Você não pode excluir turmas que não são suas.");
            return;
        }

        try {
            TurmaDAO.excluirTurma(turmaSelecionada.getIdTurma());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Turma excluída com sucesso!");
            limparCampos();
            carregarTurmas(); // Recarrega as turmas do professor logado
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir turma: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Novo método de validação sem a ComboBox de professor
    private boolean validarCamposSemProfessorComboBox() {
        if (txtNomeTurma.getText().isEmpty() || txtDescricaoTurma.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Nome e Descrição da turma são obrigatórios.");
            return false;
        }
        return true;
    }

    private void limparCampos() {
        txtNomeTurma.clear();
        txtDescricaoTurma.clear();
        // cmbProfessorResponsavel.getSelectionModel().clearSelection(); // <-- REMOVA OU COMENTE ESTA LINHA
        listViewTurmas.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}