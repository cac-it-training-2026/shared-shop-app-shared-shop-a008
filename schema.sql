-- カテゴリテーブルの作成
CREATE TABLE categories (
  id NUMBER(2) PRIMARY KEY,
  name VARCHAR2(15 CHAR) NOT NULL,
  description VARCHAR2(30 CHAR),
  delete_flag NUMBER(1) DEFAULT 0 NOT NULL,
  insert_date DATE DEFAULT SYSDATE NOT NULL
);

-- 商品テーブルの作成
CREATE TABLE items (
  id NUMBER(6) PRIMARY KEY,
  name VARCHAR2(100 CHAR) NOT NULL,
  kana VARCHAR2(100 CHAR) NOT NULL,
  price NUMBER(7) NOT NULL,
  description VARCHAR2(400 CHAR),
  stock NUMBER(4) DEFAULT 0 NOT NULL,
  image VARCHAR2(64 CHAR),
  category_id NUMBER(2) REFERENCES categories(id) NOT NULL,
  delete_flag NUMBER(1) DEFAULT 0 NOT NULL,
  insert_date DATE DEFAULT SYSDATE NOT NULL
);

-- 会員テーブルの作成
CREATE TABLE users (
  id NUMBER(6) PRIMARY KEY,
  email VARCHAR2(256) UNIQUE NOT NULL,
  password VARCHAR2(16) NOT NULL,
  name VARCHAR2(30 CHAR) NOT NULL,
  postal_code VARCHAR2(7) NOT NULL,
  address VARCHAR2(150 CHAR) NOT NULL,
  phone_number VARCHAR2(11) NOT NULL,
  authority NUMBER(1) NOT NULL,
  delete_flag NUMBER(1) DEFAULT 0 NOT NULL,
  insert_date DATE DEFAULT SYSDATE NOT NULL
);

-- 注文テーブル
CREATE TABLE orders (
  id NUMBER(6) PRIMARY KEY,
  postal_code VARCHAR2(7) NOT NULL,
  address VARCHAR2(150 CHAR) NOT NULL,
  name VARCHAR2(30 CHAR) NOT NULL,
  phone_number VARCHAR2(11) NOT NULL,
  pay_method NUMBER(1) NOT NULL,
  user_id NUMBER(6) REFERENCES users(id) NOT NULL,
  insert_date DATE DEFAULT SYSDATE NOT NULL
);

-- 注文商品テーブル
CREATE TABLE order_items (
  id NUMBER(6) PRIMARY KEY,
  quantity NUMBER(4) NOT NULL,
  order_id NUMBER(6) REFERENCES orders(id) NOT NULL,
  item_id NUMBER(6) REFERENCES items(id) NOT NULL,
  price NUMBER(7) NOT NULL
);

-- シーケンスの作成(カテゴリテーブル用)
CREATE SEQUENCE seq_categories NOCACHE;

-- シーケンスの作成(商品テーブル用)
CREATE SEQUENCE seq_items NOCACHE;

-- シーケンスの作成(会員テーブル用)
CREATE SEQUENCE seq_users NOCACHE;

-- シーケンスの作成(注文テーブル用)
CREATE SEQUENCE seq_orders NOCACHE;

-- シーケンスの作成(注文商品テーブル用)
CREATE SEQUENCE seq_order_items NOCACHE;

-- レコード登録(カテゴリ)削除フラグが0の複数件のカテゴリ情報
INSERT INTO categories VALUES(seq_categories.NEXTVAL, '食料品', '野菜類、肉類、海産物、加工食品などを扱います。', DEFAULT, DEFAULT);
INSERT INTO categories VALUES(seq_categories.NEXTVAL, '書籍', '和書、洋書、専門書、漫画、雑誌などを扱います。', DEFAULT, DEFAULT);
INSERT INTO categories VALUES(seq_categories.NEXTVAL, '文房具', '鉛筆、消しゴム、ペンなどを扱います。', DEFAULT, DEFAULT);

-- レコード登録(商品)
-- 在庫数が0個、画像データが登録
INSERT INTO items VALUES(seq_items.NEXTVAL, 'りんご', 'リンゴ', 100, '青森県産のりんごです。とってもみずみずしい！', 0, 'apple.jpg', 1, DEFAULT, DEFAULT);
-- 在庫数が1個、画像データが登録
INSERT INTO items VALUES(seq_items.NEXTVAL, '辞書', 'ジショ', 2000, 'これ一冊があれば大丈夫！', 1, 'dictionary.jpg', 2, DEFAULT, DEFAULT);
-- 在庫数が5個、画像データが未登録
INSERT INTO items VALUES(seq_items.NEXTVAL, 'オレンジ', 'オレンジ', 150, 'オーストラリア産のオレンジです。', 5, NULL, 1, DEFAULT, DEFAULT);
-- 在庫数が6個、画像データが未登録
INSERT INTO items VALUES(seq_items.NEXTVAL, 'スイカ', 'スイカ', 300, '熊本県産のスイカです。甘く美味しい！', 6, NULL, 1, DEFAULT, DEFAULT);
-- 在庫数が9999個、画像データが未登録
INSERT INTO items VALUES(seq_items.NEXTVAL, 'もっちゅりん', 'モッチュリン', 230, '今までにないもっちゅり食感のドーナツ！', 9999, NULL, 1, DEFAULT, DEFAULT);

-- レコード登録(会員)
INSERT INTO users VALUES(seq_users.NEXTVAL, 'tanaka_taro@test.co.jp', 'Testtest0', 'システム管理太郎', '1111111', '東京都台東区1-2-3 ABCビル10階', '0123456789', 0, DEFAULT, DEFAULT);
INSERT INTO users VALUES(seq_users.NEXTVAL, 'unyo_jiro@test.co.jp', 'Testtest1', '運用管理二郎', '1111111', '東京都台東区1-2-3 ABCビル10階', '0123456789', 1, DEFAULT, DEFAULT);
INSERT INTO users VALUES(seq_users.NEXTVAL, 'ippan_saburo@test.co.jp', 'Testtest2', '一般二郎', '1111111', '東京都台東区4-5-6 ABCマンション5階', '0123456789', 2, DEFAULT, DEFAULT);

INSERT INTO users VALUES(seq_users.NEXTVAL, 'okamoto_taro@test.co.jp', 'tester123', '田中太郎', '2222222', '東京都港区芝公園3-5-3  EDCビル10階', '0991234567', 0, DEFAULT, DEFAULT);
INSERT INTO users VALUES(seq_users.NEXTVAL, 'iiiiu_jiro@test.co.jp', 'tester787', '中山二郎', '2222222', '東京都港区芝公園3-5-3  EDCビル10階', '0872394891', 1, DEFAULT, DEFAULT);
INSERT INTO users VALUES(seq_users.NEXTVAL, 'bnvmvmv_saburo@test.co.jp', 'tester139', '山田三郎', '2223342', '東京都港区芝公園7-7-7  AAKKマンション1階', '0120223322', 2, DEFAULT, DEFAULT);

-- レコード登録(注文)データあり
--条件：一般会員での買い物履歴あり
--   備考：初期SQLの一般会員「一般三郎」の user_id は 3
INSERT INTO orders VALUES(seq_orders.NEXTVAL,'1111111','東京都台東区4-5-6 ABCマンション5階','一般三郎','0123456789',2,3,DEFAULT);

-- レコード登録(商品注文)データあり
INSERT INTO order_items VALUES(seq_order_items.NEXTVAL,4,seq_orders.CURRVAL,1,100);

-- コミット
COMMIT;
