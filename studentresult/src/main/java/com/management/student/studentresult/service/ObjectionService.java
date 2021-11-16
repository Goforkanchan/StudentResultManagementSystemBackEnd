package com.management.student.studentresult.service;

import com.management.student.studentresult.dao.*;
import com.management.student.studentresult.enums.Operation;
import com.management.student.studentresult.repository.MarksRepository;
import com.management.student.studentresult.repository.ObjectionRepository;
import com.management.student.studentresult.repository.SubjectRepository;
import com.management.student.studentresult.vo.MarksVO;
import com.management.student.studentresult.vo.ObjectionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ObjectionService {

    @Autowired
    ObjectionRepository objectionRepository;

    @Autowired
    UserService userService;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    MarksRepository repository;



    public Objection raiseObjection(MarksVO marksVO){
        Objection objection = new Objection();
        User user = userService.getUserByExtId(marksVO.getRollNo());
        objection.setCreatedBy(user);
//        objection.setStatus("");
        Subject subject = subjectRepository.findBySubCode(marksVO.getSubjectCode());
        Marks mark = repository.findByUserAndSubjectAndTermAndYear(user, subject, marksVO.getTerm(), marksVO.getYear());
        objection.setMarks(mark);
//        objection.setResolverId(mark.getCreatedBy());
        objectionRepository.save(objection);
        String response = "Objection raised Successfullly";
        return objection;
    }

    public List<ObjectionVO> resolveObjection(List<ObjectionVO> objectionVOS) {
        List<ObjectionVO> objectionVOList = new ArrayList<>();
        for (ObjectionVO objection : objectionVOS) {
            User user = userService.getUserByExtId(objection.getRollNo());
            Subject subject = subjectRepository.findBySubCode(objection.getSubjectCode());
            Marks mark = repository.findByUserAndSubjectAndTermAndYear(user, subject, objection.getTerm(), objection.getYear());
            User moderator = userService.getUserById(mark.getCreatedBy().getUserId());
            Objection obj = objectionRepository.findByMarks(mark);
            obj.setComment(objection.getComments());
            obj.setResolverId(mark.getCreatedBy());
            if (objection.getOperation().equals("RESOLVED"))
                obj.setStatus("RESOLVED");
            else
                obj.setStatus("REJECTED");
            objectionRepository.save(obj);
            objectionVOList.add(objection);

        }
        return objectionVOList;
    }
}
