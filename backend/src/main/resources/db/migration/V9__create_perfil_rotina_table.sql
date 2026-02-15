CREATE TABLE perfil_rotina (
    perfil_id UUID NOT NULL,
    rotina_id UUID NOT NULL,
    PRIMARY KEY (perfil_id, rotina_id),
    CONSTRAINT fk_perfil_rotina_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (id) ON DELETE CASCADE,
    CONSTRAINT fk_perfil_rotina_rotina FOREIGN KEY (rotina_id) REFERENCES rotina (id) ON DELETE CASCADE
);

CREATE INDEX ix_perfil_rotina_rotina_id ON perfil_rotina (rotina_id);
