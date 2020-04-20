SELECT DISTINCT s4 AS numLote, if(c1 = '', NULL, cast(c1 * 1 AS DATE)) AS dtProcessamento
FROM sqldados.itxa
WHERE l4 > 0
  AND s4 > 0