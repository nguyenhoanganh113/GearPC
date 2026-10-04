CREATE TABLE public.users (
    id uuid PRIMARY KEY,
    email varchar(255) NOT NULL,
    password_hash varchar(255) NOT NULL,
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    phone varchar(255),
    avatar_key varchar(255),
    user_status varchar(255) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    email_verified boolean NOT NULL DEFAULT false,
    created_at timestamp(6) with time zone NOT NULL,
    created_by varchar(255),
    last_modified_at timestamp(6) with time zone NOT NULL,
    last_modified_by varchar(255),
    deleted_at timestamp(6) with time zone,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_phone UNIQUE (phone),
    CONSTRAINT ck_users_status CHECK (
        user_status IN (
                        'PENDING_VERIFICATION',
                        'ACTIVE',
                        'INACTIVE',
                        'LOCKED'
            )
        )
);

CREATE TABLE public.roles (
    id uuid PRIMARY KEY,
    name varchar(255) NOT NULL,
    description varchar(255),
    created_at timestamp(6) with time zone NOT NULL,
    created_by varchar(255),
    last_modified_at timestamp(6) with time zone NOT NULL,
    last_modified_by varchar(255),
    deleted_at timestamp(6) with time zone,
    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE public.user_roles (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    role_id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    created_by varchar(255),
    last_modified_at timestamp(6) with time zone NOT NULL,
    last_modified_by varchar(255),
    deleted_at timestamp(6) with time zone,
    CONSTRAINT uq_user_roles_user_role UNIQUE (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES public.users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES public.roles (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_role_id
    ON public.user_roles (role_id);
