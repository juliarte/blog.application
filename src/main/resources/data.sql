INSERT INTO user_details (username, password)
VALUES ('user1', 'password1');

INSERT INTO user_details (username, password)
VALUES ('user2', 'password2');

INSERT INTO user_details (username, password)
VALUES ('user3', 'password3');

INSERT INTO posts (content, user_id, created_at)
VALUES ('Post 1 for user1', (SELECT id FROM user_details WHERE username = 'user1'), CURRENT_TIMESTAMP);

INSERT INTO posts (content, user_id, created_at)
VALUES ('Post 2 for user1', (SELECT id FROM user_details WHERE username = 'user1'), CURRENT_TIMESTAMP);

INSERT INTO posts (content, user_id, created_at)
VALUES ('Post 1 for user2', (SELECT id FROM user_details WHERE username = 'user2'), CURRENT_TIMESTAMP);

INSERT INTO posts (content, user_id, created_at)
VALUES ('Post 2 for user2', (SELECT id FROM user_details WHERE username = 'user2'), CURRENT_TIMESTAMP);

INSERT INTO posts (content, user_id, created_at)
VALUES ('Post 1 for user3', (SELECT id FROM user_details WHERE username = 'user3'), CURRENT_TIMESTAMP);

INSERT INTO posts (content, user_id, created_at)
VALUES ('Post 2 for user3', (SELECT id FROM user_details WHERE username = 'user3'), CURRENT_TIMESTAMP);