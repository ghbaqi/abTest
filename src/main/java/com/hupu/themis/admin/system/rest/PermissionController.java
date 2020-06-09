package com.hupu.themis.admin.system.rest;

import cn.hutool.core.date.DateUtil;
import com.hupu.themis.admin.modules.common.aop.log.Log;
import com.hupu.themis.admin.modules.common.exception.BadRequestException;
import com.hupu.themis.admin.system.domain.Permission;
import com.hupu.themis.admin.system.service.PermissionService;
import com.hupu.themis.admin.system.service.dto.PermissionDTO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */
@Api(value = "/api/permission", description = "权限资源模块")
@RestController
@RequestMapping("api/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    private static final String ENTITY_NAME = "permission";

    @GetMapping(value = "/query/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERMISSION_ALL','PERMISSION_SELECT')")
    public ResponseEntity getPermissions(@PathVariable Long id){
        return new ResponseEntity(permissionService.findById(id), HttpStatus.OK);
    }

    /**
     * 返回全部的权限，新增角色时下拉选择
     * @return
     */
    @GetMapping(value = "/tree")
    @PreAuthorize("hasAnyRole('ADMIN','PERMISSION_ALL','PERMISSION_SELECT','ROLES_SELECT','ROLES_ALL')")
    public ResponseEntity getRoleTree(){
        return new ResponseEntity(permissionService.getPermissionTree(permissionService.findByPid(0L)),HttpStatus.OK);
    }

    @Log(description = "查询权限")
    @GetMapping(value = "/list")
    @PreAuthorize("hasAnyRole('ADMIN','PERMISSION_ALL','PERMISSION_SELECT')")
    public ResponseEntity getPermissions(@RequestParam(required = false) String name){
        List<PermissionDTO> permissionDTOS = permissionService.queryAll(name);
        return new ResponseEntity(permissionService.buildTree(permissionDTOS),HttpStatus.OK);
    }

    @Log(description = "新增权限")
    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('ADMIN','PERMISSION_ALL','PERMISSION_CREATE')")
    public ResponseEntity create(@Validated @RequestBody Permission resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        resources.setCreateTime(DateUtil.date().toTimestamp());
        return new ResponseEntity(permissionService.create(resources),HttpStatus.CREATED);
    }

    @Log(description = "修改权限")
    @PostMapping(value = "/edit")
    @PreAuthorize("hasAnyRole('ADMIN','PERMISSION_ALL','PERMISSION_EDIT')")
    public ResponseEntity update(@Validated @RequestBody Permission resources){
        if (resources.getId() == null) {
            throw new BadRequestException(ENTITY_NAME +" ID Can not be empty");
        }
        permissionService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log(description = "删除权限")
    @GetMapping(value = "/del/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERMISSION_ALL','PERMISSION_DELETE')")
    public ResponseEntity delete(@PathVariable Long id){
        permissionService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
