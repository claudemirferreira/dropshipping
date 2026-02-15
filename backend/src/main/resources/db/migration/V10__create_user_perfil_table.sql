CREATE TABLE user_perfil (
    user_id UUID NOT NULL,
    perfil_id UUID NOT NULL,
    PRIMARY KEY (user_id, perfil_id),
    CONSTRAINT fk_user_perfil_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_perfil_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (id) ON DELETE CASCADE
);

CREATE INDEX ix_user_perfil_perfil_id ON user_perfil (perfil_id);
