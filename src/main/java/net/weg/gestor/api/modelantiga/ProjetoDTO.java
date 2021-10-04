package net.weg.gestor.api.modelantiga;

import lombok.Getter;
import lombok.Setter;
import net.weg.gestor.domain.entities.StatusProjeto;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ProjetoDTO {

    private Long id;
    private String nome;
    private LocalDate dataInicio;
    private LocalDate dataFinalizacao;
    private LocalDate dataCadastro;
    private int horasPrevistas;
    private int horasTrabalhadas;
    private int horasRestantes;
    private double valor;
    private String nomeSolicitante;
    private String nomeResponsavel;
    private double valorUtilizado;
    private double valorRestante;
    private StatusProjeto status;
    private String descricao;
    private List<SecoesDTO> secaos;
    private List<ConsultorDTO> usuarios;
    private int barraProgresso;


}
