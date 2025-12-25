// Script de filtro mantido
$(document).ready(function() {
    // Alerta: substituição do confirm() nativo por um console.warn (pois alerts não funcionam em iframes)
    $('a.btn-danger').on('click', function(e) {
        // Se o ambiente não for o Canvas (o que é o caso para um arquivo HTML simples), usamos o confirm() nativo.
        // Para produção com Thymeleaf, seu backend garantirá o funcionamento do confirm.
        if (typeof __app_id === 'undefined' && !confirm('Deseja realmente excluir esta consulta?')) {
            e.preventDefault();
        }
    });
    
    var $input = $('#filtroInput');
    var $tabela = $('#tabelaConsultas');
    var $linhas = $tabela.find('tbody tr');
    
    $input.on('keyup', function() {
        var termo = $(this).val().toLowerCase().trim();

        $linhas.each(function() {
            var $linha = $(this);
            var textoLinha = $linha.text().toLowerCase();

            if (textoLinha.indexOf(termo) > -1) {
                $linha.show();
            } else {
                $linha.hide();
            }
        });
    });

    window.limparFiltro = function() {
        $input.val('');
        $linhas.show();
        $input.focus();
    };
});