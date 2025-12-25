$(document).ready(function() {
    var $input = $('#filtroInput');
    var $tabela = $('#tabelaRelatorios');
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