DROP TABLE IF EXISTS T;
CREATE TEMPORARY TABLE T (
  PRIMARY KEY (nossoNumero)
)
SELECT R.nossoNumero,
       R.documento,
       R.chave,
       R.banco,
       R.agencia,
       R.carteira,
       R.ocorrencia,
       R.instCancelada,
       R.erros,
       cast(
           CONCAT('20', MID(dtOcorrencia, 5, 2), MID(dtOcorrencia, 3, 2), MID(dtOcorrencia, 1, 2)) *
           1 AS UNSIGNED) AS dtOcorrencia,
       cast(
           CONCAT('20', MID(R.vencimento, 5, 2), MID(R.vencimento, 3, 2), MID(R.vencimento, 1, 2)) *
           1 AS UNSIGNED) AS vencimento,
       R.valor * 1        AS valor,
       R.juros * 1        AS juros,
       count(*)           AS ct
FROM retornoBanco AS R
WHERE chave REGEXP '[0-9]{1,2}-[0-9]{9,10}-[0-9]{1,2}'
  AND ocorrencia = '06'
GROUP BY nossoNumero;

DROP TABLE IF EXISTS T2;
CREATE TEMPORARY TABLE T2
SELECT P.storeno,
       P.contrno,
       P.instno,
       P.status                  AS statusParcela,
       I.status                  AS statusContrato,
       P.duedate                 AS dtVencimento,
       I.date                    AS dtEmissao,
       P.instamt                 AS valorDevido,
       P.paidamt                 AS valorPago,

       P.l4                      AS nossoNumero,

       P.paiddate                AS dataPagamento,
       TRUNCATE(
         if(20200321 > duedate, P.instamt * (DATEDIFF(20200321, duedate) * ((7.90 / 30) / 100)),
            0.00), 0)            AS valorJuros,
       CT.no                     AS codigo,
       CT.name                   AS nome,
       CT.cpf_cgc                AS documento,
       CT.add1                   AS endereco,
       CT.nei1                   AS bairro,
       TRIM(MID(CT.zip, 1, 10))  AS cep,
       CT.city1                  AS cidade,
       CT.state1                 AS uf,
       IFNULL(C3.auxString7, '') AS email,
       P.c1 * 1                  AS dtProcessamento,
       if(adddate(P.c1 * 1, INTERVAL 7 DAY) * 1 > duedate, adddate(P.c1 * 1, INTERVAL 7 DAY) * 1,
          duedate)               AS dtVencimentoBoleto,
       P.s4                      AS numLote
FROM sqldados.itxa            AS P
  INNER JOIN sqldados.inst    AS I
               USING (storeno, contrno)
  INNER JOIN sqldados.custp   AS CT
               ON I.custno = CT.no
  LEFT JOIN  sqldados.ctmore3 AS C3
               USING (custno)
WHERE P.l4 <> 0;

DROP TABLE IF EXISTS T3;
CREATE TEMPORARY TABLE T3
SELECT S.nossoNumero,
       S.codigo,
       S.nome,
       S.documento                    AS cpf,
       R.dtOcorrencia                 AS dataOcorencia,
       R.valor                        AS valorBoleto,

       S.contrno                      AS contrato,
       S.instno                       AS parcela,
       S.dtVencimento,
       S.dataPagamento,
       S.valorDevido                  AS valorDevido,
       S.valorPago                    AS valorPago,
       S.valorJuros                   AS valorJuros,
       (S.valorJuros + S.valorDevido) AS valorParaBoleto,
       CASE
         WHEN valorPago = 0
           THEN 'ABERTO'
         WHEN valorPago < valorDevido
           THEN 'PAGAMENTO PARCIAL'
         WHEN valorPago >= valorDevido
           THEN 'LIQUIDADO'
       END                            AS SituacaoAtual
FROM T2        AS S
  INNER JOIN T AS R
               USING (nossoNumero)
ORDER BY R.dtOcorrencia, S.nossoNumero, S.contrno, S.instno;

DROP TABLE IF EXISTS T4;
CREATE TEMPORARY TABLE T4 (
  PRIMARY KEY (nossoNumero)
)
SELECT nossoNumero,
       SUM(SituacaoAtual = 'ABERTO')            AS ABERTO,
       SUM(SituacaoAtual = 'PAGAMENTO PARCIAL') AS PARCIAL,
       SUM(SituacaoAtual = 'LIQUIDADO')         AS LIQUIDADO
FROM T3
GROUP BY nossoNumero;

SELECT nossoNumero,
       codigo,
       nome,
       cpf,
       dataOcorencia,
       valorBoleto,
       contrato,
       parcela,
       dtVencimento,
       dataPagamento,
       valorDevido,
       valorPago,
       valorJuros,
       valorParaBoleto,
       SituacaoAtual,
       IF(ABERTO + PARCIAL = 0, 'S', 'N') AS BoletoLiquidado
FROM T3
  INNER JOIN T4
               USING (nossoNumero)
ORDER BY dataOcorencia, nossoNumero, contrato, parcela;