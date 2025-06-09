package model;

public class Professor {
    private Integer idProfessor;
    private String nm_professor;        // Corresponde à coluna 'nm_professor'
    private String email_professor;     // Corresponde à coluna 'email_professor'
    private String senha_professor;     // Corresponde à coluna 'senha_professor'
    private String cpf_professor;       // Corresponde à coluna 'cpf_professor'
    private String disciplina_professor; // Corresponde à coluna 'disciplina_professor'

    public Professor() {
        // Construtor vazio
    }

    public Professor(String nm_professor, String email_professor, String senha_professor, String cpf_professor, String disciplina_professor) {
        this.nm_professor = nm_professor;
        this.email_professor = email_professor;
        this.senha_professor = senha_professor;
        this.cpf_professor = cpf_professor;
        this.disciplina_professor = disciplina_professor;
    }

    public Professor(Integer idProfessor, String nm_professor, String email_professor, String senha_professor, String cpf_professor, String disciplina_professor) {
        this.idProfessor = idProfessor;
        this.nm_professor = nm_professor;
        this.email_professor = email_professor;
        this.senha_professor = senha_professor;
        this.cpf_professor = cpf_professor;
        this.disciplina_professor = disciplina_professor;
    }

    public Professor(int idProfessor, String nome, String email, String senha) {
    	 this.idProfessor = idProfessor;
         this.nm_professor = nome;
         this.email_professor = email;
         this.senha_professor = senha;
    }

	// Getters e Setters
    public Integer getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(Integer idProfessor) {
        this.idProfessor = idProfessor;
    }

    // Usar 'nm_professor' para refletir o nome da coluna no banco
    public String getNmProfessor() {
        return nm_professor;
    }

    public void setNmProfessor(String nm_professor) {
        this.nm_professor = nm_professor;
    }

    // Usar 'email_professor' para refletir o nome da coluna no banco
    public String getEmailProfessor() {
        return email_professor;
    }

    public void setEmailProfessor(String email_professor) {
        this.email_professor = email_professor;
    }

    // Usar 'senha_professor' para refletir o nome da coluna no banco
    public String getSenhaProfessor() {
        return senha_professor;
    }

    public void setSenhaProfessor(String senha_professor) {
        this.senha_professor = senha_professor;
    }

    // Usar 'cpf_professor' para refletir o nome da coluna no banco
    public String getCpfProfessor() {
        return cpf_professor;
    }

    public void setCpfProfessor(String cpf_professor) {
        this.cpf_professor = cpf_professor;
    }

    // Usar 'disciplina_professor' para refletir o nome da coluna no banco
    public String getDisciplinaProfessor() {
        return disciplina_professor;
    }

    public void setDisciplinaProfessor(String disciplina_professor) {
        this.disciplina_professor = disciplina_professor;
    }

    @Override
    public String toString() {
        return nm_professor; // Representação para ListView/ComboBox
    }
}