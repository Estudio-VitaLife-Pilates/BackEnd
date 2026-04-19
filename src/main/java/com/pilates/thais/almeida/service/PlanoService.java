package com.pilates.thais.almeida.service;

import com.pilates.thais.almeida.entity.Plano;
import com.pilates.thais.almeida.repository.PlanoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanoService {

    private final PlanoRepository planoRepository;

    public PlanoService(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    public List<Plano> buscarTodos(){
        return planoRepository.findAll();
    }

    public Plano criar(Plano plano){
        return planoRepository.save(plano);
    }

}
