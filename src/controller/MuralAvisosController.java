package controller;

import DAO.AvisoDAO;
import DAO.TurmaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Aviso;
import model.Professor;
import model.Turma;
import util.Sessao;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MuralAvisosController implements Initializable {

    @FXML
    private TextField txtTituloAviso;
    @FXML
    private TextArea txtConteudoAviso;
    @FXML
    private ComboBox<Turma> cmbTurmaAviso;
    @FXML
    private ListView<Aviso> listViewAvisos;

    private ObservableList<Aviso> observableListAvisos;
    private ObservableList<Turma> observableListTurmas;

    private Professor professorLogado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        professorLogado = Sessao.getInstance().getProfessorLogado();

        observableListAvisos = FXCollections.observableArrayList();
        listViewAvisos.setItems(observableListAvisos);

        observableListTurmas = FXCollections.observableArrayList();
        cmbTurmaAviso.setItems(observableListTurmas);

        carregarTurmasDoProfessorLogado();

        // Adiciona um listener para a seleção da ComboBox de Turmas
        cmbTurmaAviso.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Chama o método para carregar avisos da nova turma
                    handleTurmaSelecionada(new ActionEvent());
                } else {
                    observableListAvisos.clear(); // Limpa a lista se nenhuma turma estiver selecionada
                    limparCampos(); // Limpa os campos de texto
                }
            }
        );

        // Adiciona um listener para a seleção da ListView de Avisos
        listViewAvisos.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    txtTituloAviso.setText(newValue.getTitulo());
                    txtConteudoAviso.setText(newValue.getConteudo());
                } else {
                    limparCampos();
                }
            }
        );

        // Opcional: Seleciona a primeira turma e carrega seus avisos ao iniciar a tela
        if (!observableListTurmas.isEmpty()) {
            cmbTurmaAviso.getSelectionModel().selectFirst();
        }
    }

    private void carregarTurmasDoProfessorLogado() {
        if (professorLogado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Acesso", "Professor logado não encontrado. Faça login novamente.");
            return;
        }
        try {
            observableListTurmas.clear();
            // Usa o método do TurmaDAO para listar apenas as turmas do professor logado
            observableListTurmas.addAll(TurmaDAO.listarTurmasPorProfessor(professorLogado.getIdProfessor()));
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar turmas do professor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTurmaSelecionada(ActionEvent event) {
        Turma turmaSelecionada = cmbTurmaAviso.getSelectionModel().getSelectedItem();
        if (turmaSelecionada != null) {
            // Regra de Negócio: Garante que a turma selecionada realmente pertence ao professor logado
            if (professorLogado == null || turmaSelecionada.getProfessor_idProfessor() != professorLogado.getIdProfessor()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "A turma selecionada não pertence ao seu perfil de professor.");
                observableListAvisos.clear(); // Limpa avisos
                cmbTurmaAviso.getSelectionModel().clearSelection(); // Limpa seleção da turma
                return;
            }

            try {
                observableListAvisos.clear();
                // Carrega avisos APENAS da turma selecionada
                observableListAvisos.addAll(AvisoDAO.listarAvisosPorTurma(turmaSelecionada.getIdTurma()));
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar avisos da turma: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            observableListAvisos.clear(); // Limpa a lista se nenhuma turma estiver selecionada
        }
    }

    @FXML
    private void handleAdicionarAviso(ActionEvent event) {
        if (validarCampos()) {
            Turma turmaSelecionada = cmbTurmaAviso.getSelectionModel().getSelectedItem();
            if (turmaSelecionada == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma turma para o aviso.");
                return;
            }
            if (professorLogado == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Professor logado não encontrado. Faça login novamente.");
                return;
            }

            // Regra de Negócio: Apenas permite adicionar aviso às próprias turmas
            if (turmaSelecionada.getProfessor_idProfessor() != professorLogado.getIdProfessor()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Você só pode adicionar avisos às suas próprias turmas.");
                return;
            }

            Aviso aviso = new Aviso(
                txtTituloAviso.getText(),
                txtConteudoAviso.getText(),
                turmaSelecionada.getIdTurma(),
                professorLogado.getIdProfessor()
            );

            try {
                AvisoDAO.inserirAviso(aviso);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Aviso publicado com sucesso!");
                limparCampos();
                handleTurmaSelecionada(null); // Recarrega os avisos da turma atual
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao publicar aviso: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAtualizarAviso(ActionEvent event) {
        Aviso avisoSelecionado = listViewAvisos.getSelectionModel().getSelectedItem();
        if (avisoSelecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione um aviso para atualizar.");
            return;
        }

        if (validarCampos()) {
            Turma turmaSelecionada = cmbTurmaAviso.getSelectionModel().getSelectedItem();
            if (turmaSelecionada == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma turma para o aviso.");
                return;
            }
            if (professorLogado == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Professor logado não encontrado. Faça login novamente.");
                return;
            }

            // Regras de Negócio: Apenas permite atualizar avisos de suas próprias turmas e que você criou
            if (turmaSelecionada.getProfessor_idProfessor() != professorLogado.getIdProfessor() ||
                avisoSelecionado.getProfessor_idProfessor() != professorLogado.getIdProfessor() ||
                avisoSelecionado.getTurma_idTurma() != turmaSelecionada.getIdTurma()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Você só pode atualizar avisos de suas próprias turmas ou avisos que você publicou.");
                return;
            }

            avisoSelecionado.setTitulo(txtTituloAviso.getText());
            avisoSelecionado.setConteudo(txtConteudoAviso.getText());
            avisoSelecionado.setTurma_idTurma(turmaSelecionada.getIdTurma());
            // O professor do aviso permanece o mesmo (o logado)

            try {
                AvisoDAO.atualizarAviso(avisoSelecionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Aviso atualizado com sucesso!");
                limparCampos();
                handleTurmaSelecionada(null); // Recarrega os avisos da turma atual
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar aviso: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExcluirAviso(ActionEvent event) {
        Aviso avisoSelecionado = listViewAvisos.getSelectionModel().getSelectedItem();
        if (avisoSelecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione um aviso para excluir.");
            return;
        }

        if (professorLogado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Professor logado não encontrado. Faça login novamente.");
            return;
        }

        // Regra de Negócio: Apenas permite excluir avisos de suas próprias turmas e que você criou
        try {
            Turma turmaDoAviso = TurmaDAO.buscarTurmaPorId(avisoSelecionado.getTurma_idTurma());
            if (turmaDoAviso == null || turmaDoAviso.getProfessor_idProfessor() != professorLogado.getIdProfessor() ||
                avisoSelecionado.getProfessor_idProfessor() != professorLogado.getIdProfessor()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Você só pode excluir avisos de suas próprias turmas ou avisos que você publicou.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar turma do aviso para exclusão: " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao verificar permissão para exclusão.");
            return;
        }

        try {
            AvisoDAO.excluirAviso(avisoSelecionado.getIdAviso());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Aviso excluído com sucesso!");
            limparCampos();
            handleTurmaSelecionada(null); // Recarrega os avisos da turma atual
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir aviso: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLimparCampos(ActionEvent event) {
        limparCampos();
    }

    private boolean validarCampos() {
        if (txtTituloAviso.getText().isEmpty() || txtConteudoAviso.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Título e Conteúdo do aviso são obrigatórios.");
            return false;
        }
        if (cmbTurmaAviso.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Selecione uma turma para o aviso.");
            return false;
        }
        return true;
    }

    private void limparCampos() {
        txtTituloAviso.clear();
        txtConteudoAviso.clear();
        // Não limpa a seleção da turma, para facilitar a adição de vários avisos na mesma turma
        // cmbTurmaAviso.getSelectionModel().clearSelection();
        listViewAvisos.getSelectionModel().clearSelection(); // Desseleciona item da lista de avisos
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}