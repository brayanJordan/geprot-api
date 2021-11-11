package net.weg.gestor.domain.service;

import lombok.AllArgsConstructor;
import net.weg.gestor.api.model.input.SkillInput;
import net.weg.gestor.domain.entities.Skill;
import net.weg.gestor.domain.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SkillService {

    private SkillRepository skillRepository;

    public List<Skill> buscarSkillsSelecionadas(List<SkillInput> idDasSkills) {
        List<Skill> skillsSelecionadas = new ArrayList<>();
        idDasSkills.forEach(skillInput -> {
            skillsSelecionadas.add(skillRepository.buscarPorId(skillInput.getId()));
        });

        return skillsSelecionadas;
    }

}
