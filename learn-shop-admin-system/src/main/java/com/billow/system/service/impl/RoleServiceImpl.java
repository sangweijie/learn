package com.billow.system.service.impl;

import com.billow.common.base.DefaultSpec;
import com.billow.system.dao.MenuDao;
import com.billow.system.dao.PermissionDao;
import com.billow.system.dao.RoleDao;
import com.billow.system.dao.RoleMenuDao;
import com.billow.system.dao.RolePermissionDao;
import com.billow.system.dao.UserRoleDao;
import com.billow.system.pojo.po.MenuPo;
import com.billow.system.pojo.po.PermissionPo;
import com.billow.system.pojo.po.RoleMenuPo;
import com.billow.system.pojo.po.RolePermissionPo;
import com.billow.system.pojo.po.RolePo;
import com.billow.system.pojo.po.UserRolePo;
import com.billow.system.pojo.vo.RoleVo;
import com.billow.system.service.RoleService;
import com.billow.system.service.redis.CommonRoleMenuRedis;
import com.billow.system.service.redis.CommonRolePermissionRedis;
import com.billow.tools.utlis.ConvertUtils;
import com.billow.tools.utlis.ToolsUtils;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色信息
 *
 * @author liuyongtao
 * @create 2018-11-05 16:16
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private RolePermissionDao rolePermissionDao;
    @Autowired
    private RoleMenuDao roleMenuDao;
    @Autowired
    private CommonRolePermissionRedis commonRolePermissionRedis;
    @Autowired
    private CommonRoleMenuRedis commonRoleMenuRedis;
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private MenuDao menuDao;

    @Override
    public List<RoleVo> findRoleListInfoByUserId(Long userId) {
        List<RoleVo> roleVos = new ArrayList<>();
        List<UserRolePo> userRolePos = userRoleDao.findRoleIdByUserId(userId);
        if (ToolsUtils.isNotEmpty(userRolePos)) {
            userRolePos.stream().forEach(userRolePo -> {
                RolePo rolePo = roleDao.findOne(userRolePo.getRoleId());
                RoleVo roleVo = ConvertUtils.convert(rolePo, RoleVo.class);
                roleVos.add(roleVo);
            });
        }
        return roleVos;
    }

    @Override
    public Page<RolePo> findRoleByCondition(RoleVo roleVo) throws Exception {
        RolePo rolePo = ConvertUtils.convert(roleVo, RolePo.class);
        DefaultSpec<RolePo> defaultSpec = new DefaultSpec<>(rolePo);
        Pageable pageable = new PageRequest(roleVo.getPageNo(), roleVo.getPageSize());
        Page<RolePo> rolePos = roleDao.findAll(defaultSpec, pageable);
        return rolePos;
    }

    @Override
    public List<Long> findPermissionByRoleId(Long roleId) throws Exception {
        // 查询权限信息
        List<RolePermissionPo> rolePermissionPos = rolePermissionDao.findByRoleIdIsAndValidIndIsTrue(roleId);
        return rolePermissionPos.stream().map(m -> m.getPermissionId()).collect(Collectors.toList());
    }

    @Override
    public List<String> findMenuByRoleId(Long roleId) throws Exception {
        List<RoleMenuPo> roleMenuPos = roleMenuDao.findByRoleIdIsAndValidIndIsTrue(roleId);
        return roleMenuPos.stream().map(m -> m.getMenuId().toString()).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveRole(RoleVo roleVo) {
        // 保存/修改角色信息
        RolePo rolePo = ConvertUtils.convert(roleVo, RolePo.class);
        Long id = rolePo.getId();
        if (id != null) {
            RolePo one = roleDao.findOne(id);
            // 更新角色CODE
            commonRolePermissionRedis.updateRoleCode(roleVo.getRoleCode(), one.getRoleCode());
            commonRoleMenuRedis.updateRoleCode(roleVo.getRoleCode(), one.getRoleCode());
        }

        roleDao.save(rolePo);

        if (id != null) {
            // 删除原始的关联菜单数据
            List<RoleMenuPo> delRoleMenus = roleMenuDao.findByRoleIdIsAndValidIndIsTrue(roleVo.getId());
            roleMenuDao.deleteInBatch(delRoleMenus);
            // 删除原始的关联权限数据
            List<RolePermissionPo> delrolePermissions = rolePermissionDao.findByRoleIdIsAndValidIndIsTrue(roleVo.getId());
            rolePermissionDao.deleteInBatch(delrolePermissions);
        }
        // 用于更新 redis 中的角色对应的菜单信息
        List<MenuPo> newMenuPos = new ArrayList<>();
        // 保存/修改菜单信息
        List<RoleMenuPo> roleMenuPos = roleVo.getMenuChecked().stream().map(m -> {
            RoleMenuPo roleMenuPo = new RoleMenuPo();
            roleMenuPo.setRoleId(rolePo.getId());
            roleMenuPo.setMenuId(new Long(m));
            roleMenuPo.setValidInd(true);
            newMenuPos.add(menuDao.findOne(new Long(m)));
            return roleMenuPo;
        }).collect(Collectors.toList());
        roleMenuDao.save(roleMenuPos);
        // 更新指定角色的菜单信息
        commonRoleMenuRedis.updateRoleMenuByRoleCode(newMenuPos, rolePo.getRoleCode());

        // 用于更新 redis 中的角色对应的权限信息
        List<PermissionPo> newPermissionPos = new ArrayList<>();
        // 保存/修改权限信息
        List<RolePermissionPo> rolePermissionPos = roleVo.getPermissionChecked().stream().map(m -> {
            RolePermissionPo rolePermissionPo = new RolePermissionPo();
            rolePermissionPo.setRoleId(rolePo.getId());
            rolePermissionPo.setPermissionId(new Long(m));
            rolePermissionPo.setValidInd(true);
            // 查询出该角色要更新的权限
            newPermissionPos.add(permissionDao.findOne(new Long(m)));
            return rolePermissionPo;
        }).collect(Collectors.toList());
        rolePermissionDao.save(rolePermissionPos);
        // 更新指定角色的权限信息
        commonRolePermissionRedis.updateRolePermissionByRoleCode(newPermissionPos, rolePo.getRoleCode());
    }
}