create table if not exists sample(
    id serial PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    data text,
    value int default 0
);
-- |||||||||||||||||||||||||||| above was a sample ||||||||||||||||||||||||||||||||||||--
create table if not exists company(
  id serial PRIMARY KEY,
  name VARCHAR(128) UNIQUE NOT NULL,
  country VARCHAR(128),
  city VARCHAR(128),
  zip_number INTEGER,
  street_info TEXT,
  phone_number VARCHAR(20) UNIQUE NOT NULL,
  e_mails TEXT[]
);


create table if not exists product(
    id serial PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    description text,
    brand_name VARCHAR(128) NOT NULL
    );

CREATE TABLE IF NOT EXISTS produce(
  id serial PRIMARY KEY,
  company_name VARCHAR(128) NOT NULL REFERENCES company(name),
  product_id int NOT NULL REFERENCES product(id),
  capacity int NOT NULL
);

create table if not exists transaction(
    id serial PRIMARY KEY,
    company_name VARCHAR(128) NOT NULL REFERENCES company(name),
    product_id int NOT NULL REFERENCES product(id),
    amount int NOT NULL,
    created_date timestamp without time zone NOT NULL
    );

-- |||||||||||||||||||||||||||| this was a sample start ||||||||||||||||||||||||||||||||||||--
create FUNCTION sample_trigger() RETURNS TRIGGER AS
'
    BEGIN
        IF (SELECT value FROM sample where id = NEW.id ) > 1000
           THEN
           RAISE SQLSTATE ''23503'';
           END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;
-- |||||||||||||||||||||||||||| this was a sample end ||||||||||||||||||||||||||||||||||||--

-- |||||||||||||||||||||||||||| this was a sample start ||||||||||||||||||||||||||||||||||||--
create TRIGGER sample_value AFTER insert ON sample
    FOR EACH ROW EXECUTE PROCEDURE sample_trigger();
-- |||||||||||||||||||||||||||| this was a sample end ||||||||||||||||||||||||||||||||||||--

-- what was asked in requirements?
-- no same name company, (already done above as primary key)
-- IF ZIP1 == ZIP2 THEN CITY1=CITY2
-- every product has a name, (already done above as not null)
-- transaction amount < production capacity (can do as a trigger)
-- production capacity of a company >= total active orders (can do as a trigger)

create FUNCTION check_capacity() RETURNS TRIGGER AS
    '
    BEGIN
        IF (SELECT SUM(amount) FROM transaction WHERE company_name = NEW.company_name AND product_id = NEW.product_id) > (SELECT capacity FROM produce WHERE company_name = NEW.company_name AND product_id = NEW.product_id)
           THEN
           RAISE SQLSTATE ''23503'' USING MESSAGE = ''Order exceeds the production capacity'';
           END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

create TRIGGER enforce_capacity AFTER insert ON transaction
    FOR EACH ROW EXECUTE PROCEDURE check_capacity();

create FUNCTION product_has_name() RETURNS TRIGGER AS
    '
    BEGIN
        IF NEW.name IS NULL OR NEW.name = ''''
           THEN
           RAISE SQLSTATE ''23503'' USING MESSAGE = ''Product must have a name'';
           END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

create TRIGGER enforce_product_name BEFORE insert ON product
    FOR EACH ROW EXECUTE PROCEDURE product_has_name();

CREATE FUNCTION enforce_zip_city() RETURNS TRIGGER AS '
BEGIN
    IF EXISTS (SELECT 1 FROM company WHERE zip_number = NEW.zip_number AND city <> NEW.city) THEN
        RAISE SQLSTATE ''23514'' USING MESSAGE = ''If zip numbers are the same, city names should also be the same.'';
    END IF;
    RETURN NEW;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER enforce_zip_city_condition
    BEFORE INSERT OR UPDATE ON company
    FOR EACH ROW EXECUTE PROCEDURE enforce_zip_city();


