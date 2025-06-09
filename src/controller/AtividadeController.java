package controller;

import DAO.AtividadeDAO;
import DAO.TurmaDAO; // Precisaremos para listar turmas do professor
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea; // Certifique-se de que é TextArea se você usa para descrição
import model.Atividade;
import model.Professor;
import model.Turma;
import util.Sessao;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AtividadeController implements Initializable {

    @FXML
    private TextField txtNomeAtividade;
    @FXML
    private TextField txtDescricaoAtividade; // Ou TextArea, dependendo do seu FXML
    @FXML
    private ComboBox<Turma> cmbTurmaAtividade;
    @FXML
    private ListView<Atividade> listViewAtividades;

    private ObservableList<Atividade> observableListAtividades;
    private ObservableList<Turma> observableListTurmas;

    private Professor professorLogado; // O professor logado via Sessao

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // --- ATENÇÃO AQUI: Obtendo o professor logado via Singleton ---
        professorLogado = Sessao.getInstance().getProfessorLogado();

        observableListAtividades = FXCollections.observableArrayList();
        listViewAtividades.setItems(observableListAtividades);

        observableListTurmas = FXCollections.observableArrayList();
        cmbTurmaAtividade.setItems(observableListTurmas);

        // Carrega APENAS as turmas do professor logado
        carregarTurmasDoProfessorLogado();

        // Adiciona um listener para a ComboBox de Turmas
        // Este listener agora substituirá a chamada direta a carregarAtividades() no initialize
        cmbTurmaAtividade.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    handleTurmaSelecionada(new ActionEvent()); // Chama o método para carregar as atividades da nova turma
                } else {
                    observableListAtividades.clear(); // Limpa a lista se nenhuma turma estiver selecionada
                    limparCampos(); // Limpa também os campos de texto
                }
            }
        );

        // Listener para preencher os campos quando uma atividade é selecionada
        listViewAtividades.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    txtNomeAtividade.setText(newValue.getNmAtividade());
                    txtDescricaoAtividade.setText(newValue.getDsAtividade());
                    // A ComboBox já deve estar com a turma selecionada pela lógica anterior
                } else {
                    limparCampos();
                }
            });

        // Opcional: Seleciona a primeira turma e carrega suas atividades na inicialização
        if (!observableListTurmas.isEmpty()) {
            cmbTurmaAtividade.getSelectionModel().selectFirst();
        }
    }

    /**
     * Carrega APENAS as turmas do professor logado na ComboBox.
     */
    private void carregarTurmasDoProfessorLogado() {
        if (professorLogado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Acesso", "Professor logado não encontrado. Faça login novamente.");
            return;
        }
        try {
            observableListTurmas.clear();
            // --- AQUI: Usa o método específico do DAO para turmas por professor ---
            observableListTurmas.addAll(TurmaDAO.listarTurmasPorProfessor(professorLogado.getIdProfessor()));
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar turmas do professor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Este método agora é chamado quando uma turma é selecionada na ComboBox.
     * Ele carrega as atividades da turma selecionada.
     */
    @FXML
    private void handleTurmaSelecionada(ActionEvent event) {
        Turma turmaSelecionada = cmbTurmaAtividade.getSelectionModel().getSelectedItem();
        if (turmaSelecionada != null) {
            // Regra de Negócio: Garante que a turma selecionada realmente pertence ao professor logado
            if (professorLogado == null || turmaSelecionada.getProfessor_idProfessor() != professorLogado.getIdProfessor()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "A turma selecionada não pertence ao seu perfil de professor.");
                observableListAtividades.clear(); // Limpa atividades
                cmbTurmaAtividade.getSelectionModel().clearSelection(); // Limpa seleção da turma
                return;
            }

            try {
                observableListAtividades.clear();
                // --- AQUI: Carrega atividades APENAS da turma selecionada ---
                observableListAtividades.addAll(AtividadeDAO.listarAtividadesPorTurma(turmaSelecionada.getIdTurma()));
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar atividades da turma: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            observableListAtividades.clear(); // Limpa a lista se nenhuma turma estiver selecionada
        }
    }

    @FXML
    private void handleAdicionarAtividade(ActionEvent event) {
        if (validarCampos()) {
            Turma turmaSelecionada = cmbTurmaAtividade.getSelectionModel().getSelectedItem();
            if (turmaSelecionada == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma turma para a atividade.");
                return;
            }
            if (professorLogado == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Professor logado não encontrado. Faça login novamente.");
                return;
            }

            // --- REGRA DE NEGÓCIO: Apenas permite adicionar atividades às próprias turmas ---
            if (turmaSelecionada.getProfessor_idProfessor() != professorLogado.getIdProfessor()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Você só pode adicionar atividades às suas próprias turmas.");
                return;
            }

            Atividade atividade = new Atividade(
                    txtNomeAtividade.getText(),
                    txtDescricaoAtividade.getText(),
                    turmaSelecionada.getIdTurma(),
                    professorLogado.getIdProfessor() // Associa a atividade ao professor logado
            );
            try {
                AtividadeDAO.adicionarAtividade(atividade);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Atividade adicionada com sucesso!");
                limparCampos();
                // Recarrega as atividades da turma atualmente selecionada
                handleTurmaSelecionada(null);
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao adicionar atividade: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAtualizarAtividade(ActionEvent event) {
        Atividade atividadeSelecionada = listViewAtividades.getSelectionModel().getSelectedItem();
        if (atividadeSelecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma atividade para atualizar.");
            return;
        }

        if (validarCampos()) {
            Turma turmaSelecionada = cmbTurmaAtividade.getSelectionModel().getSelectedItem();
            if (turmaSelecionada == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma turma para a atividade.");
                return;
            }
            if (professorLogado == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Professor logado não encontrado. Faça login novamente.");
                return;
            }

            // --- REGRA DE NEGÓCIO: Apenas permite atualizar atividades de suas próprias turmas ---
            // E também que a atividade selecionada pertença à turma do professor logado.
            if (turmaSelecionada.getProfessor_idProfessor() != professorLogado.getIdProfessor() ||
                atividadeSelecionada.getProfessor_idProfessor() != professorLogado.getIdProfessor() ||
                atividadeSelecionada.getTurma_idTurma() != turmaSelecionada.getIdTurma() ) { // Verifica se a atividade pertence à turma selecionada
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Você só pode atualizar atividades de suas próprias turmas.");
                return;
            }

            atividadeSelecionada.setNmAtividade(txtNomeAtividade.getText());
            atividadeSelecionada.setDsAtividade(txtDescricaoAtividade.getText());
            atividadeSelecionada.setTurma_idTurma(turmaSelecionada.getIdTurma());
            atividadeSelecionada.setProfessor_idProfessor(professorLogado.getIdProfessor()); // Garante que continua associada ao professor logado

            try {
                AtividadeDAO.atualizarAtividade(atividadeSelecionada);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Atividade atualizada com sucesso!");
                limparCampos();
                handleTurmaSelecionada(null); // Recarrega a lista da turma
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar atividade: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExcluirAtividade(ActionEvent event) {
        Atividade atividadeSelecionada = listViewAtividades.getSelectionModel().getSelectedItem();
        if (atividadeSelecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma atividade para excluir.");
            return;
        }

        if (professorLogado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Professor logado não encontrado. Faça login novamente.");
            return;
        }

        // --- REGRA DE NEGÓCIO: Apenas permite excluir atividades de suas próprias turmas ---
        // Pega a turma da atividade selecionada para verificar se pertence ao professor logado
        Turma turmaDaAtividade = null;
        try {
            turmaDaAtividade = TurmaDAO.buscarTurmaPorId(atividadeSelecionada.getTurma_idTurma());
        } catch (SQLException e) {
            System.err.println("Erro ao buscar turma da atividade para validação: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível verificar a propriedade da atividade.");
            return;
        }

        if (turmaDaAtividade == null || turmaDaAtividade.getProfessor_idProfessor() != professorLogado.getIdProfessor() ||
            atividadeSelecionada.getProfessor_idProfessor() != professorLogado.getIdProfessor()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Você só pode excluir atividades de suas próprias turmas.");
            return;
        }

        try {
            AtividadeDAO.excluirAtividade(atividadeSelecionada.getIdAtividade());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Atividade excluída com sucesso!");
            limparCampos();
            handleTurmaSelecionada(null); // Recarrega a lista da turma
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir atividade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (txtNomeAtividade.getText().isEmpty() || txtDescricaoAtividade.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Nome e Descrição da atividade são obrigatórios.");
            return false;
        }
        if (cmbTurmaAtividade.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Selecione uma turma para a atividade.");
            return false;
        }
        return true;
    }

    private void limparCampos() {
        txtNomeAtividade.clear();
        txtDescricaoAtividade.clear();
        // Não limpa a seleção da turma, pois o usuário pode querer adicionar outra atividade na mesma turma
        // cmbTurmaAtividade.getSelectionModel().clearSelection();
        listViewAtividades.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}