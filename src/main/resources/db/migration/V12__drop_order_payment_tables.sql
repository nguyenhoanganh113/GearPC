-- Order, address, and payment data are no longer owned by GearPC.
-- Drop dependent tables before their referenced tables.
DROP TABLE IF EXISTS public.payments;
DROP TABLE IF EXISTS public.order_details;
DROP TABLE IF EXISTS public.orders;
DROP TABLE IF EXISTS public.group_payment_method;
DROP TABLE IF EXISTS public.payment_method;
DROP TABLE IF EXISTS public.payment_group;
DROP TABLE IF EXISTS public.addresses;
