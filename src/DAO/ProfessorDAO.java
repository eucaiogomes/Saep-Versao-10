package DAO;

import model.Professor;
import util.ConexaoBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDAO {

    public static void adicionarProfessor(Professor professor) throws SQLException {
        // SQL ajustado para as colunas: nm_professor, email_professor, senha_professor
        // Assumindo que cpf_professor e disciplina_professor NÃO existem mais na tabela OU são NULLable
        String sql = "INSERT INTO professor (nm_professor, email_professor, senha_professor) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, professor.getNmProfessor());
            stmt.setString(2, professor.getEmailProfessor());
            stmt.setString(3, professor.getSenhaProfessor());
            stmt.executeUpdate();
            System.out.println("Professor " + professor.getNmProfessor() + " adicionado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar professor: " + e.getMessage());
            throw e;
        }
    }

    public static Professor buscarProfessorPorEmailESenha(String email, String senha) throws SQLException {
        // SQL ajustado para as colunas: idProfessor, nm_professor, email_professor, senha_professor
        String sql = "SELECT idProfessor, nm_professor, email_professor, senha_professor FROM professor WHERE email_professor = ? AND senha_professor = ?";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Professor professor = new Professor();
                    professor.setIdProfessor(rs.getInt("idProfessor"));
                    professor.setNmProfessor(rs.getString("nm_professor"));
                    professor.setEmailProfessor(rs.getString("email_professor"));
                    professor.setSenhaProfessor(rs.getString("senha_professor"));
                    // Não estamos mais buscando CPF e Disciplina aqui
                    return professor;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar professor por email e senha: " + e.getMessage());
            throw e;
        }
        return null; // Professor não encontrado
    }

    public static List<Professor> listarProfessores() throws SQLException {
        List<Professor> professores = new ArrayList<>();
        // SQL ajustado para as colunas: idProfessor, nm_professor, email_professor, senha_professor
        String sql = "SELECT idProfessor, nm_professor, email_professor, senha_professor FROM professor";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Professor professor = new Professor();
                professor.setIdProfessor(rs.getInt("idProfessor"));
                professor.setNmProfessor(rs.getString("nm_professor"));
                professor.setEmailProfessor(rs.getString("email_professor"));
                professor.setSenhaProfessor(rs.getString("senha_professor"));
                // Não estamos mais buscando CPF e Disciplina aqui
                professores.add(professor);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar professores: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return professores;
    }

    public static void atualizarProfessor(Professor professor) throws SQLException {
        // SQL ajustado para as colunas: nm_professor, email_professor, senha_professor
        // Apenas 3 campos para SET, mais o ID no WHERE, total de 4 parâmetros no PreparedStatement
        String sql = "UPDATE professor SET nm_professor = ?, email_professor = ?, senha_professor = ? WHERE idProfessor = ?";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, professor.getNmProfessor());
            stmt.setString(2, professor.getEmailProfessor());
            stmt.setString(3, professor.getSenhaProfessor());
            // O próximo parâmetro é o 4, que corresponde ao ID no WHERE
            stmt.setInt(4, professor.getIdProfessor()); // Corrigido para posição 4

            stmt.executeUpdate();
            System.out.println("Professor " + professor.getNmProfessor() + " atualizado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar professor: " + e.getMessage());
            throw e;
        }
    }

    public static void excluirProfessor(int idProfessor) throws SQLException {
        String sql = "DELETE FROM professor WHERE idProfessor = ?";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProfessor);
            stmt.executeUpdate();
            System.out.println("Professor com ID " + idProfessor + " excluído com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao excluir professor: " + e.getMessage());
            throw e;
        }
    }

    public static Professor buscarProfessorPorId(int idProfessor) throws SQLException {
        // SQL ajustado para as colunas: idProfessor, nm_professor, email_professor, senha_professor
        String sql = "SELECT idProfessor, nm_professor, email_professor, senha_professor FROM professor WHERE idProfessor = ?";
        Professor professor = null;
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProfessor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    professor = new Professor();
                    professor.setIdProfessor(rs.getInt("idProfessor"));
                    professor.setNmProfessor(rs.getString("nm_professor"));
                    professor.setEmailProfessor(rs.getString("email_professor"));
                    professor.setSenhaProfessor(rs.getString("senha_professor"));
                    // Não estamos mais buscando CPF e Disciplina aqui
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar professor por ID: " + e.getMessage());
            throw e;
        }
        return professor;
    }
}