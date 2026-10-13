-- Các bản ghi cũ chỉ có jti nên không thể bổ sung chính xác user_id và expires_at.
-- Chúng chỉ là trạng thái revoke tạm thời; xóa chúng để tạo schema mới.
DELETE FROM public.revoked_tokens;

ALTER TABLE public.revoked_tokens
    ADD COLUMN user_id varchar(36) NOT NULL,
    ADD COLUMN expires_at timestamp with time zone NOT NULL;
