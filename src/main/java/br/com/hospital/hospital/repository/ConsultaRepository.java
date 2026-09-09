package br.com.hospital.hospital.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.hospital.hospital.entity.Consulta;
import br.com.hospital.hospital.entity.Paciente;

public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {

    List<Consulta> findByPaciente(Paciente paciente);

    @Query("SELECT c.medico.nomeMedico, COUNT(c) FROM Consulta c " +
           "WHERE c.dataehoraConsulta BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY c.medico.nomeMedico")
    List<Object[]> countConsultasByMedicoAndPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.dataehoraConsulta BETWEEN :inicioDoDia AND :fimDoDia")
    int countConsultasDoDia(@Param("inicioDoDia") LocalDateTime inicioDoDia,
                            @Param("fimDoDia") LocalDateTime fimDoDia);

    // *** MÉTODO NOVO PARA CARREGAR CONSULTAS DO DIA ***
    @Query("SELECT c FROM Consulta c " +
       "JOIN FETCH c.paciente " +
       "WHERE c.dataehoraConsulta BETWEEN :inicio AND :fim " +
       "ORDER BY c.dataehoraConsulta ASC")
List<Consulta> buscarConsultasDoDia(@Param("inicio") LocalDateTime inicio,
                                    @Param("fim") LocalDateTime fim);

                                    // Use JOIN FETCH para carregar a entidade 'atendimento' junto com a 'consulta'
    @Query("SELECT c FROM Consulta c LEFT JOIN FETCH c.atendimento")
    List<Consulta> findAllWithAtendimento();

    @Query("select c from Consulta c join fetch c.paciente join fetch c.medico left join fetch c.atendimento where c.medico.idMedico = :medicoId order by c.dataehoraConsulta")
    List<Consulta> findByMedicoId(@Param("medicoId") Integer medicoId);

    @Query("select c from Consulta c join fetch c.paciente where c.medico.idMedico = :medicoId and c.dataehoraConsulta >= :inicio and c.dataehoraConsulta < :fim order by c.dataehoraConsulta")
    List<Consulta> findDayByMedicoId(@Param("medicoId") Integer medicoId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
