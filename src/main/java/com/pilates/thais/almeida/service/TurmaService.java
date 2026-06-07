package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.dto.turma.TurmaAlunoRequest;
import com.pilates.thais.almeida.dto.turma.TurmaRequestDto;
import com.pilates.thais.almeida.dto.turma.TurmaResponseDto;
import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.AlunoTurma;
import com.pilates.thais.almeida.entity.Turma;
import com.pilates.thais.almeida.exceptions.AlunoNaoEncontrado;
import com.pilates.thais.almeida.exceptions.TurmaNaoEncontrada;
import com.pilates.thais.almeida.exceptions.UsuarioJaCadastradoNoMaximoDeTurmas;
import com.pilates.thais.almeida.mapper.TurmaMapper;
import com.pilates.thais.almeida.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class TurmaService {
    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;
    private final AlunoTurmaRepository alunoTurmaRepository;
    private final AlunoPlanoRepository alunoPlanoRepository;
    private final AulaRepository aulaRepository;

    private final AulaService aulaService;

    public TurmaService(TurmaRepository turmaRepository, AlunoRepository alunoRepository, AlunoTurmaRepository alunoTurmaRepository, AlunoPlanoRepository alunoPlanoRepository, AulaRepository aulaRepository, AulaService aulaService) {
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
        this.alunoTurmaRepository = alunoTurmaRepository;
        this.alunoPlanoRepository = alunoPlanoRepository;
        this.aulaRepository = aulaRepository;
        this.aulaService = aulaService;
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

        Turma turma = TurmaMapper.toEntity(requestDto);

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

        // verificando se aulas ja existem - caso nao, cria aulas
        LocalDate hoje = LocalDate.now();

        String diaSemanaTurma = String.valueOf(turma.getDiaSemana());

        LocalDate diaTurma = hoje.plusDays(1);

        while(true){
            if(obterDiaSemana(diaTurma).equals(diaSemanaTurma)){
                break;
            }
            diaTurma = diaTurma.plusDays(1);
        }

        while(true){
            if(!diaTurma.isAfter(alunoPlanos.getFirst().getDataFim())){
                if(!aulaRepository.existsAulaByDataAulaAndTurma_Id(diaTurma, turma.getId())){
                    aulaService.criarAula(turma.getId(), turma.getProfessor().getId(), diaTurma, aluno);
                }
                diaTurma = diaTurma.plusDays(7);
            }else{
                break;
            }
        }

        return turma;
    }

    private String obterDiaSemana(LocalDate data){
        return data.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR")).split("-")[0].toUpperCase();
    }

    public Turma excluirAlunoDaTurma(Integer id, Integer alunoId) {
        AlunoTurma alunoTurma= alunoTurmaRepository.findByAlunoIdAndTurmaId(alunoId,id);
        alunoTurmaRepository.removeById(alunoTurma.getId());
        return alunoTurma.getTurma();


    }

    public Integer vagasDisponiveis(Integer id) {
        Integer ocupadas= alunoTurmaRepository.countByTurmaIdAndAtivoTrue(id);
        Turma turma= turmaRepository.findById(id).orElseThrow(()->new TurmaNaoEncontrada(""));
        return turma.getCapacidadeMax()-ocupadas;

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
}
