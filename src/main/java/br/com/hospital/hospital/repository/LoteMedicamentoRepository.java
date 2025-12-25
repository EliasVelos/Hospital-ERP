package br.com.hospital.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import br.com.hospital.hospital.entity.LoteMedicamento;
import java.time.LocalDate;

public interface LoteMedicamentoRepository extends JpaRepository<LoteMedicamento, Long> { 
    
    // 🚨 Este método é crucial e o nome deve ser exato (ou usar @Query)
    // Busca lotes com estoque > 0 e validade >= hoje, ordenados por validade (FEFO)
    List<LoteMedicamento> findByMedicamento_IdMedicamentoAndQuantidadeEmEstoqueGreaterThanAndDataValidadeGreaterThanEqualOrderByDataValidadeAsc(
        Integer idMedicamento, Integer quantidade, LocalDate dataHoje);
    
    // ... e o método que lista todos os lotes de um medicamento (útil para o service)
    List<LoteMedicamento> findByMedicamento_IdMedicamentoOrderByDataValidadeAsc(Integer idMedicamento);
}