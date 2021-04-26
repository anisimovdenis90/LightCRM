--доступы к действиям с данными
--дополнить при добавлении новых сущностей
INSERT INTO priorities(name, visible_name)
VALUES
('FULL_ACCESS', 'Полный доступ'),
--
('ADD_PROFILE', 'Создание профиля'),
('READ_PROFILE', 'Просмотр профиля'),
('CHANGE_PROFILE', 'Изменение профиля'),
('DELETE_PROFILE', 'Удаление профиля'),
--
('ADD_TASK', 'Создание задачи '),
('READ_TASK', 'Просмотр задачи'),
('CHANGE_TASK', 'Изменение задачи'),
('DELETE_TASK', 'Удаление задачи'),
--
('ADD_PROJECT', 'Создание проекта'),
('READ_PROJECT', 'Просмотр проекта'),
('CHANGE_PROJECT', 'Изменение проекта'),
('DELETE_PROJECT', 'Удаление проекта'),
--
('ADD_TASK_STATE', 'Создание статуса задачи'),
('READ_TASK_STATE', 'Просмотр статуса задачи'),
('CHANGE_TASK_STATE', 'Изменение статуса задачи'),
('DELETE_TASK_STATE', 'Удаление статуса задачи'),
--
('ADD_STAFF_UNIT', 'Создание должности'),
('READ_STAFF_UNIT', 'Просмотр должности'),
('CHANGE_STAFF_UNIT', 'Изменение должности'),
('DELETE_STAFF_UNIT', 'Удаление должности'),
--
('ADD_DEPARTMENT', 'Создание отдела'),
('READ_DEPARTMENT', 'Просмотр отдела'),
('CHANGE_DEPARTMENT', 'Изменение отдела'),
('DELETE_DEPARTMENT', 'Удаление отдела'),
--
('ADD_CONTACT', 'Создание контакта'),
('READ_CONTACT', 'Просмотр контакта'),
('CHANGE_CONTACT', 'Изменение контакта'),
('DELETE_CONTACT', 'Удаление контакта'),
--
('ADD_COMPANY', 'Создание организации'),
('READ_COMPANY', 'Просмотр организации'),
('CHANGE_COMPANY', 'Изменение организации'),
('DELETE_COMPANY', 'Удаление организации'),
--
('ADD_COMMENT', 'Создание комментария'),
('READ_COMMENT', 'Просмотр комментария'),
('CHANGE_COMMENT', 'Изменение комментария'),
('DELETE_COMMENT', 'Удаление комментария');

--роли
INSERT INTO roles(name, visible_name)
VALUES
('ROLE_ADMINISTRATOR' , 'Администратор'),
('ROLE_GUEST', 'Неавторизованный'),
('ROLE_ONLY_READ', 'Только чтение'),--зарегистрированный только чтение
('ROLE_MANAGER', 'Менеджер'),
('ROLE_PROJECT_MANAGER', 'Менеджер проекта'),
('ROLE_HEAD_OF_DEPARTMENT', 'Начльник отдела'),
('ROLE_HEAD_OF_COMPANY', 'Руководитель организации');

--роли и доступы
INSERT INTO roles_priorities(role_id, priority_id)
VALUES
--администратор
(1, 1),
--глава компании
(7, 1),
--гость
(2, 2),
--пользователь только чтение
(3, 3),
(3, 4),
(3, 7),
(3, 11),
(3, 15),
(3, 19),
(3, 23),
(3, 27),
(3, 31),
(3, 35),
--менеджер
(4, 3),
(4, 4),
(4, 6),
(4, 7),
(4, 8),
(4, 11),
(4, 15),
(4, 19),
(4, 23),
(4, 26),
(4, 27),
(4, 28),
(4, 29),
(4, 31),
(4, 32),
(4, 33),
(4, 35),
(4, 36),
(4, 37),
--руководитель проекта
(5, 3),
(5, 4),
(5, 6),
(5, 7),
(5, 8),
(5, 10),
(5, 11),
(5, 12),
(5, 15),
(5, 19),
(5, 23),
(5, 26),
(5, 27),
(5, 28),
(5, 29),
(5, 31),
(5, 32),
(5, 33),
(5, 35),
(5, 36),
(5, 37),
--руководитель отдела
(6, 3),
(6, 4),
(6, 6),
(6, 7),
(6, 8),
(6, 10),
(6, 11),
(6, 12),
(6, 15),
(6, 18),
(6, 19),
(6, 20),
(6, 23),
(6, 24),
(6, 26),
(6, 27),
(6, 28),
(6, 29),
(6, 31),
(6, 32),
(6, 33),
(6, 35),
(6, 36),
(6, 37);

--должность/штатные единицы
INSERT INTO staff_units(name)
VALUES
('Генеральный директор'),
('Заместитель генерального директора'),
('Начальник IT-департамента'),
('Начальник отдела продаж'),
('Главный бухгалтер'),
('Старший администратор'),
('Администратор'),
('Руководитель проекта'),
('Старший менеджер'),
('Менеджер'),
('Бухгалтер');

--роли должностей
INSERT INTO staff_units_roles(staff_unit_id, role_id)
VALUES
(1, 7),
(2, 7),
(3, 1),
(4, 6),
(5, 6),
(6, 1),
(7, 1),
(8, 5),
(9, 4),
(10, 4),
(11, 4);

--статусы задач
INSERT INTO task_states(name)
VALUES
('Новая'),
('Назначена'),
('В Работе'),
('Решена'),
('Закрыта'),
('Отменена');

--пользователи пароли 123
INSERT INTO users(login, password)
VALUES
('admin', '$2a$10$L2.xqqPpj/w8Jzy30yVfNu.e6iDiztQgxY6swxSShaWuXx1bovc5.'),
('director', '$2a$10$L2.xqqPpj/w8Jzy30yVfNu.e6iDiztQgxY6swxSShaWuXx1bovc5.'),
('manager1', '$2a$10$L2.xqqPpj/w8Jzy30yVfNu.e6iDiztQgxY6swxSShaWuXx1bovc5.'),
('manager2', '$2a$10$L2.xqqPpj/w8Jzy30yVfNu.e6iDiztQgxY6swxSShaWuXx1bovc5.');

--профили
INSERT INTO
profiles(firstname, lastname, middlename, sex, phone, email, birthdate, employment_date, user_id, staff_unit_id, search_index)
VALUES
('Петр', 'Петров','Петрович', 'мужской', '79009009090', 'petr@comp.com', '1990-01-01', '2010-10-01', 1, 6, 'Петров Петр Петрович'),
('Виктор', 'Викторов','Викторович', 'мужской', '78008008080', 'dir@comp.com', '1980-09-01', '2010-10-01', 2, 1, 'Викторов Виктор Викторович'),
('Анна', 'Петрова','Петровна', 'женский', '77007008080', 'manager1@comp.com', '2000-05-01', '2010-10-01', 3, 9, 'Петрова Анна Петровна'),
('Василий', 'Сидоров','Федорович', 'мужской', '79007008080', 'manager2@comp.com', '2001-05-01', '2010-10-01', 4, 10, 'Сидоров Василий Федорович');

--департаменты
INSERT INTO departments(name, description, leader_id)
VALUES
('IT-отдел', 'Админы, прогеры и прочее', 1),
('Отдел продаж', 'манагеры', 2);

--работники отделов
INSERT INTO departments_profiles(profile_id, department_id)
VALUES
(1, 1),
(3, 2),
(4, 2);

--компании и клиенты
INSERT INTO companies(name, type, inn, bill_number, phone_number, email, search_index)
VALUES
('ООО Вектор', true, 58758758964, 89544852485548, '79185185618', 'sales@vector.su', 'ООО Вектор'),
('ИП Федоров Виктор Акакиевич', true, 456789123123, 88445662211333, '79185185000', 'akaki@gmail.com', 'ИП Федоров Виктор Акакиевич'),
('Василий Ермак', false, 0, 0, '79185186006', 'ermak@gmail.com', 'Василий Ермак');

--контактные лица
INSERT INTO contacts(name, post, phone, email, description, company_id, search_index)
VALUES
('Васечкин Петр', 'Москва, ул. Панфиловцев, 111', '89457894512', 'was@vector.su', 'будни с 10 до 18', 1, 'Васечкин Петр'),
('Федоров Виктор', 'Москва, ул. Где то, 122', '79185185000', 'akaki@gmail.com', 'будни с 10 до 19', 2, 'Федоров Виктор'),
('Василий Ермак', 'Москва, ул. Там то, 123', '79185186006', 'ermak@gmail.com', 'будни с 10 до 19', 3, 'Василий Ермак');

--менеджеры компаний
INSERT INTO companies_managers(profile_id, company_id)
VALUES
(3, 1),
(4, 2),
(4, 3);

--проекты
INSERT INTO projects(name, description, manager_id)
VALUES
('Проект 1', 'пример проекта', 2);

--исполнители проекта
INSERT INTO employees_projects(profile_id, project_id)
VALUES
(3,1),
(4,1);

--задачи
INSERT INTO tasks(title, description, producer_id, responsible_id, start_date, task_state_id, allow_change_deadline, project_id, company_id, search_index)
VALUES
('Задача 1', 'Написать письмо клиенту', 2, 3, '2021-02-10', 1, false, null, null, 'Задача 1 Написать письмо клиенту'),
('Задача 2', 'Провести анализ по проекту', 2, 4, '2021-03-10', 1, true, 1, 1, 'Задача 2 Провести анализ по проекту'),
('Задача 3', 'Посчитать затраты', 2, 4, '2021-03-10', 1, true, 1, null, 'Задача 3 Посчитать затраты');

--соисполнители
INSERT INTO tasks_coexecutors(task_id, profile_id)
VALUES
(1, 4);

--наблюдатели задачи
INSERT INTO tasks_spectators(task_id, profile_id)
VALUES
(1, 1),
(2, 1),
(3, 1);

--комментарии
INSERT INTO comments(author_id, created_date, text)
VALUES
(1, '2021-02-10 10:12:00', 'Комментарий к задаче'),
(1, '2021-02-10 10:12:00', 'Комментарий к задаче новый'),
(1, '2021-02-10 10:12:00', 'Комментарий к компании');

--комментарий к задаче
INSERT INTO tasks_comments(task_id, comment_id)
VALUES
(2, 1),
(2, 2);

--комментарий компании
INSERT INTO companies_comments(company_id, comment_id)
VALUES
(1, 3);

--теги
INSERT INTO tags(name)
VALUES
('тег 1'),
('тег 2'),
('тег 3');

--Чаты
INSERT INTO chat_room(chat_id, sender_id, recipient_id)
VALUES
('1_2', 1, 2),
('1_2', 2, 1),
('1_3', 1, 3),
('1_3', 3, 1);

--Сообщения
INSERT INTO chat_message(chat_id, sender_id, recipient_id, sender_name, recipient_name, content, timestamp, message_status)
VALUES
('1_2', 1, 2, 'Петр Петров', 'Виктор Викторов', 'Превед, медвед!', '2021-04-01 10:00:00.000', 0),
('1_2', 2, 1, 'Виктор Викторов', 'Петр Петров', 'Превед, кросафчег!', '2021-04-01 10:02:00.000', 0),
('1_3', 1, 3, 'Петр Петров', 'Анна Петрова', 'Чокаво?', '2021-04-01 10:05:00.000', 0);

--Рабочие дни
INSERT INTO working_days(start_timestamp, end_timestamp, report, profile_id)
VALUES
('2021-04-12 08:12:11', '2021-04-12 17:12:07', 'Создание и поддержание учетных записей пользователей в актуальном состоянии', 1),
('2021-04-13 08:07:33', '2021-04-13 18:00:03', 'Планирование по расширению сетевой инфраструктуры', 1),
('2021-04-14 08:01:19', '2021-04-14 17:03:44', 'Мониторинг и устранение неполадок в системе', 1),
('2021-04-15 08:13:40', '2021-04-15 17:02:54', 'Подготовка и сохранение резервных копий данных', 1),
('2021-04-16 08:40:55', '2021-04-16 17:30:32', 'Обновление пакетов', 1);
