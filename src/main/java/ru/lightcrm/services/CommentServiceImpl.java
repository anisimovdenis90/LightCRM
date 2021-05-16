package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.lightcrm.entities.Comment;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.CommentRepository;
import ru.lightcrm.services.interfaces.CommentService;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Комментарий с Id %s не найден.", id)));
    }
}
