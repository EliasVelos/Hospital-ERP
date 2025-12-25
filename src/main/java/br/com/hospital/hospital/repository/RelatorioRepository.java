package br.com.hospital.hospital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hospital.hospital.entity.Relatorio;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Integer> {

    /**
     * Busca o relatório mais recente salvo no banco de dados.
     * Usado para determinar a data de início do novo período de análise.
     */
    Optional<Relatorio> findTopByOrderByDataGeracaoDesc();
}