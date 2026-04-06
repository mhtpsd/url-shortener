-- Flyway Migration V1: Create initial tables

CREATE TABLE shortened_urls (
    id              BIGSERIAL PRIMARY KEY,
    short_code      VARCHAR(10) UNIQUE NOT NULL,
    original_url    TEXT NOT NULL,
    custom_alias    BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    expires_at      TIMESTAMP WITH TIME ZONE,
    click_count     BIGINT DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_by_ip   VARCHAR(45)
);

CREATE INDEX idx_shortened_urls_short_code ON shortened_urls (short_code);
CREATE INDEX idx_shortened_urls_status ON shortened_urls (status);
CREATE INDEX idx_shortened_urls_expires_at ON shortened_urls (expires_at) WHERE expires_at IS NOT NULL;

CREATE TABLE click_events (
    id              BIGSERIAL PRIMARY KEY,
    short_code      VARCHAR(10) NOT NULL,
    clicked_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    referer         TEXT,
    country         VARCHAR(100),
    device_type     VARCHAR(50),
    browser         VARCHAR(100),
    os              VARCHAR(100)
);

CREATE INDEX idx_click_events_short_code ON click_events (short_code);
CREATE INDEX idx_click_events_clicked_at ON click_events (clicked_at);
CREATE INDEX idx_click_events_short_code_clicked_at ON click_events (short_code, clicked_at);
