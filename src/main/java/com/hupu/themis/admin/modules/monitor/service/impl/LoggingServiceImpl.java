package com.hupu.themis.admin.modules.monitor.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hupu.themis.admin.modules.common.utils.IpUtil;
import com.hupu.themis.admin.modules.common.utils.RequestHolder;
import com.hupu.themis.admin.modules.core.security.AuthorizationUser;
import com.hupu.themis.admin.modules.core.utils.JwtTokenUtil;
import com.hupu.themis.admin.modules.monitor.domain.Logging;
import com.hupu.themis.admin.modules.monitor.mapper.LoggingMapper;
import com.hupu.themis.admin.modules.monitor.service.LoggingService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 *
 * @date 2019-11-24
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LoggingServiceImpl extends ServiceImpl<LoggingMapper,Logging> implements LoggingService {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final String LOGINPATH = "authenticationLogin";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ProceedingJoinPoint joinPoint, Logging logging){

        // 获取request
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.hupu.themis.admin.modules.common.aop.log.Log log = method.getAnnotation(com.hupu.themis.admin.modules.common.aop.log.Log.class);

        // 描述
        if (log != null) {
            logging.setDescription(log.description());
        }

        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName()+"."+signature.getName()+"()";

        String params = "{";
        //参数值
        Object[] argValues = joinPoint.getArgs();
        //参数名称
        String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
        // 用户名
        String username = "";

        if(argValues != null){
            for (int i = 0; i < argValues.length; i++) {
                params += " " + argNames[i] + ": " + argValues[i];
            }
        }

        // 获取IP地址
        logging.setRequestIp(IpUtil.getIP(request));

        if(!LOGINPATH.equals(signature.getName())){
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            username = userDetails.getUsername();
        } else {
            AuthorizationUser user = JSONUtil.toBean(new JSONObject(argValues[0]),AuthorizationUser.class);
            username = user.getUsername();

        }
        logging.setMethod(methodName);
        logging.setUsername(username);
        logging.setParams(params + " }");
        baseMapper.insert(logging);
    }
}
