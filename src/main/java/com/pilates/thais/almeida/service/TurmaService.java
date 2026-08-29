package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.turma.TurmaAlunoRequest;
import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.dto.turma.TurmaResponseDto;
import com.pilates.thais.almeida.entity.*;
import com.pilates.thais.almeida.exceptions.*;
import com.pilates.thais.almeida.mapper.TurmaMapper;
import com.pilates.thais.almeida.repository.*;
import com.pilates.thais.almeida.strategy.CalculoVagasTurmaStrategy;
import com.pilates.thais.almeida.strategy.GeracaoDatasAulaStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TurmaService {
    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;
    private final AlunoTurmaRepository alunoTurmaRepository;
    private final AlunoPlanoRepository alunoPlanoRepository;
    private final AulaRepository aulaRepository;
    private final ProfessorRepository professorRepository;

    private final AulaService aulaService;
    private final GeracaoDatasAulaStrategy geracaoDatasAulaStrategy;
    private final CalculoVagasTurmaStrategy calculoVagasTurmaStrategy;

    public TurmaService(TurmaRepository turmaRepository, AlunoRepository alunoRepository, AlunoTurmaRepository alunoTurmaRepository, AlunoPlanoRepository alunoPlanoRepository, AulaRepository aulaRepository, AulaService aulaService, ProfessorRepository professorRepository, GeracaoDatasAulaStrategy geracaoDatasAulaStrategy, CalculoVagasTurmaStrategy calculoVagasTurmaStrategy) {
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
        this.alunoTurmaRepository = alunoTurmaRepository;
        this.alunoPlanoRepository = alunoPlanoRepository;
        this.aulaRepository = aulaRepository;
        this.professorRepository = professorRepository;
        this.aulaService = aulaService;
        this.geracaoDatasAulaStrategy = geracaoDatasAulaStrategy;
        this.calculoVagasTurmaStrategy = calculoVagasTurmaStrategy;
    }


    public List<Turma> listar() {
        return turmaRepository.findTurmasByAtivaTrue();

    }

    public Turma cadastrar(TurmaRequestDto requestDto) {

        boolean turmaExiste = turmaRepository
                .existsByHoraInicioAndDiaSemana(

                        requestDto.getHoraInicio(),
                        requestDto.getDiaSemana()
                );

        if (turmaExiste) {
            throw new RuntimeException(
                    "Ja existe uma turma nesse horário."
            );
        }

        Professor professor = professorRepository.findById(requestDto.getProfessorId())
                .orElseThrow(() -> new ProfessorNaoEncontrado("Professor não encontrado"));

        Turma turma = TurmaMapper.toEntity(requestDto);
        turma.setProfessor(professor);

        return turmaRepository.save(turma);
    }

    public Turma atualizar( TurmaRequestDto requestDto, Integer id) {
        Turma turma= turmaRepository.findById(id).orElseThrow(()->new TurmaNaoEncontrada("Turma nao encontrada"));

        Professor professor = professorRepository.findById(requestDto.getProfessorId())
                .orElseThrow(() -> new ProfessorNaoEncontrado("Professor não encontrado"));

        turma.setAtiva(requestDto.getAtiva());
        turma.setCapacidadeMax(requestDto.getCapacidadeMax());
        turma.setDiaSemana(requestDto.getDiaSemana());
        turma.setDuracaoMinutos(requestDto.getDuracaoMinutos());
        turma.setProfessor(professor);
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

    public Turma cadastrarAlunoEmUmaTurma(Integer id, TurmaAlunoRequest alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId.getAlunoId())
                .orElseThrow(() -> new AlunoNaoEncontrado("Aluno nao encontrado"));

        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new TurmaNaoEncontrada("Turma nao encontrada"));

        List<AlunoPlano> alunoPlanos = alunoPlanoRepository.findByAtivoIsTrueAndAluno_Id(aluno.getId());

        if(alunoPlanos.isEmpty()){
            throw new RuntimeException("Aluno sem plano ativo");
        }

        Integer frequenciaSemanal = alunoPlanos.getFirst().getPlano().getFrequenciaSemanal();

        if(frequenciaSemanal < alunoTurmaRepository.countByAlunoId(aluno.getId())+1){
            throw new UsuarioJaCadastradoNoMaximoDeTurmas("Usuário já cadastrado no máximo de turmas que o plano permite.");
        }

        boolean alunoJaExiste = alunoTurmaRepository
                .existsByAlunoIdAndTurmaId(aluno.getId(), turma.getId());

        if (alunoJaExiste) {
            throw new RuntimeException("Aluno já cadastrado nessa turma");
        }

        AlunoTurma alunoTurma = new AlunoTurma();

        alunoTurma.setAluno(aluno);
        alunoTurma.setTurma(turma);
        alunoTurma.setAtivo(true);
        alunoTurma.setDataInicio(LocalDate.now());

        alunoTurmaRepository.save(alunoTurma);

        for(LocalDate diaTurma : geracaoDatasAulaStrategy.gerarDatas(turma, alunoPlanos.getFirst(), LocalDate.now())){
            if(!aulaRepository.existsAulaByDataAulaAndTurma_Id(diaTurma, turma.getId())){
                aulaService.criarAula(turma.getId(), turma.getProfessor().getId(), diaTurma, aluno);
            }
        }

        return turma;
    }

    public Turma excluirAlunoDaTurma(Integer id, Integer alunoId) {
        AlunoTurma alunoTurma= alunoTurmaRepository.findByAlunoIdAndTurmaId(alunoId,id);
        alunoTurmaRepository.removeById(alunoTurma.getId());
        return alunoTurma.getTurma();


    }

    public Integer vagasDisponiveis(Integer id) {
        Integer ocupadas= alunoTurmaRepository.countByTurmaIdAndAtivoTrue(id);
        Turma turma= turmaRepository.findById(id).orElseThrow(()->new TurmaNaoEncontrada(""));
        return calculoVagasTurmaStrategy.calcular(turma, ocupadas);

    }

    public List<Turma> buscarPorDiaDaSemana(String diaSemana) {
        List<Turma> turmas;

        if (diaSemana != null && !diaSemana.isBlank()) {
            turmas = turmaRepository.findByDiaSemanaIgnoreCase(diaSemana);
        } else {
            turmas = turmaRepository.findAll();
        }

        return turmas;
    }

    public Turma trocarProfessor(
            Integer idTurma,
            Integer idProfessor
    ){
        Turma turma = turmaRepository.findById(idTurma)
                .orElseThrow(() -> new TurmaNaoEncontrada("Turma não encontrada"));

        Professor professor = professorRepository.findById(idProfessor)
                .orElseThrow(() -> new ProfessorNaoEncontrado("Professor não encontrado"));

        List<Aula> aulasFuturas = aulaRepository.findAllByDataAulaAfterAndTurma_Id(LocalDate.now(), idTurma);

        for(Aula aula: aulasFuturas){
            aula.setProfessor(professor);
            aulaRepository.save(aula);
        }

        turma.setProfessor(professor);

        return turmaRepository.save(turma);
    }

    public List<Turma> listarPorProfessor(Integer professorId) {
        return turmaRepository.findByProfessor_Id(professorId);
    }
}