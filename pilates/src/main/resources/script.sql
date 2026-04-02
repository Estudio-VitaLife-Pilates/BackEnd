CREATE DATABASE IF NOT EXISTS pilates;
USE pilates;
CREATE TABLE professor (
                           id_professor INT AUTO_INCREMENT PRIMARY KEY,
                           nome VARCHAR(100) NOT NULL,
                           telefone VARCHAR(20),
                           ativo BOOLEAN,
                           email VARCHAR(100)
);

CREATE TABLE aluno (
                       id_aluno INT AUTO_INCREMENT PRIMARY KEY,
                       nome VARCHAR(100) NOT NULL,
                       telefone VARCHAR(20),
                       cpf VARCHAR(11) UNIQUE,
                       email VARCHAR(100),
                       ativo BOOLEAN DEFAULT TRUE,
                       data_nascimento DATE,
                       data_cadastro DATE
);

CREATE TABLE plano (
                       id_plano INT AUTO_INCREMENT PRIMARY KEY,
                       nome VARCHAR(50),
                       frequencia_semanal INT,
                       validade_dias INT
);

CREATE TABLE aluno_plano (
                             id_aluno_plano INT AUTO_INCREMENT PRIMARY KEY,
                             fk_aluno INT,
                             fk_plano INT,
                             data_inicio DATE,
                             data_fim DATE,
                             FOREIGN KEY (fk_aluno) REFERENCES aluno(id_aluno),
                             FOREIGN KEY (fk_plano) REFERENCES plano(id_plano)
);

CREATE TABLE turma (
                       id_turma INT AUTO_INCREMENT PRIMARY KEY,
                       dia_semana ENUM('SEGUNDA','TERCA','QUARTA','QUINTA','SEXTA','SABADO','DOMINGO') NOT NULL,
                       hora_inicio TIME NOT NULL,
                       duracao_minutos INT DEFAULT 60,
                       capacidade_max INT,
                       ativa BOOLEAN DEFAULT TRUE,
                       fk_professor INT,
                       FOREIGN KEY (fk_professor) REFERENCES professor(id_professor)
);

CREATE TABLE aluno_turma (
                             id_aluno_turma INT AUTO_INCREMENT PRIMARY KEY,
                             fk_aluno INT,
                             fk_turma INT,
                             data_inicio DATE,
                             ativo BOOLEAN DEFAULT TRUE,
                             FOREIGN KEY (fk_aluno) REFERENCES aluno(id_aluno),
                             FOREIGN KEY (fk_turma) REFERENCES turma(id_turma)
);

CREATE TABLE aula (
                      id_aula INT AUTO_INCREMENT PRIMARY KEY,
                      fk_turma INT,
                      data_aula DATE NOT NULL,
                      status ENUM('MARCADA','CANCELADA') DEFAULT 'MARCADA',
                      FOREIGN KEY (fk_turma) REFERENCES turma(id_turma)
);

CREATE TABLE aula_aluno (
                            id_aula_aluno INT AUTO_INCREMENT PRIMARY KEY,
                            fk_aula INT,
                            fk_aluno INT,
                            status ENUM('AGENDADO','AUSENTE','CANCELADO','REPOSICAO') DEFAULT 'AGENDADO',
                            FOREIGN KEY (fk_aula) REFERENCES aula(id_aula),
                            FOREIGN KEY (fk_aluno) REFERENCES aluno(id_aluno)
);

CREATE TABLE reposicao (
                           id_reposicao INT AUTO_INCREMENT PRIMARY KEY,
                           fk_aluno INT,
                           fk_aula_origem INT,
                           fk_aula_destino INT,
                           status ENUM('AGENDADA','REALIZADA','CANCELADA') DEFAULT 'AGENDADA',
                           FOREIGN KEY (fk_aluno) REFERENCES aluno(id_aluno),
                           FOREIGN KEY (fk_aula_origem) REFERENCES aula(id_aula),
                           FOREIGN KEY (fk_aula_destino) REFERENCES aula(id_aula)
);

INSERT INTO professor (nome, telefone, email)
VALUES ('Thais Almeida','11988887777','thais@pilates.com');

INSERT INTO aluno (nome, telefone, cpf, email, data_nascimento, data_cadastro)
VALUES
    ('Bruno Silva','11999990000','12345678901','bruno@email.com','2003-04-12','2026-03-01'),
    ('Isabella Chagas','11988881111','98765432100','ana@email.com','2002-09-17','2026-03-01');

INSERT INTO plano (nome, frequencia_semanal, validade_dias)
VALUES
    ('2x por semana',2,30),
    ('3x por semana',3,30);

INSERT INTO turma (dia_semana, hora_inicio, capacidade_max, fk_professor)
VALUES
    ('SEGUNDA','10:00',5,1),
    ('QUARTA','10:00',5,1),
    ('SEXTA','10:00',5,1);

INSERT INTO aluno_plano (fk_aluno, fk_plano, data_inicio, data_fim)
VALUES (1,1,'2026-03-01','2026-03-31');

INSERT INTO aluno_turma (fk_aluno, fk_turma, data_inicio)
VALUES (1,1,'2026-03-01'),(1,2,'2026-03-01');

INSERT INTO aula (fk_turma,data_aula)
VALUES
    (1,'2026-03-02'),(1,'2026-03-09'),(1,'2026-03-16'),(1,'2026-03-23'),(1,'2026-03-30'),
    (2,'2026-03-04'),(2,'2026-03-11'),(2,'2026-03-18'),(2,'2026-03-25');

INSERT INTO aula_aluno (fk_aula,fk_aluno,status)
VALUES
    (1,1,'AUSENTE'),(2,1,'AUSENTE'),
    (3,1,'AGENDADO'),(4,1,'AGENDADO'),(5,1,'AGENDADO'),
    (6,1,'AGENDADO'),(7,1,'AGENDADO'),(8,1,'AGENDADO'),(9,1,'AGENDADO');

INSERT INTO aula (fk_turma,data_aula)
VALUES (3,'2026-03-14');

INSERT INTO reposicao (fk_aluno,fk_aula_origem,fk_aula_destino)
VALUES (
           1,
           1,
           (SELECT id_aula FROM aula ORDER BY id_aula DESC LIMIT 1)
    );

SELECT aluno.nome, dia_semana, hora_inicio
FROM aluno_turma
         INNER JOIN aluno ON fk_aluno = id_aluno
         INNER JOIN turma ON fk_turma = id_turma
WHERE id_turma = 1;

SELECT *
FROM aula
         INNER JOIN aula_aluno ON id_aula = fk_aula
         INNER JOIN aluno ON fk_aluno = id_aluno;

SELECT
    a1.data_aula AS aula_faltada,
    a2.data_aula AS aula_reposicao,
    r.status
FROM reposicao r
         JOIN aula a1 ON r.fk_aula_origem = a1.id_aula
         JOIN aula a2 ON r.fk_aula_destino = a2.id_aula
WHERE r.fk_aluno = 1
  AND a2.data_aula BETWEEN '2026-03-01' AND '2026-03-31'
ORDER BY a2.data_aula;