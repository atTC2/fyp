-- Find all papers
SELECT * FROM fyp.paper;
-- Find all key phrases
SELECT * FROM fyp.key_phrase;
SELECT * FROM fyp.hyponym;
SELECT * FROM fyp.syn_link;
SELECT * FROM fyp.synonym;

-- Find overlapping key phrases
SELECT kp1.* FROM key_phrase kp1, key_phrase kp2 WHERE kp1.paper = kp2.paper AND kp1.end > kp2.start AND kp1.end < kp2.end;

-- Find a certain type of key phrases
SELECT * FROM fyp.key_phrase WHERE classification = "Task";

-- Papers not finished processing
SELECT * FROM fyp.paper WHERE status != 4;
-- Finish them
UPDATE fyp.paper SET status = -1 WHERE status != 4;

-- Proving case insentivity
SELECT * FROM paper WHERE text LIKE '%xylanases%'; # Proves default case insentivity
SELECT * FROM paper WHERE text REGEXP 'xylanases|GALLIUM'; # Proves regex default case insentivity

-- Count KPs
SELECT COUNT(DISTINCT sl.id) FROM syn_link sl, synonym s, key_phrase kp, paper p WHERE p.id = 6 AND p.id = kp.paper AND kp.id = s.kp AND sl.id = s.id;

-- Get related KPs from syonyms
SELECT * FROM synonym WHERE kp != 1224 AND syn_link IN (SELECT syn_link FROM synonym WHERE kp = 1224);
