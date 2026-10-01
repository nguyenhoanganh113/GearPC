CREATE TABLE public.categories (
    id uuid PRIMARY KEY,
    name varchar(255) NOT NULL,
    slug varchar(255) NOT NULL,
    description text,
    image_url varchar(500),
    active boolean NOT NULL DEFAULT false,
    created_at timestamp(6) with time zone NOT NULL,
    created_by varchar(255),
    last_modified_at timestamp(6) with time zone NOT NULL,
    last_modified_by varchar(255),
    deleted_at timestamp(6) with time zone
);

CREATE TABLE public.brands (
    id uuid PRIMARY KEY,
    name varchar(255) NOT NULL,
    slug varchar(255) NOT NULL,
    logo_url varchar(500),
    active boolean NOT NULL DEFAULT false,
    created_at timestamp(6) with time zone NOT NULL,
    created_by varchar(255),
    last_modified_at timestamp(6) with time zone NOT NULL,
    last_modified_by varchar(255),
    deleted_at timestamp(6) with time zone,
    CONSTRAINT uq_brands_name UNIQUE (name),
    CONSTRAINT uq_brands_slug UNIQUE (slug)
);

CREATE TABLE public.attribute_definitions (
    id uuid PRIMARY KEY,
    name varchar(255) NOT NULL,
    code varchar(255) NOT NULL,
    unit varchar(255),
    data_type varchar(50) NOT NULL,
    active boolean NOT NULL DEFAULT false,
    created_at timestamp(6) with time zone NOT NULL,
    created_by varchar(255),
    last_modified_at timestamp(6) with time zone NOT NULL,
    last_modified_by varchar(255),
    deleted_at timestamp(6) with time zone,
    CONSTRAINT uq_attribute_definitions_code UNIQUE (code),
    CONSTRAINT ck_attribute_definitions_data_type
        CHECK (data_type IN ('TEXT', 'NUMBER', 'BOOLEAN', 'DATE', 'SELECT'))
);

CREATE TABLE public.products (
    id uuid PRIMARY KEY,
    name varchar(255) NOT NULL,
    slug varchar(255) NOT NULL,
    sku varchar(255) NOT NULL,
    description text,
    images varchar(255),
    price numeric(15, 2) NOT NULL,
    product_status varchar(255) NOT NULL,
    stock_quantity integer NOT NULL,
    category_id uuid NOT NULL,
    brand_id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    created_by varchar(255),
    last_modified_at timestamp(6) with time zone NOT NULL,
    last_modified_by varchar(255),
    deleted_at timestamp(6) with time zone,
    CONSTRAINT uq_products_sku UNIQUE (sku),
    CONSTRAINT ck_products_price CHECK (price > 0),
    CONSTRAINT ck_products_stock_quantity CHECK (stock_quantity >= 0),
    CONSTRAINT ck_products_status CHECK (product_status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES public.categories (id),
    CONSTRAINT fk_products_brand
        FOREIGN KEY (brand_id) REFERENCES public.brands (id)
);

CREATE TABLE public.category_attributes (
    category_id uuid NOT NULL,
    attribute_definition_id uuid NOT NULL,
    is_required boolean NOT NULL DEFAULT false,
    CONSTRAINT pk_category_attributes
        PRIMARY KEY (category_id, attribute_definition_id),
    CONSTRAINT fk_category_attributes_category
        FOREIGN KEY (category_id) REFERENCES public.categories (id),
    CONSTRAINT fk_category_attributes_definition
        FOREIGN KEY (attribute_definition_id)
        REFERENCES public.attribute_definitions (id)
);

CREATE TABLE public.product_attribute_values (
    product_id uuid NOT NULL,
    attribute_definition_id uuid NOT NULL,
    value text NOT NULL,
    CONSTRAINT pk_product_attribute_values
        PRIMARY KEY (product_id, attribute_definition_id),
    CONSTRAINT fk_product_attribute_values_product
        FOREIGN KEY (product_id) REFERENCES public.products (id),
    CONSTRAINT fk_product_attribute_values_definition
        FOREIGN KEY (attribute_definition_id)
        REFERENCES public.attribute_definitions (id)
);

CREATE INDEX idx_products_category_id
    ON public.products (category_id);

CREATE INDEX idx_products_brand_id
    ON public.products (brand_id);

CREATE INDEX idx_products_created_at_id
    ON public.products (created_at DESC, id DESC);

CREATE INDEX idx_products_price_id
    ON public.products (price DESC, id DESC);

CREATE INDEX idx_products_status_created_at_id
    ON public.products (product_status, created_at DESC, id DESC);

CREATE INDEX idx_products_status_price_id
    ON public.products (product_status, price DESC, id DESC);

CREATE INDEX idx_category_attributes_definition_id
    ON public.category_attributes (attribute_definition_id);

CREATE INDEX idx_product_attribute_values_definition_id
    ON public.product_attribute_values (attribute_definition_id);
