CREATE TABLE card (
                      card_id UUID PRIMARY KEY,
                      card_alias VARCHAR(50) NOT NULL,
                      account_id UUID NOT NULL,
                      card_type VARCHAR(10) NOT NULL CHECK (card_type IN ('virtual', 'physical')),
                      pan VARCHAR(19) NOT NULL,
                      cvv VARCHAR(3) NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
