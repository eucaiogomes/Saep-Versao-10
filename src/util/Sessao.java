package util;

import model.Professor;

public class Sessao {

    private static Sessao instance; // Única instância do Singleton
    private Professor professorLogado;

    // Construtor privado para evitar instâncias diretas de fora
    private Sessao() {
        // Inicializações se houver
    }

    // Método estático para obter a única instância da Sessao
    public static Sessao getInstance() {
        if (instance == null) {
            instance = new Sessao();
        }
        return instance;
    }

    // Getter e Setter para o Professor logado
    public Professor getProfessorLogado() {
        return professorLogado;
    }

    public void setProfessorLogado(Professor professorLogado) {
        this.professorLogado = professorLogado;
    }

    // Método para limpar a sessão (útil no logout)
    public void clearSession() {
        this.professorLogado = null;
    }
}