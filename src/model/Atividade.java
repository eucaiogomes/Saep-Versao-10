package model;

public class Atividade {
    private Integer idAtividade;
    private String nm_atividade;    // Corresponde à coluna 'nm_atividade'
    private String ds_atividade;    // Corresponde à coluna 'ds_atividade'
    private Integer Turma_idTurma;  // Corresponde à coluna 'Turma_idTurma'
    private Integer Professor_idProfessor;

    public Atividade() {
        // Construtor vazio
    }

    public Atividade(String nm_atividade, String ds_atividade, Integer Turma_idTurma) {
        this.nm_atividade = nm_atividade;
        this.ds_atividade = ds_atividade;
        this.Turma_idTurma = Turma_idTurma;
    }

    public Atividade(Integer idAtividade, String nm_atividade, String ds_atividade, Integer Turma_idTurma) {
        this.idAtividade = idAtividade;
        this.nm_atividade = nm_atividade;
        this.ds_atividade = ds_atividade;
        this.Turma_idTurma = Turma_idTurma;
    }

    public Atividade(String nome, String descricao, Integer idTurma, Integer idProfessor) {
		this.nm_atividade = nome;
		this.ds_atividade = descricao;
		this.Turma_idTurma = idTurma;
		this.Professor_idProfessor = idProfessor;
	}

	// Getters e Setters
    public Integer getIdAtividade() {
        return idAtividade;
    }

    public void setIdAtividade(Integer idAtividade) {
        this.idAtividade = idAtividade;
    }

    // Usar 'nm_atividade' para refletir o nome da coluna no banco
    public String getNmAtividade() {
        return nm_atividade;
    }

    public void setNmAtividade(String nm_atividade) {
        this.nm_atividade = nm_atividade;
    }

    // 'ds_atividade' já está correto
    public String getDsAtividade() {
        return ds_atividade;
    }

    public void setDsAtividade(String ds_atividade) {
        this.ds_atividade = ds_atividade;
    }

    // Usar 'Turma_idTurma' para refletir o nome da coluna no banco
    public Integer getTurma_idTurma() {
        return Turma_idTurma;
    }

    public void setTurma_idTurma(Integer Turma_idTurma) {
        this.Turma_idTurma = Turma_idTurma;
    }

    // Removido: Não é necessário ter professor_idProfessor aqui.
    // public Integer getProfessor_idProfessor() { return professor_idProfessor; }
    // public void setProfessor_idProfessor(Integer professor_idProfessor) { this.professor_idProfessor = professor_idProfessor; }

    @Override
    public String toString() {
        return nm_atividade + " - " + ds_atividade;
    }

	public String getNm_atividade() {
		return nm_atividade;
	}

	public void setNm_atividade(String nm_atividade) {
		this.nm_atividade = nm_atividade;
	}

	public String getDs_atividade() {
		return ds_atividade;
	}

	public void setDs_atividade(String ds_atividade) {
		this.ds_atividade = ds_atividade;
	}

	public Integer getProfessor_idProfessor() {
		return Professor_idProfessor;
	}

	public void setProfessor_idProfessor(Integer professor_idProfessor) {
		Professor_idProfessor = professor_idProfessor;
	}

	
}