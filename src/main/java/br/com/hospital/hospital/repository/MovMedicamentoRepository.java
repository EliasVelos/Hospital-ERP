package br.com.hospital.hospital.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.hospital.hospital.entity.MovMedicamento;

public interface MovMedicamentoRepository extends JpaRepository<MovMedicamento, Integer> {

    Page<MovMedicamento> findByMedicamentoNomeMedicamentoContainingIgnoreCase(String nomeMedicamento, Pageable pageable);

    @Query("SELECT m.medicamento.nomeMedicamento, SUM(m.quantidadeMovimentada) FROM MovMedicamento m " +
           "WHERE m.dataMovimentacao BETWEEN :dataInicio AND :dataFim " +
           "AND m.tipoMovimentacao = 'SAIDA' " + 
           "GROUP BY m.medicamento.nomeMedicamento")
    List<Object[]> sumSaidasByMedicamentoAndPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim);
}