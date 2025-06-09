package model;

public class Turma {

    private Integer idTurma;
    private String nm_turma;
    private String ds_turma;
    private Integer Professor_idProfessor; // FK ID do professor
    private Professor professor;    // Objeto Professor para exibição

    public Turma() {
        // Construtor vazio
    }

    public Turma(String nm_turma, String ds_turma, Professor professor) {
        this.nm_turma = nm_turma;
        this.ds_turma = ds_turma;
        this.setProfessor(professor); // Define o objeto Professor e o ID da FK
    }

    public Turma(Integer idTurma, String nm_turma, String ds_turma, Professor professor) {
        this.idTurma = idTurma;
        this.nm_turma = nm_turma;
        this.ds_turma = ds_turma;
        this.setProfessor(professor); // Define o objeto Professor e o ID da FK
    }

    // Getters e Setters
    public Integer getIdTurma() {
        return idTurma;
    }

    public void setIdTurma(Integer idTurma) {
        this.idTurma = idTurma;
    }

    public String getNmTurma() {
        return nm_turma;
    }

    public void setNmTurma(String nm_turma) {
        this.nm_turma = nm_turma;
    }

    public String getDsTurma() {
        return ds_turma;
    }

    public void setDsTurma(String ds_turma) {
        this.ds_turma = ds_turma;
    }

    public Integer getProfessor_idProfessor() {
        return Professor_idProfessor;
    }

    public void setProfessor_idProfessor(Integer professor_idProfessor) {
        this.Professor_idProfessor = professor_idProfessor;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
        if (professor != null) {
            this.Professor_idProfessor = professor.getIdProfessor();
        } else {
            this.Professor_idProfessor = null;
        }
    }

    @Override
    public String toString() {
        String professorNome = (professor != null) ? professor.getNmProfessor() : "N/A";
        return nm_turma + " (" + ds_turma + ") - Prof: " + professorNome;
    }
}