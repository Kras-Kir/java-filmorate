<!-- -*- coding: utf-8 -*- -->
# java-filmorate
Template repository for Filmorate project.
![ER-diagram](ERfilmorate.png)
## ������� SQL-��������

���� ��������� �������� ������� � ���� ������ �������.
-- �������� ��� ������

-- �������� ��� ������
SELECT * FROM films;

-- ����� ������ �� ����� (��������, '�������')
SELECT f.* 
FROM film f
JOIN genres_film fg ON id = fg.film_id
JOIN genre g ON fg.genre_id = g.genre_id
WHERE g.name = '�������';

