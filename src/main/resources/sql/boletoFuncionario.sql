SELECT documento,
       numLote,
       valor,
       nossoNumero,
       CT.no                    AS codigo,
       CT.name                  AS nome,
       CT.add1                  AS endereco,
       CT.nei1                  AS bairro,
       TRIM(MID(CT.zip, 1, 10)) AS cep,
       CT.city1                 AS cidade,
       CT.state1                AS uf
FROM bi.boletoFuncionario  AS B
  LEFT JOIN sqldados.custp AS CT
	      ON CT.cpf_cgc = B.documento
where numLote = :numLote
ORDER BY CT.name