SELECT P.storeno,
       P.contrno,
       P.instno,
       P.status                                    AS statusParcela,
       I.status                                    AS statusContrato,
       cast(P.duedate AS DATE)                     AS dtVencimento,
       cast(I.date AS DATE)                        AS dtEmissao,
       (P.instamt - P.paidamt) / 100               AS valorParcela,
       P.l4                                        AS nossoNumero,
        CASE
         WHEN duedate >= current_date*1
           THEN 0/100
         WHEN duedate < 20200322
           THEN TRUNCATE((instamt - paidamt) * ((DATEDIFF(20200322, duedate) + DATEDIFF(current_date*1, 20200802)) * (0.0026)), 0)/100
         WHEN duedate BETWEEN 20200322 AND 20200802
           THEN TRUNCATE((instamt - paidamt) * (DATEDIFF(current_date*1, 20200802) * (0.0026)), 0)/100
         WHEN duedate > 20200802
           THEN TRUNCATE((instamt - paidamt) * ((DATEDIFF(current_date*1, duedate)) * (0.0026)), 0)/100
       END                                         AS valorJuros,
       CT.no                                       AS codigo,
       CT.name                                     AS nome,
       CT.cpf_cgc                                  AS documento,
       CT.add1                                     AS endereco,
       CT.nei1                                     AS bairro,
       TRIM(MID(CT.zip, 1, 10))                    AS cep,
       CT.city1                                    AS cidade,
       CT.state1                                   AS uf,
       IFNULL(C3.auxString7, '')                   AS email,
       if(P.c1 = '', NULL, cast(P.c1 * 1 AS DATE)) AS dtProcessamento,
       cast(20201031 AS DATE)                     AS dtVencimentoBoleto,
       P.s4                                        AS numLote
FROM sqldados.itxa   AS P
  INNER JOIN inst    AS I
               USING (storeno, contrno)
  INNER JOIN custp   AS CT
               ON I.custno = CT.no
  LEFT JOIN  ctmore3 AS C3
               USING (custno)
WHERE P.l4 <> 0 /*Nosso número*/
  AND P.s4 = :lote