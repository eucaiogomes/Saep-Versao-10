package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Professor;
import model.Turma;
import util.ConexaoBD;

public class TurmaDAO {

    // --- Métodos CRUD básicos para Turma (já existentes) ---
    // Mantenha seus métodos inserirTurma, buscarTurmaPorId, listarTurma, excluirTurma, atualizarTurma aqui
    // Exemplo de como devem estar, sem as colunas cpf/disciplina no model.Professor:

    public static void inserirTurma(Turma turma) throws SQLException {
        String sql = "INSERT INTO turma (nm_turma, ds_turma, Professor_idProfessor) VALUES (?, ?, ?)";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, turma.getNmTurma());
            stmt.setString(2, turma.getDsTurma());
            stmt.setInt(3, turma.getProfessor_idProfessor());
            stmt.executeUpdate();
            System.out.println("Turma " + turma.getNmTurma() + " adicionada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar turma: " + e.getMessage());
            throw e;
        }
    }

    public static Turma buscarTurmaPorId(int idTurma) throws SQLException {
        Turma turma = null;
        // SELECT para buscar dados da turma e do professor associado
        String sql = "SELECT t.idTurma, t.nm_turma, t.ds_turma, t.Professor_idProfessor, " +
                     "p.idProfessor, p.nm_professor, p.email_professor, p.senha_professor " + // Removido cpf_professor, disciplina_professor
                     "FROM turma t JOIN professor p ON t.Professor_idProfessor = p.idProfessor WHERE t.idTurma = ?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idTurma);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Professor professorAssociado = new Professor(
                        rs.getInt("idProfessor"),
                        rs.getString("nm_professor"),
                        rs.getString("email_professor"),
                        rs.getString("senha_professor")
                        // Não há cpf_professor e disciplina_professor no construtor
                    );
                    turma = new Turma(
                        rs.getInt("idTurma"),
                        rs.getString("nm_turma"),
                        rs.getString("ds_turma"),
                        professorAssociado
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar turma por ID: " + e.getMessage());
            throw e;
        }
        return turma;
    }

    public static List<Turma> listarTurma() throws SQLException {
        List<Turma> listaTurmas = new ArrayList<>();
        // SELECT para buscar dados de todas as turmas e seus professores associados
        String sql = "SELECT t.idTurma, t.nm_turma, t.ds_turma, t.Professor_idProfessor, " +
                     "p.idProfessor, p.nm_professor, p.email_professor, p.senha_professor " + // Removido cpf_professor, disciplina_professor
                     "FROM turma t JOIN professor p ON t.Professor_idProfessor = p.idProfessor";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Professor professorAssociado = new Professor(
                    rs.getInt("idProfessor"),
                    rs.getString("nm_professor"),
                    rs.getString("email_professor"),
                    rs.getString("senha_professor")
                    // Não há cpf_professor e disciplina_professor no construtor
                );
                Turma turma = new Turma(
                    rs.getInt("idTurma"),
                    rs.getString("nm_turma"),
                    rs.getString("ds_turma"),
                    professorAssociado
                );
                listaTurmas.add(turma);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar turmas: " + e.getMessage());
            throw e;
        }
        return listaTurmas;
    }

    public static void atualizarTurma(Turma turma) throws SQLException {
        String sql = "UPDATE turma SET nm_turma = ?, ds_turma = ?, Professor_idProfessor = ? WHERE idTurma = ?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, turma.getNmTurma());
            stmt.setString(2, turma.getDsTurma());
            stmt.setInt(3, turma.getProfessor_idProfessor());
            stmt.setInt(4, turma.getIdTurma());
            stmt.executeUpdate();
            System.out.println("Turma " + turma.getNmTurma() + " atualizada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar turma: " + e.getMessage());
            throw e;
        }
    }

    public static void excluirTurma(int idTurma) throws SQLException {
        String sql = "DELETE FROM turma WHERE idTurma = ?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idTurma);
            stmt.executeUpdate();
            System.out.println("Turma com ID " + idTurma + " excluída com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao excluir turma: " + e.getMessage());
            throw e;
        }
    }

    // --- NOVO MÉTODO: Listar Turmas por Professor (CORRIGIDO) ---
    /**
     * Lista todas as turmas associadas a um professor específico.
     * Inclui os dados do professor para cada turma.
     *
     * @param idProfessor O ID do professor para filtrar as turmas.
     * @return Uma lista de objetos Turma.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public static List<Turma> listarTurmasPorProfessor(int idProfessor) throws SQLException {
        List<Turma> listaTurmas = new ArrayList<>();
        // SQL para buscar turmas e fazer um JOIN com a tabela de professor.
        // Removidas as colunas cpf_professor e disciplina_professor do SELECT do professor.
        String sql = "SELECT t.idTurma, t.nm_turma, t.ds_turma, t.Professor_idProfessor, " +
                     "p.idProfessor, p.nm_professor, p.email_professor, p.senha_professor " +
                     "FROM turma t " +
                     "JOIN professor p ON t.Professor_idProfessor = p.idProfessor " +
                     "WHERE t.Professor_idProfessor = ?"; // Filtra pelo ID do professor

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, idProfessor); // Define o ID do professor no PreparedStatement

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Professor professorAssociado = new Professor(
                        rs.getInt("idProfessor"),
                        rs.getString("nm_professor"),
                        rs.getString("email_professor"),
                        rs.getString("senha_professor")
                    );

                    Turma turma = new Turma(
                        rs.getInt("idTurma"),
                        rs.getString("nm_turma"),
                        rs.getString("ds_turma"),
                        professorAssociado // Passa o objeto Professor completo
                    );
                    listaTurmas.add(turma);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar turmas por professor: " + e.getMessage());
            throw e;
        }
        return listaTurmas;
    }
}