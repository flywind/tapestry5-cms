package org.flywind.cms.pages.admin.log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.Messages;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.flywind.business.common.constants.FBaseConstants;
import org.flywind.business.common.constants.FLogConstants;
import org.flywind.business.common.exception.FException;
import org.flywind.business.common.utils.FLog;
import org.flywind.business.common.utils.MongoDBUtil;
import org.flywind.business.entities.base.FSysInfo;
import org.flywind.business.entities.sys.OperationLog;
import org.flywind.business.entities.sys.SysLog;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.LogService;
import org.flywind.cms.services.IState;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * <p>操作与系统日志切面通知类</p>
 * 
 * @author flywind(飞风)
 * @date 2016年1月19日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
@Aspect
public class FLogAspect {
	
	/**
	 * 记录日志
	 */
	private static final Logger logger = Logger.getLogger(FLogAspect.class);
	
	@Autowired
	protected IState state;
	
	@Autowired
	private LogService logService;
	
	/**
	 * MongoDB数据库的连接信息
	 */
	private Properties prop;
	
	public Properties getProp() {
		return prop;
	}

	public void setProp(Properties prop) {
		MongoDBUtil.setProp(prop);
		this.prop = prop;
	}
	
	@AfterReturning("@annotation(log)")
	public void addSuccessLog(JoinPoint jp, FLog log) {
		//logger.debug("记录成功日志。");
		
		try {
			FSysInfo session = state.getSysInfo();
			boolean writeFindLong = session.isWriteFindLog();
			
			//判断是否记录"查看列表"的操作日志
			if (!writeFindLong && FLogConstants.SELECT == log.optype()) {
				return;
			}
			
			User user = session.getUser();
			Messages messages = session.getMessages();
			Object[] args = jp.getArgs();
			//加上"-success"结尾，说明是操作成功的日志
			String info = messages.get(log.infokey() + "-success");
			info = formatInfo(info, args);
			
			if (log.logtype() == FLogConstants.OPERATION_LOG) {
				logger.debug("记录操作日志，操作类型：" + log.optype());
				
				addOperationLog(jp, log, user, info, FLogConstants.SUCCESS_LOG);
			} else {
				logger.debug("记录系统日志，操作类型：" + log.optype());
				
				addSysLog(jp, log, user, info, FLogConstants.SUCCESS_LOG);
			}
		} catch (Exception e) {
			//logger.error("记录日志失败", e);
		}
	}
	
	@AfterThrowing(pointcut = "@annotation(log)", throwing = "e")
	public void addErrorLog(JoinPoint jp, FLog log, Exception e) {
		//logger.debug("记录失败日志。");
		
		try {
			Messages messages = state.getSysInfo().getMessages();
			User user = state.getSysInfo().getUser();
			//加上"-error"结尾，说明是操作失败的日志
			String tmpinfo = messages.get(log.infokey() + "-error");
			
			final String info = formatFailInfo(tmpinfo, e, messages);
			
			if (log.logtype() == FLogConstants.OPERATION_LOG) {
				logger.debug("记录操作日志，操作类型：" + log.optype());
				
				addOperationLog(jp, log, user, info, FLogConstants.FAIL_LOG);
			} else {
				logger.debug("记录系统日志，操作类型：" + log.optype());
				
				addSysLog(jp, log, user, info, FLogConstants.FAIL_LOG);
			}
		} catch (Exception e1) {
			//logger.error("记录日志失败", e);
		}
	}
	
	/**
	 * 添加日志
	 * 
	 * @param jp
	 *        切入点对象
	 * @param log
	 *        日志注解对象
	 * @param user
	 *        当前登录用户
	 * @param info
	 *        日志信息
	 * @param result
	 *        操作结果(1.成功，2.操作性)
	 */
	private void addOperationLog(JoinPoint jp, FLog log, User user, String info, int result) {
		try {
			OperationLog opLog = new OperationLog(user.getUsername(), user.getName(), 
					log.optype(), info, result);
			opLog.setCustomerCode(state.getSysInfo().getCustomerCode());
			logService.createOperationLog(opLog);
		} catch (Exception e) {
			//logger.error("添加操作日志失败。", e);
		}
	}
	
	/**
	 * 添加系统日志
	 * 
	 * @param jp
	 *        切入点
	 * @param log
	 *        日志注解对象
	 */
	private void addSysLog(JoinPoint jp, FLog log, User user, String info, int result) {
		try {
			SysLog sysLog = new SysLog(user.getUsername(), user.getName(), 
					log.optype(), info, result);
			sysLog.setCustomerCode(state.getSysInfo().getCustomerCode());
			logService.createSysLog(sysLog);
		} catch (Exception e) {
			//logger.error("添加系统日志失败。", e);
		}
	}
	
	/**
	 * 格式化日志信息，将日志信息中的占位符替换成对应的值
	 * 
	 * @param info
	 *        带有占位符的原始日志信息
	 * @param args
	 *        操作对象集合
	 * @return
	 *        格式化后的日志信息
	 */
	private String formatInfo(String info, Object[] args) {
		try {
			List<String> attributes = new ArrayList<String>();
			//检查info字符串是否存在"${}"占位符
			if (null != info && info.contains("${") && info.contains("}")) {
				for (String str : info.split("\\$\\{")) {
					if (str.contains("}")) {
						String attribute = str.substring(FBaseConstants.FIRST_INDEX, str.indexOf("}"));
						attributes.add(attribute);
					}
				}
			} else {
				return info;
			}
			
			Map<String, Object> map = parseObjectArray(args, attributes);
			
			for (String attribute : attributes) {
				String oldChar = "${" + attribute + "}";
				String value;
				try {
					value = map.get(attribute).toString();
					info = info.replace(oldChar, value);
				} catch (Exception e) {
					logger.error(oldChar + ": 值不存在。");
				}
			}
		} catch (Exception e) {
			//logger.error("格式化日志信息失败。", e);
		}
		
		return info;
	}
	
	/**
	 * 格式化失败的日志信息
	 * 
	 * @param info
	 *        带有占位符的原始日志信息
	 * @param e
	 *        异常对象
	 * @return
	 *        格式化后的日志信息
	 *  
	 */
	private String formatFailInfo(String info, Exception e, Messages messages) {
		try {
			String exceptionKey = FBaseConstants.EMPTY_STRING;
			if (e instanceof FException) {
				exceptionKey = ((FException) e).getTypeKey();
			} else {
				exceptionKey = e.getMessage().replace(" ", "-");
			}
			//加上"-exception"结尾，说明是异常原因
			exceptionKey = exceptionKey + "-exception";
			String reason = messages.get(exceptionKey);
			info = info.replace("${failReason}", reason);
		} catch (Exception e2) {
			//logger.error("格式化日志信息失败。", e);
		}
		return info;
	}
	
	/**
	 * 解析操作对象中的数据，用做日志信息中的值
	 * 
	 * @param obj
	 *        操作对象
	 * @param attributes
	 *        日志信息中占位符属性集合
	 * @return
	 *        属性名与属性值的Map集合
	 */
	private Map<String, Object> parseObjectArray(Object[] obj, List<String> attributes) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FBaseConstants.IP_STRING, state.getSysInfo().getIp());
		try {
			if (null != obj && obj.length > 0) {
				Object o = obj[FBaseConstants.FIRST_INDEX];
				
				//当Service方法接收的参数是id时
				if (o instanceof Long) {
					map.put(FBaseConstants.ID_STRING, o);
				} else {
					//当Service方法接收的参数是对象
					Class<?> c = o.getClass();
					Class<?>[] cs = null;
					Object[] os = null;
					for (String attribute : attributes) {
						try {
							Field f = getField(attribute, c);
							f.setAccessible(true);
							String typeName = f.getType().getSimpleName();
							String methodName = "get";
							if ("boolean".equalsIgnoreCase(typeName) || "Boolean".equals(typeName)) {
								methodName = "is";
							}
							methodName += attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
							Method m = getMethod(methodName, c, cs);
							m.setAccessible(true);
							Object value = m.invoke(o, os);
							map.put(attribute, value);
						} catch (Exception e) {
							
						}
					}
				}
			}
		} catch (Exception e) {
			//logger.error("解析日志对象失败。", e);
		}
		return map;
	}
	
	/**
	 * 获取属性，如果子类没有找到再到父类找
	 * 
	 * @param attribute
	 *       属性类
	 * @param c
	 *       对应实例的class对象
	 * @return
	 *       Field对象
	 * @throws Exception
	 */
	private Field getField(String attribute, Class<?> c) {
		Field f = null;
		try {
			f = c.getDeclaredField(attribute);
			return f;
		} catch (Exception e) {
			if ("java.lang.Object".equals(c.getSuperclass().getName())) {
				return null;
			}
			return getField(attribute, c.getSuperclass());
		}
	}
	
	/**
	 * 获取方法，如果子类没有找到再到父类找
	 * 
	 * @param name
	 *        方法名
	 * @param c
	 *        对应实例的class对象
	 * @param cs
	 *        方法参数类型
	 * @return
	 *        Method对象
	 */
	private Method getMethod(String name, Class<?> c, Class<?>[] cs) {
		Method m = null;
		try {
			m = c.getMethod(name, cs);
			return m;
		} catch (Exception e) {
			if ("java.lang.Object".equals(c.getSuperclass().getName())) {
				return null;
			}
			return getMethod(name, c.getSuperclass(), cs);
		}
	}

}
