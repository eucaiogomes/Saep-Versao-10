package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    // URL de conexão com o banco de dados MariaDB
    // EXEMPLO: jdbc:mariadb://localhost:3306/seubanco
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/banco_escola"; // <<< AJUSTE AQUI
    private static final String DB_USER = "root"; // <<< AJUSTE AQUI
    private static final String DB_PASSWORD = "root"; // <<< AJUSTE AQUI

    public static Connection conectar() throws SQLException {
        try {
            // Carrega o driver JDBC do MariaDB
            Class.forName("org.mariadb.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do MariaDB não encontrado: " + e.getMessage());
            throw new SQLException("Driver JDBC do MariaDB não encontrado.", e);
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            throw e;
        }
    }

    public static void fecharConexao(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão com o banco de dados: " + e.getMessage());
            }
        }
    }
}