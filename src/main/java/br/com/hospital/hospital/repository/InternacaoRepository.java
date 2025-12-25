package br.com.hospital.hospital.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.hospital.hospital.entity.Internacao;
import br.com.hospital.hospital.entity.Paciente;

@Repository
public interface InternacaoRepository extends JpaRepository<Internacao, Integer> {

    @Query("SELECT i.leito.idQuarto FROM Internacao i WHERE i.status = :status")
    List<Integer> findIdQuartoByStatus(String status);

    Optional<Internacao> findByPacienteAndStatus(Paciente paciente, String status);

    // ✅ dataEntrada é LocalDateTime
    long countByDataEntradaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    // ✅ dataAlta é LocalDate
    long countByDataAltaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("SELECT COUNT(i) FROM Internacao i WHERE i.dataAlta IS NULL")
int countInternacoesAtivas();
}