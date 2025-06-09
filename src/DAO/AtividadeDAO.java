package DAO;

import model.Atividade;
import util.ConexaoBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AtividadeDAO {

    public static void adicionarAtividade(Atividade atividade) throws SQLException {
        // SQL ajustado para as colunas exatas: nm_atividade, ds_atividade, Turma_idTurma
        String sql = "INSERT INTO atividade (nm_atividade, ds_atividade, Turma_idTurma) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, atividade.getNmAtividade());    // Usa o novo getter
            stmt.setString(2, atividade.getDsAtividade());    // Usa o novo getter
            stmt.setInt(3, atividade.getTurma_idTurma());     // Usa o novo getter
            stmt.executeUpdate();
            System.out.println("Atividade '" + atividade.getNmAtividade() + "' adicionada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar atividade: " + e.getMessage());
            throw e;
        }
    }

    public static List<Atividade> listarAtividadesPorTurma(int idTurma) throws SQLException {
        List<Atividade> atividades = new ArrayList<>();
        // SQL ajustado para as colunas exatas: idAtividade, nm_atividade, ds_atividade, Turma_idTurma
        String sql = "SELECT idAtividade, nm_atividade, ds_atividade, Turma_idTurma FROM atividade WHERE Turma_idTurma = ?";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTurma);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Atividade atividade = new Atividade();
                    atividade.setIdAtividade(rs.getInt("idAtividade"));
                    atividade.setNmAtividade(rs.getString("nm_atividade"));     // Usa o novo setter
                    atividade.setDsAtividade(rs.getString("ds_atividade"));     // Usa o novo setter
                    atividade.setTurma_idTurma(rs.getInt("Turma_idTurma"));     // Usa o novo setter
                    // Não há professor_idProfessor em Atividade
                    atividades.add(atividade);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar atividades por turma: " + e.getMessage());
            throw e;
        }
        return atividades;
    }

    public static Atividade buscarAtividadePorId(int idAtividade) throws SQLException {
        Atividade atividade = null;
        // SQL ajustado para as colunas exatas: idAtividade, nm_atividade, ds_atividade, Turma_idTurma
        String sql = "SELECT idAtividade, nm_atividade, ds_atividade, Turma_idTurma FROM atividade WHERE idAtividade = ?";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAtividade);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    atividade = new Atividade();
                    atividade.setIdAtividade(rs.getInt("idAtividade"));
                    atividade.setNmAtividade(rs.getString("nm_atividade"));     // Usa o novo setter
                    atividade.setDsAtividade(rs.getString("ds_atividade"));     // Usa o novo setter
                    atividade.setTurma_idTurma(rs.getInt("Turma_idTurma"));     // Usa o novo setter
                    // Não há professor_idProfessor em Atividade
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar atividade por ID: " + e.getMessage());
            throw e;
        }
        return atividade;
    }

    public static void atualizarAtividade(Atividade atividade) throws SQLException {
        // SQL ajustado para as colunas exatas: nm_atividade, ds_atividade, Turma_idTurma
        String sql = "UPDATE atividade SET nm_atividade = ?, ds_atividade = ?, Turma_idTurma = ? WHERE idAtividade = ?";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, atividade.getNmAtividade());    // Usa o novo getter
            stmt.setString(2, atividade.getDsAtividade());    // Usa o novo getter
            stmt.setInt(3, atividade.getTurma_idTurma());     // Usa o novo getter
            stmt.setInt(4, atividade.getIdAtividade());
            stmt.executeUpdate();
            System.out.println("Atividade com ID " + atividade.getIdAtividade() + " atualizada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar atividade: " + e.getMessage());
            throw e;
        }
    }

    public static void excluirAtividade(int idAtividade) throws SQLException {
        String sql = "DELETE FROM atividade WHERE idAtividade = ?";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAtividade);
            stmt.executeUpdate();
            System.out.println("Atividade com ID " + idAtividade + " excluída com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao excluir atividade: " + e.getMessage());
            throw e;
        }
    }
}