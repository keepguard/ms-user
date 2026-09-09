-- Pré-requisito de deploy do ms-user quando ddl-auto=validate (prod).
-- Rodar ANTES de subir a imagem com uniqueConstraints em user_person_profile / user_company_profile.
-- Docker local: psql no pg_core com schema ms_user.

BEGIN;

-- PersonProfile: manter linha com CPF; senão a mais recente
WITH ranked AS (
  SELECT id,
         ROW_NUMBER() OVER (
           PARTITION BY user_id
           ORDER BY (cpf IS NOT NULL AND btrim(cpf) <> '') DESC,
                    updated_at DESC NULLS LAST,
                    created_at DESC
         ) AS rn
  FROM ms_user.user_person_profile
)
DELETE FROM ms_user.user_person_profile p
USING ranked r
WHERE p.id = r.id AND r.rn > 1;

-- CompanyProfile: manter a mais recente
WITH ranked AS (
  SELECT id,
         ROW_NUMBER() OVER (
           PARTITION BY user_id
           ORDER BY updated_at DESC NULLS LAST,
                    created_at DESC
         ) AS rn
  FROM ms_user.user_company_profile
)
DELETE FROM ms_user.user_company_profile p
USING ranked r
WHERE p.id = r.id AND r.rn > 1;

COMMIT;

-- Índices unique (fora de CONCURRENTLY para caber em script transacional simples;
-- em janela de manutenção prod pode trocar por CREATE UNIQUE INDEX CONCURRENTLY).
CREATE UNIQUE INDEX IF NOT EXISTS uk_user_person_profile_user_id
  ON ms_user.user_person_profile (user_id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_company_profile_user_id
  ON ms_user.user_company_profile (user_id);
