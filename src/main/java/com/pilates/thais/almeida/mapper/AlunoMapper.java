package com.pilates.thais.almeida.mapper;

import com.pilates.thais.almeida.dto.aluno.AlunoDetailsResponseDto;
import com.pilates.thais.almeida.dto.aluno.AlunoRequestDto;
import com.pilates.thais.almeida.dto.aluno.AlunoResponseBaseDto;
import com.pilates.thais.almeida.dto.aluno.AlunoResponseDto;
import com.pilates.thais.almeida.dto.endereco.EnderecoDto;
import com.pilates.thais.almeida.entity.Aluno;
import com.pilates.thais.almeida.entity.AlunoPlano;
import com.pilates.thais.almeida.entity.Endereco;
import java.time.LocalDate;
import java.util.List;

public class AlunoMapper {

    public static AlunoResponseDto toResponse(Aluno entity){
        AlunoResponseDto dto = new AlunoResponseDto();
        preencherResponseBase(dto, entity);
        dto.setVencimentoPlanoAtivo(
                entity.getAlunoPlanos().stream()
                        .filter(ap -> Boolean.TRUE.equals(ap.getAtivo()))
                        .map(AlunoPlano::getDataFim)
                        .findFirst()
                        .orElse(null)
        );
        return dto;
    }

    public static AlunoDetailsResponseDto toResponseDetails(Aluno entity){
        AlunoDetailsResponseDto dto = new AlunoDetailsResponseDto();
        preencherResponseBase(dto, entity);
        dto.setPlanos(entity.getAlunoPlanos().stream().map(AlunoMapper::toPlanoResponse).toList());
        return dto;
    }

    private static void preencherResponseBase(AlunoResponseBaseDto dto, Aluno entity) {
        dto.setNome(entity.getNome());
        dto.setCpf(entity.getCpf());
        dto.setEmail(entity.getEmail());
        dto.setAtivo(entity.getAtivo());
        dto.setDataCadastro(entity.getDataCadastro());
        dto.setId(entity.getId());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setTelefone(entity.getTelefone());
        dto.setFichaAnamnese(entity.getFichaAnamnese());
        dto.setEndereco(toEnderecoDto(entity.getEndereco()));
    }

    public static List<AlunoResponseDto> toResponse(List<Aluno> entities){
        return entities.stream().map(AlunoMapper::toResponse).toList();
    }

    public static Aluno toEntity(AlunoRequestDto dto){
        Aluno aluno = new Aluno();
        aluno.setNome(dto.getNome());
        aluno.setAtivo(true);
        aluno.setCpf(dto.getCpf());
        aluno.setEmail(dto.getEmail());
        aluno.setTelefone(dto.getTelefone());
        aluno.setDataCadastro(LocalDate.now());
        aluno.setDataNascimento(dto.getDataNascimento());
        aluno.setFichaAnamnese(dto.getFichaAnamnese());
        aluno.setEndereco(toEnderecoEntity(dto.getEndereco()));
        return aluno;
    }

    public static Endereco toEnderecoEntity(EnderecoDto dto) {
        if (dto == null || isEnderecoVazio(dto)) {
            return null;
        }
        Endereco endereco = new Endereco();
        copiarEndereco(dto, endereco);
        return endereco;
    }

    public static EnderecoDto toEnderecoDto(Endereco entity) {
        if (entity == null) {
            return null;
        }
        EnderecoDto dto = new EnderecoDto();
        copiarEndereco(entity, dto);
        return dto;
    }

    private static void copiarEndereco(com.pilates.thais.almeida.entity.EnderecoDados origem, com.pilates.thais.almeida.entity.EnderecoDados destino) {
        destino.setCep(origem.getCep());
        destino.setLogradouro(origem.getLogradouro());
        destino.setBairro(origem.getBairro());
        destino.setCidade(origem.getCidade());
        destino.setEstado(origem.getEstado());
        destino.setNumero(origem.getNumero());
        destino.setComplemento(origem.getComplemento());
    }

    public static boolean isEnderecoVazio(EnderecoDto dto) {
        return isBlank(dto.getCep()) && isBlank(dto.getLogradouro()) && isBlank(dto.getBairro()) &&
                isBlank(dto.getCidade()) && isBlank(dto.getEstado()) && isBlank(dto.getNumero()) && isBlank(dto.getComplemento());
    }

    private static boolean isBlank(String v) { return v == null || v.isBlank(); }

    private static AlunoDetailsResponseDto.AlunoPlanoResponseDto toPlanoResponse(AlunoPlano entity){
        AlunoDetailsResponseDto.AlunoPlanoResponseDto dto = new AlunoDetailsResponseDto.AlunoPlanoResponseDto();
        dto.setIdPlano(entity.getPlano().getId());
        dto.setIdAlunoPlano(entity.getId());
        dto.setNome(entity.getPlano().getNome());
        dto.setFrequenciaSemanal(entity.getPlano().getFrequenciaSemanal());
        dto.setValidadeDias(entity.getPlano().getValidadeDias());
        dto.setValorMensal(entity.getPlano().getValorMensal());
        dto.setDataInicio(entity.getDataInicio());
        dto.setDataFim(entity.getDataFim());
        dto.setAtivo(entity.getAtivo());
        return dto;
    }

    public static List<AlunoDetailsResponseDto> toResponseDetails(List<Aluno> entities){
        return entities.stream().map(AlunoMapper::toResponseDetails).toList();
    }
}
