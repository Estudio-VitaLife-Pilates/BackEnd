package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.Plano;
import com.pilates.thais.almeida.entity.Professor;
import com.pilates.thais.almeida.exceptions.*;
import com.pilates.thais.almeida.repository.AlunoPlanoRepository;
import com.pilates.thais.almeida.repository.AlunoRepository;
import com.pilates.thais.almeida.repository.PlanoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;
    private final AlunoPlanoRepository alunoPlanoRepository;

    public AlunoService(AlunoRepository alunoRepository, PlanoRepository planoRepository, AlunoPlanoRepository alunoPlanoRepository) {
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
        this.alunoPlanoRepository = alunoPlanoRepository;
    }

    public List<Aluno> obterTodos(){
        return alunoRepository.findAll();
    }

    public Aluno obterPorId(Integer id){
        return alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoNaoEncontrado(""));
    }

    public List<Aluno> buscarPorNome(String nome){
        return alunoRepository.findAllByNomeContainingIgnoreCase(nome);
    }

    public Aluno criar(Aluno aluno){
        if(alunoRepository.existsAlunoByEmail(aluno.getEmail())){
            throw new CadastroAlunoConflitoEmail("Aluno com este email já existe");
        }
        return alunoRepository.save(aluno);
    }

    public Aluno associarPlano(Integer idAluno, Integer idPlano){
        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() -> new AlunoNaoEncontrado(""));

        Plano plano = planoRepository.findById(idPlano)
                .orElseThrow(() -> new PlanoNaoEncontrado(""));

        AlunoPlano alunoPlano = new AlunoPlano();

        alunoPlano.setAluno(aluno);
        alunoPlano.setPlano(plano);
        alunoPlano.setDataInicio(LocalDate.now());
        alunoPlano.setDataFim(LocalDate.now().plusDays(plano.getValidadeDias()));
        alunoPlano.setAtivo(true);

        alunoPlanoRepository.save(alunoPlano);

        return alunoRepository.findById(idAluno)
                .orElseThrow(() -> new AlunoNaoEncontrado(""));
    }

    public void desativarPlano(Integer idAluno, Integer idPlano, Integer idAlunoPlano){
        AlunoPlano alunoPlano = alunoPlanoRepository.findByIdAndAluno_IdAndPlano_Id(idAlunoPlano, idAluno, idPlano)
                .orElseThrow(() -> new AlunoPlanoNaoEncontrado(""));

        alunoPlano.setAtivo(false);

        alunoPlanoRepository.save(alunoPlano);
    }

    public void reativarPlano(Integer idAluno, Integer idPlano, Integer idAlunoPlano){
        AlunoPlano alunoPlano = alunoPlanoRepository.findByIdAndAluno_IdAndPlano_Id(idAlunoPlano, idAluno, idPlano)
                .orElseThrow(() -> new AlunoPlanoNaoEncontrado(""));

        alunoPlano.setAtivo(true);

        alunoPlanoRepository.save(alunoPlano);
    }

    public Aluno editar(Aluno aluno, Integer id){
        Optional<Aluno> alunoBD = alunoRepository.findById(id);

        if(alunoBD.isPresent()){
            aluno.setId(alunoBD.get().getId());

            return alunoRepository.save(aluno);
        }
        throw new AlunoNaoEncontrado("");
    }

    public void deletarPorId(Integer id){
        alunoRepository.deleteById(id);
    }
    public void inativarPorId(Integer id){

        Aluno aluno=alunoRepository.findById(id).orElseThrow
                (()->new AlunoNaoEncontrado("Aluno não encontrado"));
        aluno.setAtivo(false);
        alunoRepository.save(aluno);
    }
}
