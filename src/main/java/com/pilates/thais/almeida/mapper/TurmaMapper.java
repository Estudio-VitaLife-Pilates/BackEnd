package com.pilates.thais.almeida.mapper;

import com.pilates.thais.almeida.dto.turma.TurmaDetailsResponseDto;
import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.dto.turma.TurmaResponseDto;
import com.pilates.thais.almeida.entity.AlunoTurma;
import com.pilates.thais.almeida.entity.Turma;

import java.util.List;

public class TurmaMapper {
    public static TurmaResponseDto toResponse(Turma turma){
        Integer professorId = turma.getProfessor() != null ? turma.getProfessor().getId() : null;
        String professorNome = turma.getProfessor() != null ? turma.getProfessor().getNome() : null;

        return new TurmaResponseDto(turma.getId(), turma.getDiaSemana(), turma.getHoraInicio(),
                turma.getDuracaoMinutos(), turma.getCapacidadeMax(), turma.getAtiva(),
                professorId, professorNome);
    }
    public static Turma toEntity(TurmaRequestDto turmaRequestDto){
        return new Turma(turmaRequestDto.getDiaSemana(),turmaRequestDto.getHoraInicio(),turmaRequestDto.getDuracaoMinutos(),turmaRequestDto.getCapacidadeMax(),turmaRequestDto.getAtiva());
    }
    public static List<TurmaResponseDto> toResponse(List<Turma> turmas){
        return turmas.stream().map(TurmaMapper::toResponse).toList();
    }
    public static  TurmaDetailsResponseDto.TurmaAlunoResponseDto toTurmaAlunoResponseDto(AlunoTurma alunoTurma){
        TurmaDetailsResponseDto.TurmaAlunoResponseDto turmaAlunoResponseDto= new TurmaDetailsResponseDto.TurmaAlunoResponseDto();
        turmaAlunoResponseDto.setId(alunoTurma.getAluno().getId());
        turmaAlunoResponseDto.setCpf(alunoTurma.getAluno().getCpf());
        turmaAlunoResponseDto.setDataNascimento(alunoTurma.getAluno().getDataNascimento());
        turmaAlunoResponseDto.setFichaAnamnese(alunoTurma.getAluno().getFichaAnamnese());
        turmaAlunoResponseDto.setAlunoAtivo(alunoTurma.getAluno().getAtivo());
        turmaAlunoResponseDto.setAlunoTurmaAtivo(alunoTurma.getAtivo());
        turmaAlunoResponseDto.setDataCadastro(alunoTurma.getAluno().getDataCadastro());
        turmaAlunoResponseDto.setTelefone(alunoTurma.getAluno().getTelefone());
        turmaAlunoResponseDto.setNome(alunoTurma.getAluno().getNome());
        turmaAlunoResponseDto.setEmail(alunoTurma.getAluno().getEmail());
        turmaAlunoResponseDto.setDataInicio(alunoTurma.getDataInicio());
        return turmaAlunoResponseDto;

    }
    public static TurmaDetailsResponseDto toResponseDetails(Turma turma){
        TurmaDetailsResponseDto turmaDetailsResponseDto=new TurmaDetailsResponseDto();
        turmaDetailsResponseDto.setId(turma.getId());
        turmaDetailsResponseDto.setAtiva(turma.getAtiva());
        turmaDetailsResponseDto.setCapacidadeMax(turma.getCapacidadeMax());
        turmaDetailsResponseDto.setDiaSemana(turma.getDiaSemana());
        turmaDetailsResponseDto.setAlunos(
                turma.getAlunos().stream().map(TurmaMapper::toTurmaAlunoResponseDto).toList()

        );
        turmaDetailsResponseDto.setDuracaoMinutos(turma.getDuracaoMinutos());
        return turmaDetailsResponseDto;
    }
}