CREATE TABLE public.stored_files (
    id uuid PRIMARY KEY,
    bucket varchar(63) NOT NULL,
    object_key varchar(1024) NOT NULL,
    temporary_object_key varchar(1024),
    file_name varchar(255) NOT NULL,
    content_type varchar(255) NOT NULL,
    size bigint,
    status varchar(32) NOT NULL DEFAULT 'PENDING',
    upload_expired_at timestamp(6) with time zone NOT NULL,
    ready_at timestamp(6) with time zone,
    created_at timestamp(6) with time zone NOT NULL,
    created_by varchar(255),
    last_modified_at timestamp(6) with time zone NOT NULL,
    last_modified_by varchar(255),
    deleted_at timestamp(6) with time zone,
    CONSTRAINT ck_stored_files_size CHECK (size >= 0),
    CONSTRAINT ck_stored_files_status CHECK (status IN ('PENDING', 'READY', 'EXPIRED', 'DELETED'))
);

CREATE INDEX idx_stored_files_status_upload_expired_at
    ON public.stored_files (status, upload_expired_at);
