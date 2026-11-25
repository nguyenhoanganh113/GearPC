ALTER TABLE public.group_payment_method
    ADD COLUMN is_default boolean NOT NULL DEFAULT false;

CREATE UNIQUE INDEX uq_group_payment_method_default
    ON public.group_payment_method (group_id)
    WHERE is_default = true AND deleted_at IS NULL;
