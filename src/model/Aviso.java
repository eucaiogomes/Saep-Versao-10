package model;

import java.time.LocalDateTime;

public class Aviso {
    private Integer idAviso;
    private String titulo;
    private String conteudo;
    private LocalDateTime dataPublicacao;
    private Integer turma_idTurma;
    private Integer professor_idProfessor;

    // Construtor vazio
    public Aviso() {}

    // Construtor completo (para buscar do banco)
    public Aviso(Integer idAviso, String titulo, String conteudo, LocalDateTime dataPublicacao, Integer turma_idTurma, Integer professor_idProfessor) {
        this.idAviso = idAviso;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.dataPublicacao = dataPublicacao;
        this.turma_idTurma = turma_idTurma;
        this.professor_idProfessor = professor_idProfessor;
    }

    // Construtor para inserção (sem idAviso e dataPublicacao, que serão gerados pelo BD)
    public Aviso(String titulo, String conteudo, Integer turma_idTurma, Integer professor_idProfessor) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.turma_idTurma = turma_idTurma;
        this.professor_idProfessor = professor_idProfessor;
    }

    // Getters
    public Integer getIdAviso() {
        return idAviso;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public Integer getTurma_idTurma() {
        return turma_idTurma;
    }

    public Integer getProfessor_idProfessor() {
        return professor_idProfessor;
    }

    // Setters
    public void setIdAviso(Integer idAviso) {
        this.idAviso = idAviso;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public void setTurma_idTurma(Integer turma_idTurma) {
        this.turma_idTurma = turma_idTurma;
    }

    public void setProfessor_idProfessor(Integer professor_idProfessor) {
        this.professor_idProfessor = professor_idProfessor;
    }

    @Override
    public String toString() {
        // Formata a data para uma exibição mais amigável
        String dataFormatada = (dataPublicacao != null) ? dataPublicacao.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";
        return "Título: " + titulo + " | Data: " + dataFormatada;
    }
}