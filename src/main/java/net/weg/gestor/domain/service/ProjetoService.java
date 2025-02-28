package net.weg.gestor.domain.service;


import lombok.AllArgsConstructor;
import net.weg.gestor.api.map.ProjetoAssembler;
import net.weg.gestor.api.model.DashboardConcluidos;
import net.weg.gestor.api.model.DashboardConcluidosPorMes;
import net.weg.gestor.api.model.DashboardConcluidosPorPeriodo;
import net.weg.gestor.api.model.projeto.ProjetoAlocarDTO;
import net.weg.gestor.api.model.projeto.ProjetoCardDTO;
import net.weg.gestor.api.model.cadastrarprojetoinput.ProjetoCCPagantesInputDTO;
import net.weg.gestor.api.model.cadastrarprojetoinput.ProjetoInputDTO;
import net.weg.gestor.api.model.input.AlocarConsultorInputDTO;
import net.weg.gestor.api.model.projeto.ProjetoDetalhadoDTO;
import net.weg.gestor.api.model.projeto.ProjetoEditInputDTO;
import net.weg.gestor.domain.entities.*;
import net.weg.gestor.domain.exception.NegocioException;
import net.weg.gestor.domain.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class ProjetoService {

    private ProjetoRepository projetoRepository;
    private ProjetoAssembler projetoAssembler;
    private ConsultorRepository consultorRepository;
    private SkillRepository skillRepository;
    private CCPagantesService ccPagantesService;
    private ConsultoresAlocadosService consultoresAlocadosService;
    private ConsultorAlocadoRepository consultorAlocadoRepository;

    public List<Projeto> buscarTodosProjetoSecao(Long secaoId) {
        List<CCPagantes> ccPagantes = ccPagantesService.buscarPorSecao(secaoId);
        List<Projeto> projetos = new ArrayList<>();
        ccPagantes.forEach(ccPagante -> {
            projetos.add(projetoRepository.findById(ccPagante.getProjeto().getId()).orElseThrow(
                    () -> new NegocioException("Projeto nao encontrado")
            ));
        });
        return projetos;
    }

    public String editarProjeto(Long projetoId, ProjetoEditInputDTO infosEditar) {
        Projeto projeto = projetoRepository.findById(projetoId).orElseThrow(() -> new NegocioException(
                "Nao existe um projeto com esse ID"
        ));

        projeto.setNome(infosEditar.getNome());
        projeto.setDescricao(infosEditar.getDescricao());
        projeto.setDataFinalizacao(infosEditar.getDataEncerramento());
        projeto.setHorasPrevistas(infosEditar.getHorasAprovadas());
        projeto.setValor(infosEditar.getVerbasAprovadas());

        consultorAlocadoRepository.deletarConsultoresProjeto(projeto);
        infosEditar.getConsultores().forEach(consultorEditarProjetoDTO -> {
            Consultor consultor = consultorRepository.buscarConsultorPeloId(consultorEditarProjetoDTO.getId());
            List<Skill> todasAsSkills = skillRepository.findAll();
            if (consultorEditarProjetoDTO.getSkill_id() == 18) {
                ConsultorAlocado consultorAlocado = new ConsultorAlocado(projeto, consultor, consultorEditarProjetoDTO.getHoras(), todasAsSkills.get(17));
                consultorAlocadoRepository.save(consultorAlocado);
            } else {
                ConsultorAlocado consultorAlocado = new ConsultorAlocado(projeto, consultor, consultorEditarProjetoDTO.getHoras(), todasAsSkills.get(consultorEditarProjetoDTO.getSkill_id() - 1));
                consultorAlocadoRepository.save(consultorAlocado);
            }
        });

        projetoRepository.save(projeto);
        return "Projeto editado";
    }

    public ProjetoDetalhadoDTO buscarPorId(Long projetoId) {
        Projeto projeto = projetoRepository.findById(projetoId).orElseThrow(() -> new NegocioException("Projeto nao encontrado com esse ID"));
        return projetoAssembler.toModelDetalhado(projeto);
    }

    public List<ProjetoCardDTO> listarPorSecao(Long secaoId) {
        return projetoAssembler.toCollectionModel(buscarTodosProjetoSecao(secaoId));
    }

    public List<ProjetoCardDTO> buscarPorNome(Long secaoId, String pesquisaPorNome) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getNome().toLowerCase(Locale.ROOT).contains(pesquisaPorNome.toLowerCase(Locale.ROOT))) {
                projetosFiltrados.add(projeto);
            }
        });
        return projetoAssembler.toCollectionModel(projetosFiltrados);
    }

    public List<ProjetoCardDTO> buscarPorNomeResponsavel(Long secaoId, String pesquisaPorNomeResponsavel) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getNomeResponsavel().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNomeResponsavel.toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });
        return projetoAssembler.toCollectionModel(projetosFiltrados);
    }

    public List<ProjetoCardDTO> buscarPorId(Long secaoId, Long pesquisaPorId) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getId().toString().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorId.toString().toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });
        return projetoAssembler.toCollectionModel(projetosFiltrados);
    }

    public List<ProjetoCardDTO> buscarPorNomeENomeResponsavel(
            Long secaoId,
            String pesquisaPorNome,
            String pesquisaPorNomeResponsavel
    ) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getNome().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNome.toLowerCase(Locale.ROOT)) &&
                projeto.getNomeResponsavel().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNomeResponsavel.toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });
        return projetoAssembler.toCollectionModel(projetosFiltrados);
    }

    public List<ProjetoCardDTO> buscarPorNomeEId(
            Long secaoId,
            String pesquisaPorNome,
            Long pesquisaPorId
    ) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getNome().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNome.toLowerCase(Locale.ROOT)) &&
                projeto.getId().toString().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorId.toString().toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });
        return projetoAssembler.toCollectionModel(projetosFiltrados);
    }

    public List<ProjetoCardDTO> buscarPorNomeResponsavelEId(
            Long secaoId,
            String pesquisaPorNomeResponsavel,
            Long pesquisaPorId
    ) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getNomeResponsavel().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNomeResponsavel.toLowerCase(Locale.ROOT)) &&
                projeto.getId().toString().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorId.toString().toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });
        return projetoAssembler.toCollectionModel(projetosFiltrados);
    }

    public List<ProjetoCardDTO> buscarPorNomeNomeResponsavelEId(
            Long secaoId,
            String pesquisaPorNomeResponsavel,
            String pesquisaPorNome,
            Long pesquisaPorId
    ) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getNomeResponsavel().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNomeResponsavel.toLowerCase(Locale.ROOT)) &&
                projeto.getId().toString().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorId.toString().toLowerCase(Locale.ROOT)) &&
                projeto.getNome().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNome.toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });
        return projetoAssembler.toCollectionModel(projetosFiltrados);
    }



    public List<ProjetoCardDTO> buscarPorNomeEStatus(Long secaoId, String pesquisaPorNome, int status) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        StatusProjeto statusConvertido = convertFilter(status);
        todosProjetos.forEach(projeto -> {
            if (projeto.getNome().toLowerCase(Locale.ROOT).contains(pesquisaPorNome.toLowerCase(Locale.ROOT)) &&
                    projeto.getStatus().equals(statusConvertido)
            ) {
                projetosFiltrados.add(projeto);
            }
        });
        return projetoAssembler.toCollectionModel(projetosFiltrados);
    }

    public List<ProjetoCardDTO> buscarPorStatus(Long secaoId, int status) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        StatusProjeto statusConvertido = convertFilter(status);
        todosProjetos.forEach(projeto -> {
            if (projeto.getStatus().equals(statusConvertido)) {
                projetosFiltrados.add(projeto);
            }
        });
        return projetoAssembler.toCollectionModel(projetosFiltrados);
    }


    public List<ProjetoAlocarDTO> alocBuscarProjeto(Long consultorId, Long secaoId) {
        Consultor consultor = consultorRepository.findById(consultorId).orElseThrow(
                () -> new NegocioException("Consultor nao encontrado")
        );

        return projetoAssembler.toCollectionModelProjetosAlocar(buscarTodosProjetoSecao(secaoId), consultor);
    }

    public List<ProjetoAlocarDTO> alocBuscarProjetoComNomeResponsavel(
            Long consultorId,
            Long secaoId,
            String pesquisaPorNomeResponsavel
    ) {
        Consultor consultor = consultorRepository.findById(consultorId).orElseThrow(
                () -> new NegocioException("Consultor nao encontrado")
        );

        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getNomeResponsavel().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNomeResponsavel.toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });

        return projetoAssembler.toCollectionModelProjetosAlocar(projetosFiltrados, consultor);
    }

    public List<ProjetoAlocarDTO> alocBuscarProjetoComId(
            Long consultorId,
            Long secaoId,
            String pesquisaPorId
    ) {
        Consultor consultor = consultorRepository.findById(consultorId).orElseThrow(
                () -> new NegocioException("Consultor nao encontrado")
        );

        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getId().toString().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorId.toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });

        return projetoAssembler.toCollectionModelProjetosAlocar(projetosFiltrados, consultor);
    }

    public List<ProjetoAlocarDTO> alocBuscarProjetoComNomeProjeto(
            Long consultorId,
            Long secaoId,
            String pesquisaPorNomeProjeto
    ) {
        Consultor consultor = consultorRepository.findById(consultorId).orElseThrow(
                () -> new NegocioException("Consultor nao encontrado")
        );

        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getNome().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNomeProjeto.toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });

        return projetoAssembler.toCollectionModelProjetosAlocar(projetosFiltrados, consultor);
    }

    public List<ProjetoAlocarDTO> alocBuscarProjetoComIdeNome(
            Long consultorId,
            Long secaoId,
            String pesquisaPorId,
            String pesquisaPorNomeProjeto
    ) {
        Consultor consultor = consultorRepository.findById(consultorId).orElseThrow(
                () -> new NegocioException("Consultor nao encontrado")
        );

        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getId().toString().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorId.toLowerCase(Locale.ROOT)) &&
                projeto.getNome().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNomeProjeto.toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });

        return projetoAssembler.toCollectionModelProjetosAlocar(projetosFiltrados, consultor);
    }

    public List<ProjetoAlocarDTO> alocBuscarProjetoComIdeNomeResponsavel(
            Long consultorId,
            Long secaoId,
            String pesquisaPorId,
            String pesquisaPorNomeResponsavel
    ) {
        Consultor consultor = consultorRepository.findById(consultorId).orElseThrow(
                () -> new NegocioException("Consultor nao encontrado")
        );

        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getId().toString().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorId.toLowerCase(Locale.ROOT)) &&
                projeto.getNomeResponsavel().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNomeResponsavel.toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });

        return projetoAssembler.toCollectionModelProjetosAlocar(projetosFiltrados, consultor);
    }

    public List<ProjetoAlocarDTO> alocBuscarProjetoComNomeeNomeResponsavel(
            Long consultorId,
            Long secaoId,
            String pesquisaPorNome,
            String pesquisaPorNomeResponsavel
    ) {
        Consultor consultor = consultorRepository.findById(consultorId).orElseThrow(
                () -> new NegocioException("Consultor nao encontrado")
        );

        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getNome().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNome.toLowerCase(Locale.ROOT)) &&
                projeto.getNomeResponsavel().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNomeResponsavel.toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });

        return projetoAssembler.toCollectionModelProjetosAlocar(projetosFiltrados, consultor);
    }

    public List<ProjetoAlocarDTO> alocBuscarProjetoComNomeeNomeResponsaveleId(
            Long consultorId,
            Long secaoId,
            String pesquisaPorNome,
            String pesquisaPorNomeResponsavel,
            String pesquisaPorId
    ) {
        Consultor consultor = consultorRepository.findById(consultorId).orElseThrow(
                () -> new NegocioException("Consultor nao encontrado")
        );

        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<Projeto> projetosFiltrados = new ArrayList<>();
        todosProjetos.forEach(projeto -> {
            if (projeto.getNome().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNome.toLowerCase(Locale.ROOT)) &&
                projeto.getNomeResponsavel().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorNomeResponsavel.toLowerCase(Locale.ROOT)) &&
                projeto.getId().toString().toLowerCase(Locale.ROOT).contains(
                    pesquisaPorId.toLowerCase(Locale.ROOT))
            ) {
                projetosFiltrados.add(projeto);
            }
        });

        return projetoAssembler.toCollectionModelProjetosAlocar(projetosFiltrados, consultor);
    }

    public String cadastrarProjeto(ProjetoInputDTO projeto) {
//        Chama o método void que faz as verificações se é possível cadastrar esse projeto
        projetoValidations(projeto);

//        Salva o projeto e pega o ID para usar nas entidades fracas
        Long projetoId = projetoRepository.save(projetoAssembler.toEntityCadastro(projeto)).getId();

//        Pega a lista de cc pagantes e manda para um método próprio as salvar
        ccPagantesService.saveCCPagantesProjeto(projeto.getCcpagantes(), projetoId);

//        Pega consultor por consultor dos escolhidos e salva em consultores alocados
        projeto.getConsultores().forEach(consultor -> {
            consultoresAlocadosService.alocarConsultor(new AlocarConsultorInputDTO(
                    consultor.getConsultorId(),
                    projetoId,
                    consultor.getQuantidadeHoras(),
                    consultor.getNumeroDaSkill()
                )
            );
        });
        return "Projeto cadastrado";
    }

    private void projetoValidations(ProjetoInputDTO projeto) {
        if (projeto.getCcpagantes().size() == 0) {
            throw new NegocioException("Voce nao alocou nenhum centro de custo pagante");
        }

        if (projeto.getConsultores().size() == 0) {
            throw new NegocioException("Voce nao alocou nenhum consultor");
        }

        int taxa = projeto.getCcpagantes().stream().mapToInt(ProjetoCCPagantesInputDTO::getTaxa).sum();

        if (taxa != 100) {
            throw new NegocioException("Taxa é diferente de 100%");
        }

        projeto.getConsultores().forEach(consultorInputDTO -> {
            if (!consultorRepository.existsById(consultorInputDTO.getConsultorId())) {
                throw new NegocioException("Nao existe um consultor com algum ID informado");
            }
        });
    }

    private StatusProjeto convertFilter(int filtroInteiro) {
        switch (filtroInteiro) {
            case 1:
                return StatusProjeto.ATRASADO;
            case 2:
                return StatusProjeto.CONCLUIDO;
            case 3:
                return StatusProjeto.EM_ANDAMENTO;
            case 4:
                return StatusProjeto.NAO_INICIADO;
            default:
                return null;
        }
    }

    private List<DashboardConcluidos> mapearUltimos7Dias() {
        List<DashboardConcluidos> ultimos7dias = new ArrayList<>();
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 7; ++i) {
            date = date.minusDays(1);
            ultimos7dias.add(new DashboardConcluidos(date.format(DateTimeFormatter.ofPattern("dd/MM")), 0));
        }
        return ultimos7dias;
    }

    private int converterParaODiaDaListaUltimos7Dias(LocalDate dataProjeto) {
        for (int i = 0; i < 7; ++i) {
            if (dataProjeto.isEqual(LocalDate.now().minusDays((i + 1)))) {
                return i;
            }
        }

        return 1000;
    }

    public List<DashboardConcluidos> concluidosUltimos7Dias(Long secaoId) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<DashboardConcluidos> ultimos7dias = mapearUltimos7Dias();
        todosProjetos.forEach(projeto -> {
            if (projeto.getDataFinalizacao() != null) {
                if (projeto.getDataFinalizacao().isBefore(LocalDate.now()) && projeto.getDataFinalizacao().isAfter(LocalDate.now().minusDays(8))) {
                    ultimos7dias.get(converterParaODiaDaListaUltimos7Dias(projeto.getDataFinalizacao())).setQuantidadeConcluidos(
                    ultimos7dias.get(converterParaODiaDaListaUltimos7Dias(projeto.getDataFinalizacao())).getQuantidadeConcluidos() + 1);
                }
            }
        });

        return ultimos7dias;
    }

    private List<DashboardConcluidosPorPeriodo> mapearUltimoMes() {
        List<DashboardConcluidosPorPeriodo> ultimoMes = new ArrayList<>();
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 4; ++i) {
            if (i != 0) {
                date = date.minusDays(7);
            }
            ultimoMes.add(new DashboardConcluidosPorPeriodo((date.minusDays(7).format(DateTimeFormatter.ofPattern("dd/MM"))) + "-" + (date.minusDays(1).format(DateTimeFormatter.ofPattern("dd/MM"))), 0));
        }
        return ultimoMes;
    }

    private int converterParaODiaDaListaUltimoMes(LocalDate dataProjeto) {
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 4; ++i) {
            if (i != 0) {
                date = date.minusDays(7);
            }
            if ((dataProjeto.isBefore(date) || dataProjeto.isEqual(date)) && dataProjeto.isAfter((date.minusDays(7)))) {
                return i;
            }
        }

        return 1000;
    }

    public List<DashboardConcluidosPorPeriodo> concluidosUltimoMes(Long secaoId) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<DashboardConcluidosPorPeriodo> ultimoMes = mapearUltimoMes();
        todosProjetos.forEach(projeto -> {
            if (projeto.getDataFinalizacao() != null) {
                if (projeto.getDataFinalizacao().isBefore(LocalDate.now()) && projeto.getDataFinalizacao().isAfter(LocalDate.now().minusDays(28))) {
                    ultimoMes.get(converterParaODiaDaListaUltimoMes(projeto.getDataFinalizacao())).setQuantidadeConcluidos(
                    ultimoMes.get(converterParaODiaDaListaUltimoMes(projeto.getDataFinalizacao())).getQuantidadeConcluidos() + 1);
                }
            }
        });

        return ultimoMes;
    }

    private List<DashboardConcluidosPorMes> mapear6meses() {
        List<DashboardConcluidosPorMes> ultimos6meses = new ArrayList<>();
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 6; ++i) {
            if (i != 0) {
                date = date.minusMonths(1);
            }
            String mesCerto = date.format(DateTimeFormatter.ofPattern("MM/yyyy"));
            ultimos6meses.add(new DashboardConcluidosPorMes(mesCerto, 0));
        }

        return ultimos6meses;
    }

    private int converterParaODiaDaListaUltimos6Mes(LocalDate dataProjeto) {
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 6; ++i) {
            if (i != 0) {
                date = date.minusMonths(1);
            }
            if (dataProjeto.getMonth().equals(date.getMonth())) {
                return i;
            }
        }

        return 1000;
    }

    public List<DashboardConcluidosPorMes> concluidosUltimos6Mes(Long secaoId) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<DashboardConcluidosPorMes> ultimos6meses = mapear6meses();
        todosProjetos.forEach(projeto -> {
            if (projeto.getDataFinalizacao() != null) {
                if ((projeto.getDataFinalizacao().isBefore(LocalDate.now()) || projeto.getDataFinalizacao().isEqual(LocalDate.now())) && projeto.getDataFinalizacao().isAfter(LocalDate.now().minusMonths(6))) {
                    ultimos6meses.get(converterParaODiaDaListaUltimos6Mes(projeto.getDataFinalizacao())).setQuantidadeConcluidos(
                    ultimos6meses.get(converterParaODiaDaListaUltimos6Mes(projeto.getDataFinalizacao())).getQuantidadeConcluidos() + 1);
                }
            }
        });

        return ultimos6meses;
    }

    private List<DashboardConcluidosPorMes> mapear12meses() {
        List<DashboardConcluidosPorMes> ultimos6meses = new ArrayList<>();
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 12; ++i) {
            if (i != 0) {
                date = date.minusMonths(1);
            }
            String mesCerto = date.format(DateTimeFormatter.ofPattern("MM/yyyy"));
            ultimos6meses.add(new DashboardConcluidosPorMes(mesCerto, 0));
        }

        return ultimos6meses;
    }

    private int converterParaODiaDaListaUltimos12Mes(LocalDate dataProjeto) {
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 12; ++i) {
            if (i != 0) {
                date = date.minusMonths(1);
            }
            if (dataProjeto.getMonth().equals(date.getMonth())) {
                return i;
            }
        }

        return 1000;
    }

    public List<DashboardConcluidosPorMes> concluidosUltimos12Mes(Long secaoId) {
        List<Projeto> todosProjetos = buscarTodosProjetoSecao(secaoId);
        List<DashboardConcluidosPorMes> ultimos12meses = mapear12meses();
        todosProjetos.forEach(projeto -> {
            if (projeto.getDataFinalizacao() != null) {
                if ((projeto.getDataFinalizacao().isBefore(LocalDate.now()) || projeto.getDataFinalizacao().isEqual(LocalDate.now())) && projeto.getDataFinalizacao().isAfter(LocalDate.now().minusMonths(6))) {
                    ultimos12meses.get(converterParaODiaDaListaUltimos6Mes(projeto.getDataFinalizacao())).setQuantidadeConcluidos(
                    ultimos12meses.get(converterParaODiaDaListaUltimos6Mes(projeto.getDataFinalizacao())).getQuantidadeConcluidos() + 1);
                }
            }
        });

        return ultimos12meses;
    }

}
