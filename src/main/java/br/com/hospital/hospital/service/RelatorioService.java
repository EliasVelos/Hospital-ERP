package br.com.hospital.hospital.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hospital.hospital.entity.Relatorio;
import br.com.hospital.hospital.repository.ConsultaRepository;
import br.com.hospital.hospital.repository.InternacaoRepository;
import br.com.hospital.hospital.repository.MovMedicamentoRepository;
import br.com.hospital.hospital.repository.RelatorioRepository;

@Service
public class RelatorioService {

    @Autowired private RelatorioRepository relatorioRepository;
    @Autowired private ConsultaRepository consultaRepository;
    @Autowired private InternacaoRepository internacaoRepository;
    @Autowired private MovMedicamentoRepository movMedicamentoRepository;
    @Autowired private LeitoService leitoService; 
    
    // --- CRUD BÁSICO ---

    public Relatorio save(Relatorio relatorio) { 
        return relatorioRepository.save(relatorio); 
    }

    public List<Relatorio> findAll() { 
        return relatorioRepository.findAll(); 
    }
    
    public Relatorio findById(Integer id) { 
        return relatorioRepository.findById(id).orElse(null); 
    }
    
    public void deleteById(Integer id) { 
        relatorioRepository.deleteById(id); 
    }
    
    // --------------------------------------------------------

    // ✅ MÉTODO ATUALIZADO: Recebe as datas de Início e Fim da Controller
    public Relatorio gerarNovoRelatorio(LocalDate dataInicioForm, LocalDate dataFimForm) {
        
        // Define a data de inauguração: 08 de Dezembro de 2003
        LocalDate dataInauguracao = LocalDate.of(2003, 12, 8); 
        LocalDate hoje = LocalDate.now();
        
        LocalDate dataInicio = dataInicioForm; 
        LocalDate dataFim = dataFimForm != null ? dataFimForm : hoje; // Usa a data do form ou a data atual
        
        // Lógica para CONTINUIDADE: Se já houver relatórios, começa no dia seguinte
        Optional<Relatorio> ultimoRelatorio = relatorioRepository.findTopByOrderByDataGeracaoDesc();
        
        if (ultimoRelatorio.isPresent()) {
            dataInicio = ultimoRelatorio.get().getDataFimPeriodo().plusDays(1);
        } else {
            // Se for o primeiro relatório, usa a data de inauguração (2003-12-08)
            dataInicio = dataInauguracao;
        }

        // Garante que o relatório não comece em uma data futura
        if (dataInicio.isAfter(hoje)) {
             dataInicio = hoje;
        }

        String conteudoDetalhado = montarConteudoRelatorio(dataInicio, dataFim);

        Relatorio novoRelatorio = new Relatorio();
        novoRelatorio.setDataGeracao(hoje);
        novoRelatorio.setDataInicioPeriodo(dataInicio);
        novoRelatorio.setDataFimPeriodo(dataFim);
        novoRelatorio.setTituloRelatorio("Relatório Administrativo de " + dataInicio.toString() + " a " + dataFim.toString());
        novoRelatorio.setConteudo(conteudoDetalhado);

        return relatorioRepository.save(novoRelatorio);
    }

    private String montarConteudoRelatorio(LocalDate dataInicio, LocalDate dataFim) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- RELATÓRIO ADMINISTRATIVO ---\n\n");
        sb.append("PERÍODO DE ANÁLISE: ").append(dataInicio).append(" até ").append(dataFim).append("\n");
        sb.append("DATA DE GERAÇÃO: ").append(LocalDate.now()).append("\n");
        sb.append("--------------------------------------------------------------------------------------------------\n\n");

        
        // --- CONVERSÃO PARA LocalDateTime ---
        // --- CONVERSÃO PARA LocalDateTime (substituir a chamada antiga) ---
LocalDateTime inicioPeriodo = dataInicio.atStartOfDay();
LocalDateTime fimPeriodo  = dataFim.atTime(23, 59, 59);

// 1. ESTATÍSTICAS DE CONSULTAS (mantém como já estava)
List<Object[]> consultasPorMedico = consultaRepository.countConsultasByMedicoAndPeriodo(inicioPeriodo, fimPeriodo);
sb.append("1. ESTATÍSTICAS DE CONSULTAS (Período: ").append(dataInicio).append(" - ").append(dataFim).append(")\n");
if (!consultasPorMedico.isEmpty()) {
    for (Object[] resultado : consultasPorMedico) {
        sb.append("   - Médico: ").append(resultado[0]).append(" | Total de Consultas: ").append(resultado[1]).append("\n");
    }
} else {
    sb.append("   - Nenhuma consulta encontrada no período.\n");
}
sb.append("\n");

// 2. TAXA DE OCUPAÇÃO DE LEITOS (sem alteração)
long totalLeitos = leitoService.countAllLeitos();
long leitosOcupados = leitoService.countLeitosOcupados();
double taxaOcupacao = leitoService.calcularTaxaOcupacaoAtual();

sb.append("2. TAXA DE OCUPAÇÃO DE LEITOS (Dados Atuais)\n");
sb.append("   - Total de Leitos: ").append(totalLeitos).append("\n");
sb.append("   - Leitos Ocupados: ").append(leitosOcupados).append("\n");
sb.append("   - Taxa de Ocupação: ").append(String.format("%.2f", taxaOcupacao)).append("%%\n");
sb.append("\n");

// 3. CONSUMO DE MEDICAMENTOS (usa inicioPeriodo / fimPeriodo)
List<Object[]> consumo = movMedicamentoRepository.sumSaidasByMedicamentoAndPeriodo(inicioPeriodo, fimPeriodo);
sb.append("3. CONSUMO DE MEDICAMENTOS (Período: ").append(dataInicio).append(" - ").append(dataFim).append(")\n");
if (!consumo.isEmpty()) {
    for (Object[] resultado : consumo) {
        sb.append("   - ").append(resultado[0]).append(" | Quantidade Consumida: ").append(resultado[1]).append("\n");
    }
} else {
    sb.append("   - Nenhum consumo de medicamento registrado no período.\n");
}
sb.append("\n");

// 4. ESTATÍSTICAS DE INTERNAÇÕES
// - novasInternacoes (usa dataEntrada: LocalDateTime)
long novasInternacoes = internacaoRepository.countByDataEntradaBetween(inicioPeriodo, fimPeriodo);

// - altasNoPeriodo: dataAlta é LocalDateTime → usar inicioPeriodo/fimPeriodo ou criar variáveis separadas
long altasNoPeriodo = internacaoRepository.countByDataAltaBetween(inicioPeriodo, fimPeriodo);

sb.append("4. ESTATÍSTICAS DE INTERNAÇÕES (Período: ").append(dataInicio).append(" - ").append(dataFim).append(")\n");
sb.append("   - Novas Internações: ").append(novasInternacoes).append("\n");
sb.append("   - Altas Registradas: ").append(altasNoPeriodo).append("\n");
sb.append("\n");



        sb.append("--------------------------------------------------------------------------------------------------");
        
        return sb.toString();
    }
}