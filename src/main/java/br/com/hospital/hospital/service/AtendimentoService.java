package br.com.hospital.hospital.service;

import br.com.hospital.hospital.entity.Atendimento;
import br.com.hospital.hospital.entity.Consulta;
import br.com.hospital.hospital.repository.AtendimentoRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;
    @Autowired
    private ConsultaService consultaService;

    // A ASSINATURA FOI CORRIGIDA: Agora recebe apenas o objeto Atendimento.
    public Atendimento salvarAtendimento(Atendimento atendimento) {
        return atendimentoRepository.save(atendimento);
    }

    public Optional<Atendimento> findById(Integer idAtendimento) {
        return atendimentoRepository.findById(idAtendimento);
    }

    public Optional<Atendimento> buscarPorIdConsulta(Integer idConsulta) {
        // Lógica mantida para buscar a Consulta primeiro, se o Repository exigir o objeto Consulta
        Optional<Consulta> consultaOptional = consultaService.buscarConsultaPorId(idConsulta);

        if (consultaOptional.isPresent()) {
            // Assumindo que atendimentoRepository.findByIdConsulta espera o objeto Consulta
            return atendimentoRepository.findByConsulta(consultaOptional.get());
        }

        return Optional.empty();
    }

     
    @Transactional
    public void excluirAtendimentosDaConsulta(Integer idConsulta) {
        atendimentoRepository.deleteByConsulta_IdConsulta(idConsulta);
    }
}