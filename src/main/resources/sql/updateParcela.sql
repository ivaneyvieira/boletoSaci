UPDATE sqldados.itxa
SET l4 = :nossoNumero,
    s4 = IF(:processado, s4 | pow(2, 0), s4 & ~ pow(2, 0))
WHERE storeno = :loja
  AND contrno = :contrato
  AND instno = :parcela