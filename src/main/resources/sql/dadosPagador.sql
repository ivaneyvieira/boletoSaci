SELECT P.storeno,
       P.contrno,
       P.instno,
       P.status                 AS statusParcela,
       I.status                 AS statusContrato,
       cast(P.duedate AS DATE)  AS dtVencimento,
       P.instamt / 100          AS valorParcela,
       P.l4                     AS nossoNumero,
       0.00                     AS valorJuros,
       CT.name                  AS nome,
       CT.cpf_cgc               AS documento,
       CT.add1                  AS endereco,
       CT.nei1                  AS bairro,
       TRIM(MID(CT.zip, 1, 10)) AS cep,
       CT.city1                 AS cidade,
       CT.state1                AS uf
FROM sqldados.itxa AS P
  INNER JOIN inst  AS I
               USING (storeno, contrno)
  INNER JOIN custp AS CT
               ON I.custno = CT.no
WHERE P.storeno = :loja
  AND P.contrno = :contrato