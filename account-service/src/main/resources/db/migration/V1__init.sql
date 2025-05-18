CREATE TABLE account (
                         account_id UUID PRIMARY KEY,
                         iban VARCHAR(34) NOT NULL UNIQUE,
                         bic_swift VARCHAR(11) NOT NULL,
                         customer_id UUID NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
