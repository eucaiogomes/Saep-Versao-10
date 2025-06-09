package DAO;

import model.Aviso;
import util.ConexaoBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class AvisoDAO {

    /**
     * Adiciona um novo aviso ao banco de dados.
     * @param aviso O objeto Aviso a ser inserido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public static void inserirAviso(Aviso aviso) throws SQLException {
        String sql = "INSERT INTO Aviso (titulo, conteudo, Turma_idTurma, Professor_idProfessor) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, aviso.getTitulo());
            stmt.setString(2, aviso.getConteudo());
            stmt.setInt(3, aviso.getTurma_idTurma());
            stmt.setInt(4, aviso.getProfessor_idProfessor());
            stmt.executeUpdate();
            System.out.println("Aviso '" + aviso.getTitulo() + "' adicionado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar aviso: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Lista todos os avisos de uma turma específica.
     * @param idTurma O ID da turma.
     * @return Uma lista de objetos Aviso.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public static List<Aviso> listarAvisosPorTurma(int idTurma) throws SQLException {
        List<Aviso> listaAvisos = new ArrayList<>();
        String sql = "SELECT idAviso, titulo, conteudo, dataPublicacao, Turma_idTurma, Professor_idProfessor FROM Aviso WHERE Turma_idTurma = ? ORDER BY dataPublicacao DESC";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idTurma);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime dataPublicacao = null;
                    Timestamp ts = rs.getTimestamp("dataPublicacao");
                    if (ts != null) {
                        dataPublicacao = ts.toLocalDateTime();
                    }

                    Aviso aviso = new Aviso(
                        rs.getInt("idAviso"),
                        rs.getString("titulo"),
                        rs.getString("conteudo"),
                        dataPublicacao,
                        rs.getInt("Turma_idTurma"),
                        rs.getInt("Professor_idProfessor")
                    );
                    listaAvisos.add(aviso);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar avisos por turma: " + e.getMessage());
            throw e;
        }
        return listaAvisos;
    }

    /**
     * Busca um aviso pelo seu ID.
     * @param idAviso O ID do aviso.
     * @return O objeto Aviso, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public static Aviso buscarAvisoPorId(int idAviso) throws SQLException {
        Aviso aviso = null;
        String sql = "SELECT idAviso, titulo, conteudo, dataPublicacao, Turma_idTurma, Professor_idProfessor FROM Aviso WHERE idAviso = ?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idAviso);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime dataPublicacao = null;
                    Timestamp ts = rs.getTimestamp("dataPublicacao");
                    if (ts != null) {
                        dataPublicacao = ts.toLocalDateTime();
                    }
                    aviso = new Aviso(
                        rs.getInt("idAviso"),
                        rs.getString("titulo"),
                        rs.getString("conteudo"),
                        dataPublicacao,
                        rs.getInt("Turma_idTurma"),
                        rs.getInt("Professor_idProfessor")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar aviso por ID: " + e.getMessage());
            throw e;
        }
        return aviso;
    }

    /**
     * Atualiza um aviso existente no banco de dados.
     * @param aviso O objeto Aviso com os dados atualizados.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public static void atualizarAviso(Aviso aviso) throws SQLException {
        String sql = "UPDATE Aviso SET titulo = ?, conteudo = ?, Turma_idTurma = ? WHERE idAviso = ?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, aviso.getTitulo());
            stmt.setString(2, aviso.getConteudo());
            stmt.setInt(3, aviso.getTurma_idTurma());
            stmt.setInt(4, aviso.getIdAviso());
            stmt.executeUpdate();
            System.out.println("Aviso com ID " + aviso.getIdAviso() + " atualizado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar aviso: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Exclui um aviso do banco de dados.
     * @param idAviso O ID do aviso a ser excluído.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public static void excluirAviso(int idAviso) throws SQLException {
        String sql = "DELETE FROM Aviso WHERE idAviso = ?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idAviso);
            stmt.executeUpdate();
            System.out.println("Aviso com ID " + idAviso + " excluído com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao excluir aviso: " + e.getMessage());
            throw e;
        }
    }
    public static List<Aviso> listarUltimosAvisosPorProfessor(int idProfessor, int limit) throws SQLException {
        List<Aviso> listaAvisos = new ArrayList<>();
        // Note: Se o professor puder ver avisos de outras turmas que ele não gerencia,
        // essa query precisaria ser ajustada para incluir JOIN com Turma e Professor.
        // Por enquanto, assumimos que ele só vê avisos que ele mesmo postou em suas turmas.
        String sql = "SELECT idAviso, titulo, conteudo, dataPublicacao, Turma_idTurma, Professor_idProfessor " +
                     "FROM Aviso WHERE Professor_idProfessor = ? ORDER BY dataPublicacao DESC LIMIT ?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idProfessor);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime dataPublicacao = null;
                    Timestamp ts = rs.getTimestamp("dataPublicacao");
                    if (ts != null) {
                        dataPublicacao = ts.toLocalDateTime();
                    }

                    Aviso aviso = new Aviso(
                        rs.getInt("idAviso"),
                        rs.getString("titulo"),
                        rs.getString("conteudo"),
                        dataPublicacao,
                        rs.getInt("Turma_idTurma"),
                        rs.getInt("Professor_idProfessor")
                    );
                    listaAvisos.add(aviso);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar últimos avisos por professor: " + e.getMessage());
            throw e;
        }
        return listaAvisos;
    }

}	