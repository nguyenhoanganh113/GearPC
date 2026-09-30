--
-- PostgreSQL database dump
--
-- Dumped from database version 16.15
-- Dumped by pg_dump version 16.15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: attribute_definitions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.attribute_definitions (
    active boolean NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    last_modified_at timestamp(6) with time zone NOT NULL,
    id uuid NOT NULL,
    data_type character varying(50) NOT NULL,
    code character varying(255) NOT NULL,
    created_by character varying(255),
    last_modified_by character varying(255),
    name character varying(255) NOT NULL,
    unit character varying(255),
    deleted_at timestamp(6) with time zone,
    CONSTRAINT attribute_definitions_data_type_check CHECK (((data_type)::text = ANY ((ARRAY['TEXT'::character varying, 'NUMBER'::character varying, 'BOOLEAN'::character varying, 'DATE'::character varying, 'SELECT'::character varying])::text[])))
);


--
-- Name: brands; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.brands (
    active boolean NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    last_modified_at timestamp(6) with time zone NOT NULL,
    id uuid NOT NULL,
    logo_url character varying(500),
    created_by character varying(255),
    last_modified_by character varying(255),
    name character varying(255) NOT NULL,
    slug character varying(255) NOT NULL,
    deleted_at timestamp(6) with time zone
);


--
-- Name: categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.categories (
    active boolean NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    last_modified_at timestamp(6) with time zone NOT NULL,
    id uuid NOT NULL,
    image_url character varying(500),
    created_by character varying(255),
    description text,
    last_modified_by character varying(255),
    name character varying(255) NOT NULL,
    slug character varying(255) NOT NULL,
    deleted_at timestamp(6) with time zone
);


--
-- Name: category_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.category_attributes (
    is_required boolean NOT NULL,
    attribute_definition_id uuid NOT NULL,
    category_id uuid NOT NULL
);


--
-- Name: product_attribute_values; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.product_attribute_values (
    attribute_definition_id uuid NOT NULL,
    product_id uuid NOT NULL,
    value character varying(36) NOT NULL
);


--
-- Name: products; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.products (
    price numeric(15,2),
    stock_quantity integer NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    last_modified_at timestamp(6) with time zone NOT NULL,
    brand_id uuid NOT NULL,
    category_id uuid NOT NULL,
    id uuid NOT NULL,
    created_by character varying(255),
    description text,
    images character varying(255),
    last_modified_by character varying(255),
    name character varying(255) NOT NULL,
    product_status character varying(255) NOT NULL,
    sku character varying(255) NOT NULL,
    slug character varying(255) NOT NULL,
    deleted_at timestamp(6) with time zone,
    CONSTRAINT products_product_status_check CHECK (((product_status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying])::text[])))
);


--
-- Name: attribute_definitions attribute_definitions_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.attribute_definitions
    ADD CONSTRAINT attribute_definitions_code_key UNIQUE (code);


--
-- Name: attribute_definitions attribute_definitions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.attribute_definitions
    ADD CONSTRAINT attribute_definitions_pkey PRIMARY KEY (id);


--
-- Name: brands brands_logo_url_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.brands
    ADD CONSTRAINT brands_logo_url_key UNIQUE (logo_url);


--
-- Name: brands brands_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.brands
    ADD CONSTRAINT brands_name_key UNIQUE (name);


--
-- Name: brands brands_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.brands
    ADD CONSTRAINT brands_pkey PRIMARY KEY (id);


--
-- Name: brands brands_slug_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.brands
    ADD CONSTRAINT brands_slug_key UNIQUE (slug);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: category_attributes category_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.category_attributes
    ADD CONSTRAINT category_attributes_pkey PRIMARY KEY (attribute_definition_id, category_id);


--
-- Name: product_attribute_values product_attribute_values_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_attribute_values
    ADD CONSTRAINT product_attribute_values_pkey PRIMARY KEY (attribute_definition_id, product_id);


--
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- Name: idx_products_brand_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_products_brand_id ON public.products USING btree (brand_id);


--
-- Name: idx_products_category_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_products_category_id ON public.products USING btree (category_id);


--
-- Name: idx_products_created_at_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_products_created_at_id ON public.products USING btree (created_at DESC, id DESC);


--
-- Name: idx_products_price; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_products_price ON public.products USING btree (price);


--
-- Name: idx_products_price_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_products_price_id ON public.products USING btree (price DESC, id DESC);


--
-- Name: idx_products_product_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_products_product_status ON public.products USING btree (product_status);


--
-- Name: idx_products_status_created_at_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_products_status_created_at_id ON public.products USING btree (product_status, created_at DESC, id DESC);


--
-- Name: idx_products_status_price_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_products_status_price_id ON public.products USING btree (product_status, price DESC, id DESC);


--
-- Name: product_attribute_values fk9cv255c78bptiixa9axev9act; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_attribute_values
    ADD CONSTRAINT fk9cv255c78bptiixa9axev9act FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- Name: products fka3a4mpsfdf4d2y6r8ra3sc8mv; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fka3a4mpsfdf4d2y6r8ra3sc8mv FOREIGN KEY (brand_id) REFERENCES public.brands(id);


--
-- Name: product_attribute_values fkavkptj46cpb5wb17gik0e8pe0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_attribute_values
    ADD CONSTRAINT fkavkptj46cpb5wb17gik0e8pe0 FOREIGN KEY (attribute_definition_id) REFERENCES public.attribute_definitions(id);


--
-- Name: category_attributes fkkvxw6909rdbxpd6y37yb18d03; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.category_attributes
    ADD CONSTRAINT fkkvxw6909rdbxpd6y37yb18d03 FOREIGN KEY (attribute_definition_id) REFERENCES public.attribute_definitions(id);


--
-- Name: products fkog2rp4qthbtt2lfyhfo32lsw9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fkog2rp4qthbtt2lfyhfo32lsw9 FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: category_attributes fks8x8saggy3b3wx581pn813por; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.category_attributes
    ADD CONSTRAINT fks8x8saggy3b3wx581pn813por FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- PostgreSQL database dump complete
--
