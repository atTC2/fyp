-- Find all papers
SELECT * FROM fyp.paper;
-- Find all key phrases
SELECT * FROM fyp.key_phrase;
SELECT * FROM fyp.hyponym;
SELECT * FROM fyp.synonym;

SELECT * FROM fyp.key_phrase WHERE paper = 1 AND relative_id = 1;