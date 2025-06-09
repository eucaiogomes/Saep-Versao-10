package controller;

import DAO.ProfessorDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Professor;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CDProfessorController implements Initializable {

    @FXML
    private TextField txtNomeProfessor;
    @FXML
    private TextField txtEmailProfessor;
    @FXML
    private TextField txtSenhaProfessor;
    // Removidos os FXML para txtCpfProfessor e txtDisciplinaProfessor
    // @FXML
    // private TextField txtCpfProfessor;
    // @FXML
    // private TextField txtDisciplinaProfessor;
    @FXML
    private ListView<Professor> listViewProfessores;

    private ObservableList<Professor> observableListProfessores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        observableListProfessores = FXCollections.observableArrayList();
        listViewProfessores.setItems(observableListProfessores);
        carregarProfessores();

        // Listener para preencher os campos quando um professor é selecionado na lista
        listViewProfessores.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    txtNomeProfessor.setText(newValue.getNmProfessor());
                    txtEmailProfessor.setText(newValue.getEmailProfessor());
                    txtSenhaProfessor.setText(newValue.getSenhaProfessor()); // Cuidado com senhas em UI
                    // Removidas as linhas para CPF e Disciplina
                    // txtCpfProfessor.setText(newValue.getCpfProfessor());
                    // txtDisciplinaProfessor.setText(newValue.getDisciplinaProfessor());
                } else {
                    limparCampos();
                }
            });
    }

    private void carregarProfessores() {
        try {
            observableListProfessores.clear();
            observableListProfessores.addAll(ProfessorDAO.listarProfessores());
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar professores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdicionarProfessor(ActionEvent event) {
        if (validarCampos()) {
            Professor professor = new Professor(
                    txtNomeProfessor.getText(),
                    txtEmailProfessor.getText(),
                    txtSenhaProfessor.getText(),
                    // CPF e Disciplina agora são passados como null ou string vazia,
                    // dependendo do que o construtor do Professor aceita e como o DAO lida com isso.
                    // Se você não quer que eles sejam persistidos no banco, o DAO precisa ser ajustado.
                    // Assumindo que você quer apenas não preenchê-los na UI, mantemos o construtor
                    // e passamos valores padrão.
                    null, // Ou "" para CPF
                    null  // Ou "" para Disciplina
            );
            try {
                ProfessorDAO.adicionarProfessor(professor);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Professor adicionado com sucesso!");
                limparCampos();
                carregarProfessores();
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao adicionar professor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAtualizarProfessor(ActionEvent event) {
        Professor professorSelecionado = listViewProfessores.getSelectionModel().getSelectedItem();
        if (professorSelecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione um professor para atualizar.");
            return;
        }

        if (validarCampos()) {
            professorSelecionado.setNmProfessor(txtNomeProfessor.getText());
            professorSelecionado.setEmailProfessor(txtEmailProfessor.getText());
            professorSelecionado.setSenhaProfessor(txtSenhaProfessor.getText());
            // Não estamos pegando valores para CPF e Disciplina da UI,
            // então eles permanecerão com os valores existentes no objeto professorSelecionado (carregados do banco).
            // Se você quer "apagar" esses campos ao atualizar por esta tela,
            // você precisaria setá-los explicitamente para null ou "".
            // professorSelecionado.setCpfProfessor(null);
            // professorSelecionado.setDisciplinaProfessor(null);

            try {
                ProfessorDAO.atualizarProfessor(professorSelecionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Professor atualizado com sucesso!");
                limparCampos();
                carregarProfessores();
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar professor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExcluirProfessor(ActionEvent event) {
        Professor professorSelecionado = listViewProfessores.getSelectionModel().getSelectedItem();
        if (professorSelecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione um professor para excluir.");
            return;
        }

        try {
            ProfessorDAO.excluirProfessor(professorSelecionado.getIdProfessor());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Professor excluído com sucesso!");
            limparCampos();
            carregarProfessores();
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir professor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        // Validação agora só para Nome, Email e Senha
        if (txtNomeProfessor.getText().isEmpty() ||
            txtEmailProfessor.getText().isEmpty() ||
            txtSenhaProfessor.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Nome, Email e Senha devem ser preenchidos.");
            return false;
        }
        return true;
    }

    private void limparCampos() {
        txtNomeProfessor.clear();
        txtEmailProfessor.clear();
        txtSenhaProfessor.clear();
        // Removidas as chamadas para limpar CPF e Disciplina
        // txtCpfProfessor.clear();
        // txtDisciplinaProfessor.clear();
        listViewProfessores.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}