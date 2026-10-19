ALTER TABLE public.users
    ADD COLUMN keycloak_id uuid;

ALTER TABLE public.users
    ADD CONSTRAINT uq_users_keycloak_id
        UNIQUE (keycloak_id);

ALTER TABLE public.users
DROP COLUMN password_hash;