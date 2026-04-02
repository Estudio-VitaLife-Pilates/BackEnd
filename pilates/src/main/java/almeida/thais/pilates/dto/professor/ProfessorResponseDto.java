package almeida.thais.pilates.dto.professor;

public class ProfessorResponseDto {

    private Integer id;
    private String nome;
    private String telefone;
    private Boolean ativo;
    private String email;

    public ProfessorResponseDto() {
    }

    public ProfessorResponseDto(Integer id, String nome, String telefone, Boolean ativo, String email) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.ativo = ativo;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
