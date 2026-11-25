ALTER TABLE public.stored_files
    DROP CONSTRAINT ck_stored_files_status;

ALTER TABLE public.stored_files
    ADD CONSTRAINT ck_stored_files_status
        CHECK (status IN ('PENDING', 'READY', 'FAILED', 'EXPIRED', 'DELETED'));
