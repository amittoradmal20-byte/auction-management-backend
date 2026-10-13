ALTER TABLE auctions
    ADD COLUMN opening_bid NUMERIC(12, 2) NOT NULL DEFAULT 1000.00,
    ADD COLUMN bid_increment NUMERIC(12, 2) NOT NULL DEFAULT 500.00;

ALTER TABLE auctions
    ADD CONSTRAINT chk_auctions_opening_bid_positive
        CHECK (opening_bid > 0),
    ADD CONSTRAINT chk_auctions_bid_increment_positive
        CHECK (bid_increment > 0);
