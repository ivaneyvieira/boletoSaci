SELECT cast(nossoNumero * 1 AS UNSIGNED) AS nossoNumero,
       X.storeno                         AS loja,
       X.contrno                         AS contrato,
       X.instno                          AS parcela,
       nome                              AS nome,
       chave                             AS chave,
       documento                         AS documento,
       cast(
	   CONCAT('20', MID(dtOcorrencia, 5, 2), MID(dtOcorrencia, 3, 2), MID(dtOcorrencia, 1, 2)) *
	   1 AS DATE)                    AS dtOcorrencia,
       cast(
	   CONCAT('20', MID(R.vencimento, 5, 2), MID(R.vencimento, 3, 2), MID(R.vencimento, 1, 2)) *
	   1 AS DATE)                    AS vencimento,
       (taxa + principal) * 1 / 100      AS valor,
       R.taxa * 1 / 100                  AS taxa,
       R.juros * 1 / 100                 AS juros,
       R.desconto * 1 / 100              AS desconto
FROM bi.retornoBanco       AS R
  INNER JOIN sqldados.itxa AS X
	       ON X.l4 = R.nossoNumero
WHERE ocorrencia = '06'
HAVING dtOcorrencia * 1 >= 20200427
ORDER BY dtOcorrencia;