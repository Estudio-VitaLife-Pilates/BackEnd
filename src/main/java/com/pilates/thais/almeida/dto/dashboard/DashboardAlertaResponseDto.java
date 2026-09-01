package com.pilates.thais.almeida.dto.dashboard;

public class    DashboardAlertaResponseDto {
    private Long alunosParaReagendamento;
    private Long aulasCanceladasMes;
    private Long aulasRemarcadasMes;
    private Long novosAlunosMes;
    private Long desmatriculasMes;

    public DashboardAlertaResponseDto() {
    }

    public DashboardAlertaResponseDto(Long alunosParaReagendamento, Long aulasCanceladasMes,
                                     Long aulasRemarcadasMes, Long novosAlunosMes, Long desmatriculasMes) {
        this.alunosParaReagendamento = alunosParaReagendamento;
        this.aulasCanceladasMes = aulasCanceladasMes;
        this.aulasRemarcadasMes = aulasRemarcadasMes;
        this.novosAlunosMes = novosAlunosMes;
        this.desmatriculasMes = desmatriculasMes;
    }

    public Long getAlunosParaReagendamento() {
        return alunosParaReagendamento;
    }

    public void setAlunosParaReagendamento(Long alunosParaReagendamento) {
        this.alunosParaReagendamento = alunosParaReagendamento;
    }

    public Long getAulasCanceladasMes() {
        return aulasCanceladasMes;
    }

    public void setAulasCanceladasMes(Long aulasCanceladasMes) {
        this.aulasCanceladasMes = aulasCanceladasMes;
    }

    public Long getAulasRemarcadasMes() {
        return aulasRemarcadasMes;
    }

    public void setAulasRemarcadasMes(Long aulasRemarcadasMes) {
        this.aulasRemarcadasMes = aulasRemarcadasMes;
    }

    public Long getNovosAlunosMes() {
        return novosAlunosMes;
    }

    public void setNovosAlunosMes(Long novosAlunosMes) {
        this.novosAlunosMes = novosAlunosMes;
    }

    public Long getDesmatriculasMes() {
        return desmatriculasMes;
    }

    public void setDesmatriculasMes(Long desmatriculasMes) {
        this.desmatriculasMes = desmatriculasMes;
    }
}