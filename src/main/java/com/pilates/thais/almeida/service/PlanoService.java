package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Plano;
import com.pilates.thais.almeida.exceptions.RecursoNaoEncontrado;
import com.pilates.thais.almeida.repository.PlanoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanoService {

    private final PlanoRepository planoRepository;

    public PlanoService(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    public List<Plano> buscarTodos(){
        return planoRepository.findAll();
    }

    public Plano buscarPorId(Integer id){
        Optional<Plano> planoOptional = planoRepository.findById(id);

        if(planoOptional.isPresent()){
            return planoOptional.get();
        }

        throw new RecursoNaoEncontrado("");
    }

    public Plano criar(Plano plano){
        return planoRepository.save(plano);
    }

    public void deletarPorId(Integer id){
        if(planoRepository.existsById(id)){
            planoRepository.deleteById(id);
        }else{
            throw new RecursoNaoEncontrado("");
        }
    }
}
