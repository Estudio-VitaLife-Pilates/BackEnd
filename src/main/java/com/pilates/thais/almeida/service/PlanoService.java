package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Plano;
import com.pilates.thais.almeida.exceptions.PlanoNaoEncontrado;
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

        throw new PlanoNaoEncontrado("");
    }

    public Plano criar(Plano plano){
        return planoRepository.save(plano);
    }

    public Plano editarPorId(Plano plano, Integer id){
        Optional<Plano> planoBD = planoRepository.findById(id);

        if(planoBD.isPresent()){
            plano.setId(planoBD.get().getId());
            return planoRepository.save(plano);
        }
        throw new PlanoNaoEncontrado("");
    }

    public void deletarPorId(Integer id){
        if(planoRepository.existsById(id)){
            planoRepository.deleteById(id);
        }else{
            throw new PlanoNaoEncontrado("");
        }
    }
}
