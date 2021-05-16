package ru.lightcrm.services.interfaces;

import ru.lightcrm.entities.Comment;

public interface CommentService {
    Comment findById(Long id);
}
