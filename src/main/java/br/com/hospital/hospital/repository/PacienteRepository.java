package br.com.hospital.hospital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.hospital.hospital.entity.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Integer>{

    Paciente findByUsuarioId(Integer id);
    Optional<Paciente> findByIdPaciente(Integer idPaciente);
}