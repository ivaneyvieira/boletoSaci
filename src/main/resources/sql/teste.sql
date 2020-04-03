DO @TAXA := (7.90 / 30) / 100;
DO @DATA_ATUAL := 20200321;
DO @HOJE := 20200403;


DROP TABLE IF EXISTS TPARCELAS;
CREATE TEMPORARY TABLE TPARCELAS (
  PRIMARY KEY (storeno, contrno, instno)
)
SELECT custno,
       itxa.storeno,
       itxa.contrno,
       itxa.instno,
       inst.date                     AS dataCompra,
       itxa.duedate                  AS dataVencimento,
       (itxa.instamt - itxa.paidamt) AS valorAberto,

       if(@DATA_ATUAL > duedate,
          TRUNCATE((instamt - paidamt) * (1 + DATEDIFF(@DATA_ATUAL, duedate) * @TAXA), 0),
          (instamt - paidamt))       AS valorAtualizado
FROM sqldados.inst
  INNER JOIN sqldados.itxa
               ON (inst.storeno = itxa.storeno AND inst.contrno = itxa.contrno)
WHERE itxa.status IN (0, 2, 3, 4)
  AND (inst.custno IN (659717, 123456))
  AND (inst.storeno IN (1, 3, 5, 6, 8, 9, 10, 11, 12));

DROP TABLE IF EXISTS TPARCELAS_VENCIDAS;
CREATE TEMPORARY TABLE TPARCELAS_VENCIDAS (
  PRIMARY KEY (storeno, contrno, instno)
)
SELECT custno,
       storeno,
       contrno,
       instno,
       dataVencimento
FROM TPARCELAS
WHERE dataVencimento <= @HOJE;

DROP TABLE IF EXISTS TPARCELAS_AVENCER;
CREATE TEMPORARY TABLE TPARCELAS_AVENCER (
  PRIMARY KEY (storeno, contrno, instno)
)
SELECT custno,
       storeno,
       contrno,
       instno,
       dataVencimento
FROM TPARCELAS
WHERE dataVencimento > @HOJE;
