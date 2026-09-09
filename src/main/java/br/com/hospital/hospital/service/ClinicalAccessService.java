package br.com.hospital.hospital.service;

import br.com.hospital.hospital.entity.Atendimento;
import br.com.hospital.hospital.entity.Consulta;
import br.com.hospital.hospital.repository.AtendimentoRepository;
import br.com.hospital.hospital.repository.ConsultaRepository;
import br.com.hospital.hospital.security.HospitalPrincipal;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClinicalAccessService {
    private final ConsultaRepository consultas;
    private final AtendimentoRepository atendimentos;

    public ClinicalAccessService(ConsultaRepository consultas, AtendimentoRepository atendimentos) {
        this.consultas = consultas;
        this.atendimentos = atendimentos;
    }

    @Transactional(readOnly = true)
    public Consulta consultation(Integer id, HospitalPrincipal user) {
        Consulta consulta = consultas.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireAccess(consulta, user);
        return consulta;
    }

    @Transactional(readOnly = true)
    public Atendimento attendance(Integer id, HospitalPrincipal user) {
        Atendimento atendimento = atendimentos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireAccess(atendimento.getConsulta(), user);
        return atendimento;
    }

    @Transactional
    public void record(Integer consultaId, Atendimento form, HospitalPrincipal user) {
        Consulta consulta = consultation(consultaId, user);
        if (!"AGENDADA".equals(consulta.getStatusConsulta()) || atendimentos.findByConsulta(consulta).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A consulta já possui atendimento.");
        }
        // IDs e vínculos enviados pelo navegador não fazem parte do registro clínico.
        Atendimento atendimento = new Atendimento();
        atendimento.setConsulta(consulta);
        atendimento.setQueixaPrincipal(form.getQueixaPrincipal());
        atendimento.setExameFisico(form.getExameFisico());
        atendimento.setDiagnostico(form.getDiagnostico());
        atendimento.setPlanoTerapeutico(form.getPlanoTerapeutico());
        atendimento.setPrescricaoMedicamentos(form.getPrescricaoMedicamentos());
        atendimentos.save(atendimento);
        consulta.setStatusConsulta("ATENDIDA");
        consultas.save(consulta);
    }

    private void requireAccess(Consulta consulta, HospitalPrincipal user) {
        if (user == null) throw new AccessDeniedException("Acesso negado.");
        if ("ADMIN".equals(user.getRole())) return;
        if (!"MEDICO".equals(user.getRole()) || consulta.getMedico() == null
                || !Objects.equals(consulta.getMedico().getIdMedico(), user.getEntityId())) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }
}
