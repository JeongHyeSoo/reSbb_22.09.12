package com.ll.exam.sbb.question;


import com.ll.exam.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getList() {
        return this.questionRepository.findAll();
    }


    public Question getQuestion(int id) {

        Optional<Question> oq = questionRepository.findById(id);
        //Integer나 Double 클래스처럼 'T' 타입의 객체를 포장해주는 래퍼 클래스(Wrapper class)이다.[제네릭 클래스]
        //
        if ( oq.isPresent() ) {
            return oq.get();
        }

        throw new DataNotFoundException("question not found");
    }
}