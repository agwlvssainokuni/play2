## グループ
## メンバー

# --- !Ups

CREATE SEQUENCE groups_id_seq;
CREATE TABLE groups (
    id INTEGER NOT NULL DEFAULT nextval('groups_id_seq'),
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE SEQUENCE members_id_seq;
CREATE TABLE members (
    id INTEGER NOT NULL DEFAULT nextval('members_id_seq'),
    name VARCHAR(255) NOT NULL,
    birthday DATE,
    group_id INTEGER NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE members;
DROP SEQUENCE members_id_seq;

DROP TABLE groups;
DROP SEQUENCE groups_id_seq;
