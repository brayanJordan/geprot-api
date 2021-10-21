package net.weg.gestor.api.controller;

import lombok.AllArgsConstructor;
import net.weg.gestor.api.map.ConsultorAssembler;
import net.weg.gestor.api.map.UsuarioAssembler;
import net.weg.gestor.api.model.ConsultorDTO;
import net.weg.gestor.api.model.ConsultorNaoAlocadoDTO;
import net.weg.gestor.api.model.input.AlocarConsultorInputDTO;
import net.weg.gestor.api.model.input.ConsultorInputDTO;
import net.weg.gestor.domain.entities.Consultor;
import net.weg.gestor.domain.entities.RoleUsuarios;
import net.weg.gestor.domain.entities.Usuario;
import net.weg.gestor.domain.repository.FornecedorRepository;
import net.weg.gestor.domain.service.ConsultorService;
import net.weg.gestor.domain.service.ConsultoresAlocadosService;
import net.weg.gestor.domain.service.RoleUsuarioService;
import net.weg.gestor.domain.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultor")
@AllArgsConstructor
public class ConsultorController {

    private ConsultoresAlocadosService consultoresAlocadosService;
    private UsuarioAssembler usuarioAssembler;
    private UsuarioService usuarioService;
    private RoleUsuarioService roleUsuarioService;
    private ConsultorAssembler consultorAssembler;
    private ConsultorService consultorService;
    private FornecedorRepository fornecedorRepository;

    @PostMapping("/cadastrar")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultorDTO cadastrar(@RequestBody ConsultorInputDTO consultor) {
        Usuario newUsuario = usuarioAssembler.toEntity(consultor.getUsuario());
        RoleUsuarios novaRole = new RoleUsuarios();
        newUsuario.setSenha(new BCryptPasswordEncoder().encode(consultor.getUsuario().getSenha()));
        Usuario usuario1 = usuarioService.cadastrar(newUsuario);
        novaRole.setUsuarios_id(usuario1.getId());
        novaRole.setRole_nome("ROLE_CONSULTOR");
        roleUsuarioService.cadastrar(novaRole);
        Consultor consultorRecebeDados = consultorAssembler.toEntity(consultor);
        consultorRecebeDados.setUsuario(usuario1);
        consultorRecebeDados.setFornecedor(fornecedorRepository.findByIdFornecedor(consultor.getFornecedor().getId()));
        Consultor consultor1 = consultorService.cadastrar(consultorRecebeDados);
        return consultorAssembler.toModel(consultor1);
    }

    @GetMapping("/buscar/{consultorId}")
    public ConsultorDTO buscarConsultorUnicoPorId(@PathVariable long consultorId){
        return consultorService.buscarConsultor(consultorId);
    }

    @GetMapping("/listar")
    public List<ConsultorNaoAlocadoDTO> buscarConsultores() {
        return consultoresAlocadosService.buscarConsultores();
    }

    @GetMapping("/buscartodos/{pesquisarPorId}")
    public List<ConsultorNaoAlocadoDTO> buscarConsultoresPorId(@PathVariable Long pesquisaPorId) {
        return consultoresAlocadosService.buscarConsultoresPorId(pesquisaPorId);
    }

    @GetMapping("/buscar/{pesquisaPorNome}")
    public List<ConsultorNaoAlocadoDTO> buscarConsultoresPorNome(@PathVariable String pesquisaPorNome) {
        return consultoresAlocadosService.buscarConsultoresPorNome(pesquisaPorNome);
    }

    @GetMapping("/buscar/{pesquisaPorNomeFornecedor}")
    public List<ConsultorNaoAlocadoDTO> buscarConsultoresPorNomeFornecedor(@PathVariable String pesquisaPorNomeFornecedor) {
        return consultoresAlocadosService.buscarConsultoresPorNomeFornecedor(pesquisaPorNomeFornecedor);
    }

    @PostMapping("/alocar")
    public String alocarConsultor(@RequestBody AlocarConsultorInputDTO alocar) {
        return consultoresAlocadosService.alocarConsultor(alocar);
    }






}
