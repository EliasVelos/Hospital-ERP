package br.com.hospital.hospital.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hospital.hospital.entity.Medicamento;
import br.com.hospital.hospital.entity.MovMedicamento;


public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> { 
    
    // ✅ CORRETO: Buscar diretamente pela propriedade 'nomeMedicamento'
    // O prefixo 'Medicamento' só é necessário se a busca fosse feita através de uma relação (ex: findByMovimentacao_Medicamento_NomeMedicamento)
    Page<Medicamento> findByNomeMedicamentoContainingIgnoreCase(String nome, Pageable pageable);
    
    List<Medicamento> findByAtivoTrue();
}