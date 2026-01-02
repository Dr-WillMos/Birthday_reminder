package com.Birthday_reminder.controller;

import com.Birthday_reminder.domain.Relationship;
import com.Birthday_reminder.dto.RelationshipPageDTO;
import com.Birthday_reminder.service.RelationPlanService;
import com.Birthday_reminder.vo.PageVO;
import com.Birthday_reminder.vo.RelationshipPageVO;
import com.Birthday_reminder.vo.ResponseEntity;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.Birthday_reminder.service.RelationshipService;

@RestController
@RequestMapping("/api/relationship")
public class RelationshipController {
    @Autowired
    private RelationPlanService relationPlanService;

    @Resource
    private RelationshipService relationshipService;

    @GetMapping("/page")
    public ResponseEntity<PageVO<RelationshipPageVO>> page(RelationshipPageDTO dto){

        if (dto.getPage() == null || dto.getPage() <= 0) {
            dto.setPage(1);
        }
        if (dto.getSize() == null || dto.getSize() <= 0) {
            dto.setSize(10);
        }

        PageInfo<RelationshipPageVO> pageInfo = relationshipService.queryRelationshipPage(dto);
        PageVO<RelationshipPageVO> pageVO = new PageVO<>(
                pageInfo.getList(),
                pageInfo.getPages(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getTotal()
        );
        return ResponseEntity.success(pageVO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Relationship> get(@PathVariable Integer id) {
        return ResponseEntity.success(relationshipService.getRelationship(id));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Integer> create(@RequestBody Relationship relationship) {
        try {
            Relationship relation = relationshipService.createRelationship(relationship);
            relationPlanService.plan(relation);
            return ResponseEntity.success(relation.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.fail("创建亲友关系失败: " + e.getMessage());
        }
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Boolean> update(@RequestBody Relationship relationship) {
        Boolean b = relationshipService.updateRelationship(relationship);
        relationPlanService.plan(relationship);
        return ResponseEntity.success(b);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        Boolean b = relationshipService.deleteRelationship(id);
        relationPlanService.deleteByRelationshipId(id);
        return ResponseEntity.success(b);
    }
}
