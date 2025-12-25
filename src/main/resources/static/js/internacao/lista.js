$(document).ready(function() {
    // Alerta: substituição do confirm() nativo por um console.warn (pois alerts não funcionam em iframes)
    $('a.btn-outline-danger').on('click', function(e) {
        // Se o ambiente não for o Canvas (o que é o caso para um arquivo HTML simples), usamos o confirm() nativo.
        if (typeof __app_id === 'undefined' && !confirm('ATENÇÃO: A exclusão deve ser usada apenas em caso de erro no registro. Deseja realmente excluir?')) {
            e.preventDefault();
        }
    });
    $('a.btn-info').on('click', function(e) {
        // Se o ambiente não for o Canvas (o que é o caso para um arquivo HTML simples), usamos o confirm() nativo.
        if (typeof __app_id === 'undefined' && !confirm(e.currentTarget.getAttribute('onclick').match(/'([^']*)'/)[1])) {
             e.preventDefault();
        }
    });
    
    var $input = $('#filtroInput');
    var $tabela = $('#tabelaInternacoes');
    var $linhas = $tabela.find('tbody tr');
    
    // 1. Função principal de filtro
    $input.on('keyup', function() {
        var termo = $(this).val().toLowerCase().trim();

        $linhas.each(function() {
            var $linha = $(this);
            // Captura o texto de todas as células da linha para a pesquisa
            var textoLinha = $linha.text().toLowerCase();

            // Verifica se o termo de pesquisa está em qualquer parte do texto da linha
            if (textoLinha.indexOf(termo) > -1) {
                $linha.show();
            } else {
                $linha.hide();
            }
        });
    });

    // 2. Função para limpar o filtro
    window.limparFiltro = function() {
        $input.val(''); // Limpa o campo
        $linhas.show();  // Exibe todas as linhas
        $input.focus();  // Foca no campo após limpar
    };
});