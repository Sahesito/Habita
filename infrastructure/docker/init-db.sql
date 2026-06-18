CREATE DATABASE habita_keycloak;
CREATE DATABASE habita_auth;
CREATE DATABASE habita_users;
CREATE DATABASE habita_reservations;
CREATE DATABASE habita_visitors;
CREATE DATABASE habita_notifications;

GRANT ALL PRIVILEGES ON DATABASE habita_keycloak      TO habita;
GRANT ALL PRIVILEGES ON DATABASE habita_auth          TO habita;
GRANT ALL PRIVILEGES ON DATABASE habita_users         TO habita;
GRANT ALL PRIVILEGES ON DATABASE habita_reservations  TO habita;
GRANT ALL PRIVILEGES ON DATABASE habita_visitors      TO habita;
GRANT ALL PRIVILEGES ON DATABASE habita_notifications TO habita;