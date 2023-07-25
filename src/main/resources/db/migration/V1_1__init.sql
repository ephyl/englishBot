CREATE TABLE IF NOT EXISTS "public".glossary
(
    word char(200) NOT NULL,
    translate char(200) NOT NULL,
    level char(2) NOT NULL,
    CONSTRAINT pk_glossary_id PRIMARY KEY (word, translate)
);

CREATE TABLE IF NOT EXISTS "public".level_of_study
(
    user_name char(100) NOT NULL,
    level_of_study char(2) NOT NULL,
    study BOOLEAN DEFAULT false,
    chat_id char(200),
    poll_id char(300),
    start_poll_time bigint,
    time_class INT DEFAULT 1,
    CONSTRAINT pk_level_of_study_id PRIMARY KEY (user_name)
);