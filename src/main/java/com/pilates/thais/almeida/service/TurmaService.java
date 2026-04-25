package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.entity.AlunoTurma;
import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.entity.Turma;
import com.pilates.thais.almeida.exceptions.ProfessorNaoEncontrado;
import com.pilates.thais.almeida.exceptions.TurmaNaoEncontrada;
import com.pilates.thais.almeida.mapper.TurmaMapper;
import com.pilates.thais.almeida.repository.ProfessorRepository;
import com.pilates.thais.almeida.repository.TurmaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurmaService {
    private final TurmaRepository turmaRepository;

    public TurmaService(TurmaRepository turmaRepository, ProfessorRepository professorRepository) {
        this.turmaRepository = turmaRepository;

    }

    public List<Turma> listar() {
        return turmaRepository.findTurmasByAtivaTrue();

    }

    public Turma cadastrar( TurmaRequestDto requestDto) {
        Turma turma= TurmaMapper.toEntity(requestDto);
        return turmaRepository.save(turma);


    }

    public Turma atualizar( TurmaRequestDto requestDto, Integer id) {
        Turma turma= turmaRepository.findById(id).orElseThrow(()->new TurmaNaoEncontrada("Turma nao encontrada"));
        turma.setAtiva(requestDto.getAtiva());
        turma.setCapacidadeMax(requestDto.getCapacidadeMax());
        turma.setDiaSemana(requestDto.getDiaSemana());
        turma.setDuracaoMinutos(requestDto.getDuracaoMinutos());
        return turmaRepository.save(turma);


    }
    public void inativar(Integer id){
        Turma turma= turmaRepository.findById(id).orElseThrow(()->new TurmaNaoEncontrada("Turma nao encontrada"));
        turma.setAtiva(false);
        turmaRepository.save(turma);
    }

    public Turma buscarPorId(Integer id) {
        return turmaRepository.findById(id).orElseThrow(()->new TurmaNaoEncontrada("Turma nao encontrada"));

    }


    public Turma buscarAlunosPorTurma(Integer id) {
        return turmaRepository.findById(id).orElseThrow(()->new TurmaNaoEncontrada("Turma nao encontrada"));
    }
}
