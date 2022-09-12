package com.ll.exam.sbb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class AnswerRepositoryTests {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    private int lastSampleDataId;

    @BeforeEach
    //본 어노테이션을 붙인 메서드는 테스트 메서드(@Test) 실행 이전에 수행된다.
    void beforeEach() {
        clearData();
        createSampleData();
    }

//clearData와 createSampleData가 question,answerTests 모두에
// 중복으로 있었기에 questionRepository의 것을 가져와 사용한다.
    private void clearData() {
        QuestionRepositoryTests.clearData(questionRepository);
        //questionRepository truncate는 이제 questionRepositoryTests의 clearData에서 실행한다.
        answerRepository.disableForeignKeyChecks();
        answerRepository.truncate();
        answerRepository.enableForeignKeyChecks();
        //RepositoryUtil을 인터페이스 다중 상속을 통해 QuestionRepository와 EnswerRepository 모두
        // 각자의 repository를 참조하여 외래키 관계를 끊을 수 있도록 변경
    }

    private void createSampleData() {
        QuestionRepositoryTests.createSampleData(questionRepository);
    }

    @Test
    void 저장() {
        Question q = questionRepository.findById(2).get();

        Answer a = new Answer();
        a.setContent("네 자동으로 생성됩니다.");
        a.setQuestion(q);
        a.setCreateDate(LocalDateTime.now());
        answerRepository.save(a);
    }
}