package br.com.hospital.hospital.repository;

import br.com.hospital.hospital.entity.Atendimento;
import br.com.hospital.hospital.entity.Consulta;
import br.com.hospital.hospital.entity.Paciente;
import io.micrometer.core.instrument.Meter.Id;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Integer> {

    Optional<Atendimento> findById(Integer id);

    Optional<Atendimento> findByConsulta(Consulta consulta);

     void deleteByConsulta_IdConsulta(Integer idConsulta);
}