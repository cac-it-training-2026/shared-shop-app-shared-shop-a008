-- Issue #5 おみくじログインボーナス機能用
-- usersテーブルにポイント用カラムを追加
ALTER TABLE users ADD point NUMBER(6) DEFAULT 0 NOT NULL;
