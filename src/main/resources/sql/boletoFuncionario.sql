SELECT documento,
       numLote,
       valor,
       nossoNumero,
       CT.no + 900000                         AS codigo,
       IFNULL(CT.name, B.nome)                AS nome,
       CT.cpf                                 AS documento,
       CT.addr                                AS endereco,
       CT.nei                                 AS bairro,
       cast(TRIM(MID(CT.zip, 1, 10)) AS CHAR) AS cep,
       CT.city                                AS cidade,
       CT.state                               AS uf
FROM bi.boletoFuncionario AS B
  LEFT JOIN sqldados.emp  AS CT
	      ON CT.cpf = B.documento
WHERE numLote = :numLote
GROUP BY nossoNumero
ORDER BY nome
