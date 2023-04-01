package com.example.classificador.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClassificadorResponse {
    Integer numParcelas;
    Double valorParcela;
    Double valorDivida;
    Double saldoDevedor;
    Double rendaMensal;
}
