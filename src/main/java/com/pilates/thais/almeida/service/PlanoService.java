package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.Plano;
import com.pilates.thais.almeida.exceptions.PlanoNaoEncontrado;
import com.pilates.thais.almeida.repository.AlunoPlanoRepository;
import com.pilates.thais.almeida.repository.AlunoTurmaRepository;
import com.pilates.thais.almeida.repository.PlanoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanoService {

    private final PlanoRepository planoRepository;
    private final AlunoPlanoRepository alunoPlanoRepository;
    private final AlunoTurmaRepository alunoTurmaRepository;

    public PlanoService(PlanoRepository planoRepository, AlunoPlanoRepository alunoPlanoRepository, AlunoTurmaRepository alunoTurmaRepository) {
        this.planoRepository = planoRepository;
        this.alunoPlanoRepository = alunoPlanoRepository;
        this.alunoTurmaRepository = alunoTurmaRepository;
    }

    public List<Plano> buscarTodos(){
        return planoRepository.findAll();
    }

    public Plano buscarPorId(Integer id){
        Optional<Plano> planoOptional = planoRepository.findById(id);

        if(planoOptional.isPresent()){
            return planoOptional.get();
        }

        throw new PlanoNaoEncontrado("");
    }

    public Plano criar(Plano plano){
        return planoRepository.save(plano);
    }

    public Plano editarPorId(Plano plano, Integer id){
        Optional<Plano> planoBD = planoRepository.findById(id);

        if(planoBD.isEmpty()){
            throw new PlanoNaoEncontrado("");
        }

        Plano planoAtual = planoBD.get();
        List<AlunoPlano> alunoPlanosAtivos = alunoPlanoRepository.findByPlano_IdAndAtivoIsTrue(id);

        boolean validadeMudou = !planoAtual.getValidadeDias().equals(plano.getValidadeDias());
        if (validadeMudou && !alunoPlanosAtivos.isEmpty()) {
            throw new RuntimeException(
                    "Não é possível alterar a validade de um plano com alunos ativos. " +
                    "Aguarde os vínculos atuais encerrarem ou crie um novo plano."
            );
        }

        boolean frequenciaDiminuiu = plano.getFrequenciaSemanal() < planoAtual.getFrequenciaSemanal();
        if (frequenciaDiminuiu) {
            for (AlunoPlano alunoPlano : alunoPlanosAtivos) {
                Integer turmasDoAluno = alunoTurmaRepository.findByAlunoIdAndAtivoTrue(alunoPlano.getAluno().getId()).size();
                if (turmasDoAluno > plano.getFrequenciaSemanal()) {
                    throw new RuntimeException(
                            "Não é possível reduzir a frequência semanal: " + alunoPlano.getAluno().getNome() +
                            " está matriculado em " + turmasDoAluno + " turma(s), acima do novo limite."
                    );
                }
            }
        }

        plano.setId(planoAtual.getId());
        return planoRepository.save(plano);
    }

    public void deletarPorId(Integer id){
        if(planoRepository.existsById(id)){
            planoRepository.deleteById(id);
        }else{
            throw new PlanoNaoEncontrado("");
        }
    }
}
