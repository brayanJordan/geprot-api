package net.weg.gestor.api.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BaseDashboardConcluidosDTO {

   private LocalDate data;

   private int quantidade;

}
