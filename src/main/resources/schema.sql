-- Sensors that send readings (find-or-create on incoming data, never duplicated).
CREATE TABLE IF NOT EXISTS sensor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sensor_id VARCHAR(255) NOT NULL UNIQUE,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL
);

-- Earthquake alerts created once exactly 3 valid readings arrive in one request.
CREATE TABLE IF NOT EXISTS earthquake_alert (
    id INT AUTO_INCREMENT PRIMARY KEY,
    epicenter_latitude DOUBLE NOT NULL,
    epicenter_longitude DOUBLE NOT NULL,
    estimated_magnitude DOUBLE NOT NULL,
    alert_status VARCHAR(20) NOT NULL,
    area VARCHAR(255)
);

-- Raw sensor readings; alert_id is set only when the reading led to an alert.
CREATE TABLE IF NOT EXISTS sensor_reading (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reading_id VARCHAR(255) NOT NULL,
    estimated_distance_to_epicenter_km DOUBLE NOT NULL,
    estimated_magnitude DOUBLE NOT NULL,
    recorded_at DATETIME NOT NULL,
    sensor_id INT NOT NULL,
    alert_id INT,
    FOREIGN KEY (sensor_id) REFERENCES sensor(id),
    FOREIGN KEY (alert_id) REFERENCES earthquake_alert(id)
);

-- Citizen-submitted reports for an alert.
CREATE TABLE IF NOT EXISTS citizen_report (
    id INT AUTO_INCREMENT PRIMARY KEY,
    intensity INT NOT NULL,
    alert_id INT NOT NULL,
    FOREIGN KEY (alert_id) REFERENCES earthquake_alert(id)
);

-- App users for authentication (one USER and one ADMIN seeded at startup).
CREATE TABLE IF NOT EXISTS login (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);
