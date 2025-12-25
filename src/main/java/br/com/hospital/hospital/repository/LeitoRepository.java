package br.com.hospital.hospital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.hospital.hospital.entity.Leito;

public interface LeitoRepository extends JpaRepository<Leito, Integer>{

    List<Leito> findByIdQuartoNotIn(List<Integer> leitoIds);

    @Query("SELECT i.leito.idQuarto FROM Internacao i WHERE i.leito.idQuarto = :leitoId AND i.status = 'Ativa'")
    Optional<Integer> findInternacaoAtivaByLeitoId(@Param("leitoId") Integer leitoId);
}