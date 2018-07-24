package vesselA.conf;

import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.*;
import java.util.Arrays;
import java.util.EnumSet;


//@Configuration
public class HttpSessionInitializer implements WebApplicationInitializer {

    private static final String SERVLET_CONTEXT_PREFIX = "org.springframework.web.servlet.FrameworkServlet.CONTEXT.";
    public static final String DEFAULT_FILTER_NAME = "springSessionRepositoryFilter";
    private final Class<?>[] configurationClasses;

    protected HttpSessionInitializer() {
        this.configurationClasses = null;
    }

    public HttpSessionInitializer(Class... configurationClasses) {
        this.configurationClasses = configurationClasses;
    }

    public void onStartup(ServletContext servletContext) throws ServletException {
        this.beforeSessionRepositoryFilter(servletContext);
//        if (this.configurationClasses != null) {
//            AnnotationConfigWebApplicationContext rootAppContext = new AnnotationConfigWebApplicationContext();
//            rootAppContext.register(this.configurationClasses);
//            servletContext.addListener(new ContextLoaderListener(rootAppContext));
//        }

        this.insertSessionRepositoryFilter(servletContext);
        this.afterSessionRepositoryFilter(servletContext);
    }

    private void insertSessionRepositoryFilter(ServletContext servletContext) {
        String filterName = "springSessionRepositoryFilter";
        DelegatingFilterProxy springSessionRepositoryFilter = new DelegatingFilterProxy(filterName);
        String contextAttribute = this.getWebApplicationContextAttribute();
        if (contextAttribute != null) {
            springSessionRepositoryFilter.setContextAttribute(contextAttribute);
        }

        this.registerFilter(servletContext, true, filterName, springSessionRepositoryFilter);
    }

    protected final void insertFilters(ServletContext servletContext, Filter... filters) {
        this.registerFilters(servletContext, true, filters);
    }

    protected final void appendFilters(ServletContext servletContext, Filter... filters) {
        this.registerFilters(servletContext, false, filters);
    }

    private void registerFilters(ServletContext servletContext, boolean insertBeforeOtherFilters, Filter... filters) {
        Assert.notEmpty(filters, "filters cannot be null or empty");
        Filter[] var4 = filters;
        int var5 = filters.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Filter filter = var4[var6];
            if (filter == null) {
                throw new IllegalArgumentException("filters cannot contain null values. Got " + Arrays.asList(filters));
            }

            String filterName = Conventions.getVariableName(filter);
            this.registerFilter(servletContext, insertBeforeOtherFilters, filterName, filter);
        }

    }

    private void registerFilter(ServletContext servletContext, boolean insertBeforeOtherFilters, String filterName, Filter filter) {
        FilterRegistration.Dynamic registration = servletContext.addFilter(filterName, filter);
        if (registration == null) {
            throw new IllegalStateException("Duplicate Filter registration for '" + filterName + "'. Check to ensure the Filter is only configured once.");
        } else {
            registration.setAsyncSupported(this.isAsyncSessionSupported());
            EnumSet<DispatcherType> dispatcherTypes = this.getSessionDispatcherTypes();
            registration.addMappingForUrlPatterns(dispatcherTypes, !insertBeforeOtherFilters, new String[]{"/*"});
        }
    }

    private String getWebApplicationContextAttribute() {
        String dispatcherServletName = this.getDispatcherWebApplicationContextSuffix();
        return dispatcherServletName == null ? null : "org.springframework.web.servlet.FrameworkServlet.CONTEXT." + dispatcherServletName;
    }

    protected String getDispatcherWebApplicationContextSuffix() {
        return null;
    }

    protected void beforeSessionRepositoryFilter(ServletContext servletContext) {
    }

    protected void afterSessionRepositoryFilter(ServletContext servletContext) {
    }

    protected EnumSet<DispatcherType> getSessionDispatcherTypes() {
        return EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.ASYNC);
    }

    protected boolean isAsyncSessionSupported() {
        return true;
    }
}
