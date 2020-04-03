SELECT P.storeno,
       P.contrno,
       P.instno,
       P.status                               AS statusParcela,
       I.status                               AS statusContrato,
       cast(P.duedate AS DATE)                AS dtVencimento,
       (P.instamt - P.paidamt) / 100          AS valorParcela,
       P.l4                                   AS nossoNumero,
       TRUNCATE(if(20200321 > duedate, ((P.instamt - P.paidamt) / 100) *
                                       (DATEDIFF(20200321, duedate) * ((7.90 / 30) / 100)), 0.00),
                2)                            AS valorJuros,
       CT.no                                  AS codigo,
       CT.name                                AS nome,
       CT.cpf_cgc                             AS documento,
       CT.add1                                AS endereco,
       CT.nei1                                AS bairro,
       TRIM(MID(CT.zip, 1, 10))               AS cep,
       CT.city1                               AS cidade,
       CT.state1                              AS uf,
       if(P.l3 = 0, NULL, cast(P.l3 AS DATE)) AS dtProcessamento
FROM sqldados.itxa AS P
  INNER JOIN inst  AS I
               USING (storeno, contrno)
  INNER JOIN custp AS CT
               ON I.custno = CT.no
WHERE P.l4 <> 0 /*Nosso número*/
  AND P.s4 = 0