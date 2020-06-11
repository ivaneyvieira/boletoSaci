SELECT P.storeno                                                                                AS loja,
       P.contrno                                                                                AS contrato,
       P.instno                                                                                 AS numeroPrestacao,
       CASE P.status
         WHEN 0
           THEN IF(instamt - paidamt >= 0, 0, 1)
         WHEN 1
           THEN IF(instamt - paidamt >= 0, 0, 1)
         WHEN 2
           THEN IF(instamt - paidamt >= 0, 0, 1)
         WHEN 3
           THEN IF(instamt - paidamt >= 0, 0, 1)
         WHEN 4
           THEN 0
         WHEN 5
           THEN 5
       END                                                                                      AS statusParcela,
       cast(P.duedate AS DATE)                                                                  AS dtVencimento,
       (instamt - paidamt) / 100                                                                AS valorParcela,
       if(20200321 > duedate,
          TRUNCATE((instamt - paidamt) * (DATEDIFF(20200321, duedate) * (0.0026)), 0), 0) /
       100                                                                                      AS valorJuros,
       if(20200321 > duedate,
          TRUNCATE((instamt - paidamt) * (DATEDIFF(20200321, duedate) * (0.0026)), 0), 0) / 100 +
       (instamt - paidamt) / 100                                                                AS valorBoleto,
       P.l4                                                                                     AS nossoNumero,
       CT.no                                                                                    AS codigo,
       CT.name                                                                                  AS nome,
       CAST(CT.birthday AS DATE)                                                                AS dataNascimento,
       CT.cpf_cgc                                                                               AS documento,
       CT.add1                                                                                  AS endereco,
       CT.nei1                                                                                  AS bairro,
       TRIM(MID(CT.zip, 1, 10))                                                                 AS cep,
       CT.city1                                                                                 AS cidade,
       CT.state1                                                                                AS uf
FROM sqldados.itxa          AS P
  INNER JOIN sqldados.inst  AS I
               USING (storeno, contrno)
  INNER JOIN sqldados.custp AS CT
               ON I.custno = CT.no
WHERE P.duedate BETWEEN DATE_SUB(current_date, INTERVAL 150 DAY) * 1 AND DATE_ADD(current_date, INTERVAL 30 DAY) * 1
  AND P.status IN (0, 1, 2, 3, 5)
  AND P.l4 > 0
ORDER BY loja, contrato, numeroPrestacao
