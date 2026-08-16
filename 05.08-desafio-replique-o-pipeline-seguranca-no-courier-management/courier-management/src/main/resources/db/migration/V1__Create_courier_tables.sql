-- Create courier table
CREATE TABLE courier (
    id UUID PRIMARY KEY,

    -- Courier data
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,

    -- Delivery counters
    fulfilled_deliveries_quantity INTEGER NOT NULL DEFAULT 0,
    pending_deliveries_quantity INTEGER NOT NULL DEFAULT 0,

    -- Timestamps
    last_fulfilled_delivery_at TIMESTAMP WITH TIME ZONE,

    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create assigned_delivery table
CREATE TABLE assigned_delivery (
    id UUID PRIMARY KEY,
    courier_id UUID NOT NULL,
    assigned_at TIMESTAMP WITH TIME ZONE,

    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraint
    CONSTRAINT fk_assigned_delivery_courier FOREIGN KEY (courier_id) REFERENCES courier(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_courier_last_fulfilled_delivery_at ON courier(last_fulfilled_delivery_at);
CREATE INDEX idx_assigned_delivery_courier_id ON assigned_delivery(courier_id);

-- Add constraints
ALTER TABLE courier ADD CONSTRAINT chk_courier_fulfilled_deliveries_quantity
    CHECK (fulfilled_deliveries_quantity >= 0);

ALTER TABLE courier ADD CONSTRAINT chk_courier_pending_deliveries_quantity
    CHECK (pending_deliveries_quantity >= 0);
