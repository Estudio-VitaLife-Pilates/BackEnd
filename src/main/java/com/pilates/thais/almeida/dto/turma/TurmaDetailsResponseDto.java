package com.pilates.thais.almeida.dto.turma;

import com.pilates.thais.almeida.entity.Turma;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TurmaDetailsResponseDto {
    private Integer id;
    private Turma.DiaSemana diaSemana ;
    private LocalTime horaInicio;
    private Integer duracaoMinutos;
    private Integer capacidadeMax;
    private Boolean ativa;
    private List<TurmaAlunoResponseDto> alunos;



    public static  class TurmaAlunoResponseDto {
        private Integer id;
        private String nome;
        private String telefone;
        private String cpf;
        private String email;
        private Boolean alunoAtivo;
        private LocalDate dataNascimento;
        private LocalDate dataCadastro;
        private String fichaAnamnese;
        private LocalDate dataInicio;
        private Boolean alunoTurmaAtivo;

        public TurmaAlunoResponseDto() {
        }

        public TurmaAlunoResponseDto(Integer id, String nome, String telefone, String cpf, String email, Boolean alunoAtivo, LocalDate dataNascimento, LocalDate dataCadastro, String fichaAnamnese, LocalDate dataInicio, Boolean alunoTurmaAtivo) {
            this.id = id;
            this.nome = nome;
            this.telefone = telefone;
            this.cpf = cpf;
            this.email = email;
            this.alunoAtivo = alunoAtivo;
            this.dataNascimento = dataNascimento;
            this.dataCadastro = dataCadastro;
            this.fichaAnamnese = fichaAnamnese;
            this.dataInicio = dataInicio;
            this.alunoTurmaAtivo = alunoTurmaAtivo;
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

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Boolean getAlunoAtivo() {
            return alunoAtivo;
        }

        public void setAlunoAtivo(Boolean alunoAtivo) {
            this.alunoAtivo = alunoAtivo;
        }

        public LocalDate getDataNascimento() {
            return dataNascimento;
        }

        public void setDataNascimento(LocalDate dataNascimento) {
            this.dataNascimento = dataNascimento;
        }

        public LocalDate getDataCadastro() {
            return dataCadastro;
        }

        public void setDataCadastro(LocalDate dataCadastro) {
            this.dataCadastro = dataCadastro;
        }

        public String getFichaAnamnese() {
            return fichaAnamnese;
        }

        public void setFichaAnamnese(String fichaAnamnese) {
            this.fichaAnamnese = fichaAnamnese;
        }

        public LocalDate getDataInicio() {
            return dataInicio;
        }

        public void setDataInicio(LocalDate dataInicio) {
            this.dataInicio = dataInicio;
        }

        public Boolean getAlunoTurmaAtivo() {
            return alunoTurmaAtivo;
        }

        public void setAlunoTurmaAtivo(Boolean alunoTurmaAtivo) {
            this.alunoTurmaAtivo = alunoTurmaAtivo;
        }
    }

    public TurmaDetailsResponseDto(Integer id, Turma.DiaSemana diaSemana, LocalTime horaInicio, Integer capacidadeMax, Integer duracaoMinutos, Boolean ativa, List<TurmaAlunoResponseDto> alunos) {
        this.id = id;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.capacidadeMax = capacidadeMax;
        this.duracaoMinutos = duracaoMinutos;
        this.ativa = ativa;
        this.alunos = alunos;
    }

    public TurmaDetailsResponseDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Turma.DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(Turma.DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public Integer getCapacidadeMax() {
        return capacidadeMax;
    }

    public void setCapacidadeMax(Integer capacidadeMax) {
        this.capacidadeMax = capacidadeMax;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public List<TurmaAlunoResponseDto> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<TurmaAlunoResponseDto> alunos) {
        this.alunos = alunos;
    }
}
