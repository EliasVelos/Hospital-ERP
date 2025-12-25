package br.com.hospital.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.hospital.hospital.entity.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Integer>{

}