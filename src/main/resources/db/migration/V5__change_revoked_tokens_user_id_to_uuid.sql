ALTER TABLE public.revoked_tokens
ALTER COLUMN user_id TYPE uuid
    USING user_id::uuid;