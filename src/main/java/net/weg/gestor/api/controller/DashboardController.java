package net.weg.gestor.api.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.weg.gestor.api.model.dashboard.DashboardProjetosDTO;
import net.weg.gestor.api.model.dashboard.DashboardVerbaDTO;
import net.weg.gestor.domain.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@AllArgsConstructor
@Getter
@Setter
public class DashboardController {

    private DashboardService dashboardService;

    @GetMapping("/verbas/{secaoId}")
    public DashboardVerbaDTO listarDashboard(@PathVariable Long secaoId){
        return dashboardService.listarVerbas(secaoId);
    }

    @GetMapping("/projetos/{secaoId}")
    public DashboardProjetosDTO listarProjetosDashboard(@PathVariable Long secaoId){
        return dashboardService.listarProjetos(secaoId);
    }

//    @GetMapping("/concluidos/7dias/{secaoId}")
//    public List<BasePorMesDashboardDTO> buscar7days(@PathVariable long secaoId) {
//        return dashboardService.buscar7days(secaoId);
//    }

//    @GetMapping("/concluidos/ultimoMes/{secaoId}")
//    public List<BasePorMesDashboardDTO> buscarUltimoMes(@PathVariable long secaoId) {
//        return dashboardService.buscarUltimoMes(secaoId);
//    }

//    @GetMapping("/concluidos/ultimos6meses/{secaoId}")
//    public List<BasePorMesDashboardDTO> buscarUltimos6meses(@PathVariable long secaoId) {
//        return dashboardService.buscarUltimos6meses(secaoId);
//    }

//    @GetMapping("/concluidos/ultimoano/{secaoId}")
//    public List<BasePorMesDashboardDTO> buscarUltimoAno(@PathVariable long secaoId) {
//        return dashboardService.buscarUltimoAno(secaoId);
//    }

}
