$(document).ready(function() {
    var $input = $('#filtroInput');
    var $tabela = $('#tabelaUsuarios');
    var $linhas = $tabela.find('tbody tr');
    
    // 1. Função principal de filtro
    $input.on('keyup', function() {
        var termo = $(this).val().toLowerCase().trim();

        $linhas.each(function() {
            var $linha = $(this);
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
        $input.val('');
        $linhas.show();
    };
});