package net.weg.gestor.api.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BasePorPeriodoDashboardDTO {

    private LocalDate inicioPeriodo;

    private LocalDate finalPeriodo;

    private int quantidade;

}
