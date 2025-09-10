SELECT conname 
FROM pg_constraint 
WHERE conrelid = 'proposta'::regclass 
AND contype = 'c';

ALTER TABLE proposta DROP CONSTRAINT proposta_status_proposta_check;

ALTER TABLE proposta 
ADD CONSTRAINT proposta_status_proposta_check 
CHECK (status_proposta IN ('PENDENTE', 'ACEITA', 'RECUSADA', 'CANCELADA', 'CONCLUIDA'));