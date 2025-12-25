$(document).ready(function() {
    var $input = $('#filtroInput');
    var $linhas = $('#leitosTableBody tr');
    var $rodapeFiltro = $('#filtroVazio');
    var $rodapeOriginalVazio = $('#leitosTableFooter tr:not(#filtroVazio)');

    $input.on('keyup', function() {
        var termo = $(this).val().toLowerCase().trim();
        let resultsFound = false;

        $linhas.each(function() {
            var $linha = $(this);
            var textoLinha = $linha.text().toLowerCase();

            if (textoLinha.indexOf(termo) > -1) {
                $linha.show();
                resultsFound = true;
            } else {
                $linha.hide();
            }
        });

        // Controla a exibição das mensagens de rodapé
        if (resultsFound) {
            $rodapeFiltro.hide(); // Esconde "filtro vazio"
            $rodapeOriginalVazio.hide(); // Esconde "lista vazia"
        } else if (termo !== '') {
            // Se não achou nada E está filtrando
            $rodapeFiltro.show();
            $rodapeOriginalVazio.hide();
        } else {
            // Se não achou nada E o filtro está limpo (significa que a lista original estava vazia)
            $rodapeFiltro.hide();
            $rodapeOriginalVazio.show();
        }
    });

    // 2. Função para limpar o filtro
    window.limparFiltro = function() {
        $input.val(''); // Limpa o campo
        $linhas.show(); // Exibe todas as linhas
        $rodapeFiltro.hide(); // Esconde a mensagem de filtro vazio
        
        // Mostra a mensagem de "lista vazia" original, se aplicável
        if ($linhas.length === 0) {
             $rodapeOriginalVazio.show();
        }
        
        $input.focus(); // Foca no campo após limpar
    };
});