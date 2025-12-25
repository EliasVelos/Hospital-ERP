package br.com.hospital.hospital.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.hospital.hospital.entity.Consulta;
import br.com.hospital.hospital.entity.Paciente;
import br.com.hospital.hospital.repository.AtendimentoRepository;
import br.com.hospital.hospital.repository.ConsultaRepository;

@Service
public class ConsultaService{

    @Autowired
    private ConsultaRepository consultaRepository;
    @Autowired
    private AtendimentoRepository atendimentoRepository;
    //Salvar
    public Consulta save(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    //Listar
    public List<Consulta> findAll(){
        return consultaRepository.findAll();
    }

    //Excluir
    public void deleteById(Integer id){
        atendimentoRepository.deleteByConsulta_IdConsulta(id);
        consultaRepository.deleteById(id);
    }

    //Editar
    public Consulta findById(Integer id){
        return consultaRepository.findById(id).orElse(null);
    }

    public int contarConsultasDeHoje() {
    LocalDate hoje = LocalDate.now();
    LocalDateTime inicioDoDia = hoje.atStartOfDay(); // 00:00:00
    LocalDateTime fimDoDia = hoje.atTime(LocalTime.MAX);   // 23:59:59...

    return consultaRepository.countConsultasDoDia(inicioDoDia, fimDoDia);
}

public Optional<Consulta> buscarConsultaPorId(Integer id) {
    return consultaRepository.findById(id);
}

public List<Consulta> buscarPorPaciente(Paciente paciente) {
    return consultaRepository.findByPaciente(paciente);
}

    public void atualizarStatusConsulta(Integer idConsulta, String novoStatus) {
    Optional<Consulta> consultaOpt = consultaRepository.findById(idConsulta);
    if (consultaOpt.isPresent()) {
        Consulta c = consultaOpt.get();
        c.setStatusConsulta(novoStatus);
        consultaRepository.save(c);
    }
    }

     public List<Consulta> buscarProximasDoDia() {
        LocalDate hoje = LocalDate.now();

        LocalDateTime inicio = hoje.atStartOfDay();
        LocalDateTime fim = hoje.atTime(23, 59, 59);

        return consultaRepository.buscarConsultasDoDia(inicio, fim);
    }


// Novo método que traz a consulta JUNTO com o atendimento
public List<Consulta> buscarTodasComAtendimento() {
    // Esse método deve chamar aquele que você criou no Repository
    // Se no repository você chamou de 'findAllWithAtendimento', use assim:
    return consultaRepository.findAllWithAtendimento(); 
}
}