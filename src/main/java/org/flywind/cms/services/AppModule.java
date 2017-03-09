package org.flywind.cms.services;

import java.io.IOException;
import java.util.Locale;

import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateCreator;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestExceptionHandler;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.ResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptModuleConfiguration;
import org.apache.tapestry5.services.javascript.ModuleManager;
import org.apache.tapestry5.upload.services.UploadSymbols;
import org.flywind.cms.CmsSymbolConstants;
import org.flywind.cms.services.security.FSecurityRealm;
import org.slf4j.Logger;
import org.tynamo.security.Security;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.impl.SecurityFilterChain;

/**
 * <p>Tapestry IoC Module, 可以随意配置或扩展服务.</p>
 * @author flywind(飞风)
 * @date 2015年9月18日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
public class AppModule
{
    /**
     * 服务的绑定，绑定之后可以使用@Inject注入，在项目中调用
     * @param binder 服务绑定器
     */
    public static void bind(ServiceBinder binder)
    {
    	binder.bind(AuthorizingRealm.class,FSecurityRealm.class).withId(FSecurityRealm.class.getSimpleName());
    	//绑定线程服务
    	binder.bind(MyExecutorService.class,MyExecutorServiceImpl.class).withId(MyExecutorService.class.getSimpleName());
    }

    /**
     * 提供IOC默认配置，也可自己定义
     * @param configuration IOC配置
     */
    public static void contributeApplicationDefaults(
            MappedConfiguration<String, Object> configuration)
    {
    	//语言配置,种类可参考http://www.flywind.org/newtechnologydetail/185
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en,zh_CN");
        //产品模式：false时代表开发模式大多数修改不需要重启应用,编译部署时应设置为true
        configuration.add(SymbolConstants.PRODUCTION_MODE, true);
        configuration.add(SymbolConstants.COMBINE_SCRIPTS, true);
    	configuration.add(SymbolConstants.COMPRESS_WHITESPACE, true);
    	configuration.add(SymbolConstants.GZIP_COMPRESSION_ENABLED, true); 
        configuration.add(SymbolConstants.COMPACT_JSON, true);
        configuration.add(SymbolConstants.MINIFICATION_ENABLED, true);
        //configuration.add(SymbolConstants.ENABLE_PAGELOADING_MASK, false);
        
        //定义资源路径
        configuration.add(CmsSymbolConstants.PLUGINS_PATH, "classpath:/META-INF/modules/plugins");
        configuration.add(CmsSymbolConstants.MODULES_PATH, "classpath:/META-INF/modules");
        //定义jQuery模块的名称
        configuration.add(SymbolConstants.JAVASCRIPT_INFRASTRUCTURE_PROVIDER, "jquery");
        configuration.add(SymbolConstants.HMAC_PASSPHRASE, "testing, testing, 1... 2... 3...");
        
        //权限登录页
        configuration.add(SecuritySymbols.LOGIN_URL, "/login");

        //未经授权的重定向到这个页面
    	configuration.add(SecuritySymbols.UNAUTHORIZED_URL, "/Unauthorized");
        
        //权限登录成功之后的页面
    	configuration.add(SecuritySymbols.SUCCESS_URL, "/Start");
    	
    	configuration.add(UploadSymbols.FILESIZE_MAX, "1");

    	System.setProperty("file.encoding", "UTF-8");
        Locale.setDefault(Locale.CHINA);
    }
    
    /**
     * 贡献rest easy组件，webservice目录
     * @param configuration
     */
    public static void contributeResteasyPackageManager(Configuration<String> configuration)
    {
    	configuration.add("org.flywind.cms.rest");
    }
    
    /**
     * 贡献组件静态资源目录，组件开发时未贡献的目录在项目中无法调用
     * @param configuration
     */
    public static void contributeClasspathAssetAliasManager(
			MappedConfiguration<String, String> configuration) {
		//定义modules资源路径,如果是modules也可以不定义，默认会是modules这个文件夹
		configuration.add("modules-root", "META-INF/modules");
	}
    
    /**
     * requireJS shim---注册jQuery模块
     * @param configuration
     * @param fastclick 组件
     * @param placeholder 组件
     * @param slimscroll 组件
     * @param app 程序默认模块
     */
    @Contribute(ModuleManager.class)
	public static void setupComponentsShims(
			MappedConfiguration<String, Object> configuration,
			@Inject @Path("${modules.path}/lib/app-core.js") Resource appcore) {
    	
    	//后台module
		configuration.add("app",new JavaScriptModuleConfiguration(appcore).dependsOn("jquery"));
		
	} 
    
    /**
     * 贡献webSecurityManager,添加realm
     * @param configuration
     * @param authorizingRealm 授权域
     */
    public static void contributeWebSecurityManager(Configuration<Realm> configuration, 
    		@InjectService("FSecurityRealm") AuthorizingRealm authorizingRealm) {
		//注入自定义授权域
    	configuration.add(authorizingRealm);
	}
    /*public static void contributeWebSecurityManager(Configuration<Realm> configuration) {
    	//shiro配置文件，主要用于测试，不建议使用
    	ExtendedPropertiesRealm realm = new ExtendedPropertiesRealm("classpath:shiro-users.properties");
		configuration.add(realm);
		
	}*/
    
    /**
     * 贡献HttpServletRequestFilter,定义过滤器,参数请参考http://jinnianshilongnian.iteye.com/blog/2025656
     * @param configuration
     * @param factory SecurityFilterChainFactory安全过滤链工厂
     * @param securityManager 安全管理
     */
    @Contribute(HttpServletRequestFilter.class)
	@Marker(Security.class)
	public static void setupSecurity(Configuration<SecurityFilterChain> configuration,
			SecurityFilterChainFactory factory, WebSecurityManager securityManager) {

		configuration.add(factory.createChain("/login").add(factory.anon()).build());
		configuration.add(factory.createChain("/*/admin/**").add(factory.authc()).build());
		configuration.add(factory.createChain("/admin/**").add(factory.authc()).build());
		configuration.add(factory.createChain("/services/**").add(factory.anon()).build());
		configuration.add(factory.createChain("/rest/**").add(factory.anon()).build());
		/*configuration.add(factory.createChain("/partlyauthc/**").add(factory.anon()).build());
		configuration.add(factory.createChain("/contributed/**").add(factory.authc()).build());
		configuration.add(factory.createChain("/user/signup").add(factory.anon()).build());
		configuration.add(factory.createChain("/user/**").add(factory.user()).build());
		configuration.add(factory.createChain("/roles/user/**").add(factory.roles(), "user").build());
		configuration.add(factory.createChain("/roles/manager/**").add(factory.roles(), "manager").build());
		configuration.add(factory.createChain("/perms/view/**").add(factory.perms(), "news:view").build());
		configuration.add(factory.createChain("/perms/edit/**").add(factory.perms(), "news:edit").build());

		configuration.add(factory.createChain("/ports/ssl").add(factory.ssl()).build());
		//configuration.add(factory.createChain("/ports/portinuse").add(factory.port(), String.valueOf(TapestrySecurityIntegrationTest.port)).build());
		configuration.add(factory.createChain("/ports/port9090").add(factory.port(), "9090").build());

		configuration.add(factory.createChain("/hidden/**").add(factory.notfound()).build());*/
	}
    
    
    public void contributeApplicationStateManager(MappedConfiguration<Class<?>, ApplicationStateContribution> configuration) {
        ApplicationStateCreator<IState> creator = new ApplicationStateCreator<IState>() {
            @Override
            public IState create() {
                return new MyState();

            }

        };
        configuration.add(IState.class, new ApplicationStateContribution("session", creator));
    }
    
    /**
     * decorateRequestExceptionHandler(覆盖T5异常页面)
     * TODO(重新定义T5异常页面，在产品模式下生效)
     * @Exception MyExceptionReport
     */
     public RequestExceptionHandler decorateRequestExceptionHandler(final Object delegate, final ResponseRenderer renderer,
         final ComponentSource componentSource, @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode, Object service) {

         return new RequestExceptionHandler() {
             public void handleRequestException(final Throwable exception) throws IOException {
                 if (productionMode) {
                     String exceptionPage = "MyExceptionReport";
                     final ExceptionReporter errorPage = (ExceptionReporter) componentSource.getPage(exceptionPage);
                     errorPage.reportException(exception);
                     renderer.renderPageMarkupResponse(exceptionPage);
                 } else {
                     ((RequestExceptionHandler) delegate).handleRequestException(exception);
                 }
             }
         };

     }

    /**
     * 请求过滤
     * @param log
     * @return
     */
    public RequestFilter buildTimingFilter(final Logger log)
    {
        return new RequestFilter()
        {
            public boolean service(Request request, Response response, RequestHandler handler)
                    throws IOException
            {
                long startTime = System.currentTimeMillis();

                try
                {
                    return handler.service(request, response);
                } finally
                {
                    long elapsed = System.currentTimeMillis() - startTime;

                    log.info(String.format("Request time: %d ms", elapsed));
                }
            }
        };
    }

    public void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
                                         @Local
                                         RequestFilter filter)
    {
        configuration.add("Timing", filter);
    }
}
