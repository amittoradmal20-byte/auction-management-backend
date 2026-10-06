CREATE TABLE auctions (
    id UUID PRIMARY KEY,

    tournament_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,

    description VARCHAR(500),

    status VARCHAR(30) NOT NULL,

    start_time TIMESTAMP,

    end_time TIMESTAMP,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    created_by UUID,

    updated_by UUID,

    CONSTRAINT fk_auctions_tournament
        FOREIGN KEY (tournament_id)
        REFERENCES tournaments(id)
);